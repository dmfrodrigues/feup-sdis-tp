import java.io.IOException;
import java.net.*;
import java.util.*;

public class Server {

    public static void main(String[] args) throws IOException {
        if(args.length != 1){
            System.out.println("ERROR: not enough arguments");
            System.out.print(getUsage());
            return;
        }

        int         port             = Integer.parseInt(args[0]);

        DatagramSocket socket = new DatagramSocket(port);

        WorkRunnable workRunnable = new WorkRunnable(socket);
        workRunnable.run();
    }

    private static String getUsage(){
        return
            "Usage:\n"+
            "    java Server PORT\n"+
            "    PORT   Port number that the server shall use to provide the service\n"
        ;
    }

    public static class WorkRunnable implements Runnable {
        private final DatagramSocket socket;
        private final Map<String, Inet4Address> table = new HashMap<>();

        public WorkRunnable(DatagramSocket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            while(true){
                RequestMessage message;
                try {
                    message = receiveMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                System.out.println("Server: " + message.toString());
                message.process(this);
            }
        }

        private RequestMessage receiveMessage() throws IOException {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String data = new String(packet.getData()).substring(packet.getOffset(), packet.getLength());

            String[] data_split = data.split(" ");
            String operation = data_split[0];
            RequestMessage request = switch (operation) {
                case "REGISTER" -> new RegisterMessage(packet.getAddress(), packet.getPort(), data_split[1], data_split[2]);
                case "LOOKUP" -> new LookupMessage(packet.getAddress(), packet.getPort(), data_split[1]);
                default -> throw new ProtocolException("Operation " + operation + " not valid");
            };

            return request;
        }

        public void register(String dns, Inet4Address address){
            table.put(dns, address);
        }

        public Inet4Address lookup(String dns) throws NoSuchElementException, NullPointerException {
            if(!table.containsKey(dns)) throw new NoSuchElementException(dns);
            Inet4Address address = table.get(dns);
            if(address == null) throw new NullPointerException(dns);
            else return address;
        }

        public int getTableSize() {
            return table.size();
        }

        public void send(ResponseMessage message) throws IOException {
            DatagramPacket packet = message.toDatagramPacket();
            socket.send(packet);
        }
    }
}

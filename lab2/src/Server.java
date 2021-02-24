import java.io.IOException;
import java.net.*;
import java.util.*;

import static java.lang.Thread.sleep;

public class Server {

    public static void main(String[] args) throws IOException {
        if(args.length != 3){
            System.out.println("ERROR: not enough arguments");
            System.out.print(getUsage());
            return;
        }

        int         port             = Integer.parseInt(args[0]);
        InetAddress multicastAddress = InetAddress.getByName(args[1]);
        int         multicastPort    = Integer.parseInt(args[2]);

        DatagramSocket socket = new DatagramSocket(port, InetAddress.getLocalHost());

        ServiceRunnable serviceRunnable = new ServiceRunnable(socket, multicastAddress, multicastPort);
        Thread serviceThread = new Thread(serviceRunnable, "service");
        serviceThread.start();

        WorkRunnable workRunnable = new WorkRunnable(socket);
        workRunnable.run();
    }

    private static String getUsage(){
        return
            "Usage:\n"+
            "    java Server PORT MCAST_ADDR MCAST_PORT\n"+
            "    PORT        Port number that the server shall use to provide the service\n"+
            "    MCAST_ADDR  Multicast address\n"+
            "    MCAST_PORT  Multicast port\n"
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

    private static class ServiceRunnable implements Runnable {
        /// @brief Time between broadcasts, in milliseconds.
        private final static int timeBetweenBroadcasts = 1000;

        private final InetAddress multicastAddress;
        private final int multicastPort;
        private final DatagramSocket socket;
        private final ServiceMessage serviceMessage;

        public ServiceRunnable(DatagramSocket socket, InetAddress multicastAddress, int multicastPort){
            this.socket = socket;
            this.multicastAddress = multicastAddress;
            this.multicastPort = multicastPort;
            serviceMessage = new ServiceMessage(socket.getLocalAddress(), socket.getLocalPort());
        }

        public void run(){
            while(true){
                try {
                    sleep(timeBetweenBroadcasts);
                } catch (InterruptedException e) {
                }

                try {
                    broadcastSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        private void broadcastSocket() throws IOException {
            DatagramPacket packet = new DatagramPacket(serviceMessage.toString().getBytes(), serviceMessage.length(), multicastAddress, multicastPort);
            System.out.println(
                    "multicast: " +
                    multicastAddress.getHostAddress() + " " + String.valueOf(multicastPort) + ": " +
                    serviceMessage.getAddress().getHostAddress() + " " + String.valueOf(serviceMessage.getPort())
            );
            socket.send(packet);
        }
    }

}

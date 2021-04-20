import java.io.*;
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

        ServerSocket socket = new ServerSocket(port);

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
        private final ServerSocket serverSocket;
        private final Map<String, Inet4Address> table = new HashMap<>();

        public WorkRunnable(ServerSocket serverSocket){
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            while(true){
                try {
                    processMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        private void processMessage() throws IOException {
            Socket socket = serverSocket.accept();

            InputStream is = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String data = bufferedReader.readLine();

            String[] data_split = data.split(" ");
            String operation = data_split[0];
            RequestMessage request = switch (operation) {
                case "REGISTER" -> new RegisterMessage(data_split[1], data_split[2]);
                case "LOOKUP" -> new LookupMessage(data_split[1]);
                default -> throw new ProtocolException("Operation " + operation + " not valid");
            };

            System.out.println("Server: " + request.toString());
            request.process(this, socket);
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

        public void send(ResponseMessage message, Socket socket) throws IOException {
            OutputStream os = socket.getOutputStream();
            os.write((message.toString() + '\n').getBytes());
            os.flush();
        }
    }
}

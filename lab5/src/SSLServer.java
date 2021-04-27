import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.*;
import java.util.*;

public class SSLServer {

    public static void main(String[] args) throws IOException {
        if(args.length < 1){
            System.out.println("ERROR: not enough arguments");
            System.out.print(getUsage());
            return;
        }

        int         port             = Integer.parseInt(args[0]);

        SSLServerSocket socket = (SSLServerSocket) ((SSLServerSocketFactory) SSLServerSocketFactory.getDefault()).createServerSocket(port);
        socket.setNeedClientAuth(true);
        socket.setEnabledCipherSuites(getCypherSuites(args));

        WorkRunnable workRunnable = new WorkRunnable(socket);
        workRunnable.run();
    }

    private static String[] getCypherSuites(String[] args){
        List<String> cyphers = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));
        return cyphers.toArray(new String[0]);
    }

    private static String getUsage(){
        return
            "Usage:\n"+
            "    java SSLServer PORT [CYPHER-SUITE]*\n"+
            "    PORT            Port number that the server shall use to provide the service\n"+
            "    [CYPHER-SUITE]* Sequence of strings specifying the combination of cryptographic algorithms the server should use, in order of preference\n"
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

            System.out.println("SSLServer: " + request.toString());
            request.process(this, socket);
            socket.getInputStream().readAllBytes(); // Consume all data; avoids abortive TCP termination
            socket.close();
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

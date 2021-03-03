import java.io.IOException;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    private static final int TIMEOUT = 3000;
    private static final int MAX_MSG_LEN = 1024;
    private static String host;
    private static String remoteObjName;
    private static String operation;
    private static String dnsName;
    private static String ipAddress;

    public static void main(String[] args) throws IOException {

        if (!parseArgs(args)) {
            System.out.println(getUsage());
            System.exit(1);
        }

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT);

        Registry registry = LocateRegistry.getRegistry(host);


        /*
        if(args[2].equals("register") && args.length == 5)
        {
            RegisterMessage msg = new RegisterMessage(args[3], args[4]);
            sendRequest(socket, host, port, msg);
        }
        else if(args[2].equals("lookup")){
            LookupMessage msg = new LookupMessage(args[3]);
            sendRequest(socket, host, port, msg);
        }
        else{
            System.out.println("Invalid operation");
            System.out.println(getUsage());
            System.exit(1);
        }

        String response = getResponse(socket);
        printResult(args, response);
        socket.close();
        if(response.equals("ERROR")) System.exit(1);
         */
    }

    private static boolean parseArgs(String[] args) throws UnknownHostException {
        if(args.length != 4 && args.length != 5) return false;

        host = args[0];
        remoteObjName = args[1];
        operation = args[2];

        if(operation.equals("register") && args.length == 5){
            dnsName = args[3];
            ipAddress = args[4];
        }
        else if(operation.equals("lookup"))
            dnsName = args[3];
        else{
            System.out.println("Invalid operation");
            return false;
        }

        return true;
    }

    private static void sendRequest(DatagramSocket socket, InetAddress address, int port, Message msg) throws IOException {
        DatagramPacket packet = new DatagramPacket(msg.toString().getBytes(), msg.length(), address, port);
        socket.send(packet);
    }

    private static String getResponse(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[MAX_MSG_LEN];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
        } catch (SocketTimeoutException e) {
            return "ERROR";
        }
        return new String(packet.getData());
    }

    private static void printResult(String[] args, String response){
        if(args[2].equals("register"))
            System.out.println("Client: "+ args[2] + " " + args[3] + " " + args[4] + " : "+ response);
        else if(args[2].equals("lookup"))
            System.out.println("Client: "+ args[2] + " " + args[3] + " : "+ response);
    }

    private static String getUsage(){
        return "Usage: java Client <host_name> <remote_object_name> register <DNS name> <IP address>\n" +
                "       java Client <host_name> <remote_object_name> lookup <DNS name>";
    }
}

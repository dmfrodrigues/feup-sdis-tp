import java.io.IOException;
import java.net.*;

public class Client {
    private static final int TIMEOUT = 3000;
    private static final int MAX_MSG_LEN = 1024;

    private static InetAddress multicastAddress = null;
    private static InetAddress serviceAddress = null;
    private static MulticastSocket multicastSocket = null;
    private static int multicastPort;
    private static int servicePort;

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println(getUsage());
            System.exit(1);
        }

        multicastAddress = InetAddress.getByName(args[0]);
        multicastPort = Integer.parseInt(args[1]);
        multicastSocket = new MulticastSocket(multicastPort);
        multicastSocket.joinGroup(multicastAddress);

        // TODO read service port and address

        // TODO send request

        // TODO  wait response

        /*
        DatagramSocket socket = new DatagramSocket(servicePort);
        socket.setSoTimeout(TIMEOUT);

        if(args[2].equals("register") && args.length == 5)
        {
            RegisterMessage msg = new RegisterMessage(args[3], args[4]);
            sendRequest(socket, serviceAddress, servicePort, msg);
        }
        else if(args[2].equals("lookup")){
            LookupMessage msg = new LookupMessage(args[3]);
            sendRequest(socket, serviceAddress, servicePort, msg);
        }
        else{
            System.out.println("Invalid operation");
            System.out.println(getUsage());
            System.exit(1);
        }

        String response = getResponse(multicastSocket);
        printResult(args, response);
        if(response.equals("ERROR")) System.exit(1);
        */
        multicastSocket.leaveGroup(multicastAddress);
        multicastSocket.close();
    }

    private static void sendRequest(DatagramSocket socket, InetAddress address, int port, Message msg) throws IOException {
        DatagramPacket packet = new DatagramPacket(msg.toString().getBytes(), msg.length(), address, port);
        socket.send(packet);
    }

    private static String getResponse(MulticastSocket socket) throws IOException {
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
        return "Usage: java Client <mcast_addr> <mcast_port> register <DNS name> <IP address>\n" +
                "       java Client <mcast_addr> <mcast_port> lookup <DNS name>";
    }
}

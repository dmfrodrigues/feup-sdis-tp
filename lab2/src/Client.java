import java.io.IOException;
import java.net.*;

public class Client {
    private static final int TIMEOUT               = 3000;
    private static final int MAX_MSG_LEN           = 1024;

    private static InetAddress multicastAddress    = null;
    private static MulticastSocket multicastSocket = null;
    private static ServiceMessage serviceMessage   = null;
    private static int multicastPort;
    private static String operation;
    private static String dnsName;
    private static String ipAddress;

    public static void main(String[] args) throws IOException {
        if (!parseArgs(args)) {
            System.out.println(getUsage());
            System.exit(1);
        }

        multicastSocket = new MulticastSocket(multicastPort);

        if(!joinGroup()) System.exit(1);

        receiveServiceMessage();

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT);

        sendRequest(socket);

        String response = getResponse(socket);
        printStatus(response);

        multicastSocket.leaveGroup(multicastAddress);
        multicastSocket.close();
        socket.close();
        if(response.equals("ERROR")) System.exit(1);
    }

    private static boolean parseArgs(String[] args) throws UnknownHostException {
        if(args.length != 4 && args.length != 5) return false;

        multicastAddress = InetAddress.getByName(args[0]);
        multicastPort = Integer.parseInt(args[1]);
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

    private static boolean joinGroup() throws IOException {
        printStatus("Joining group");
        try {
            multicastSocket.joinGroup(multicastAddress);
        }catch (SocketException e){
            printStatus("ERROR : "+ e.getMessage());
            return false;
        }
        return true;
    }

    private static void receiveServiceMessage() throws IOException {
        byte[] buffer = new byte[MAX_MSG_LEN];
        printStatus("Waiting for service message");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        multicastSocket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        String[] receivedSplit = received.split(" ");
        serviceMessage = new ServiceMessage(InetAddress.getByName(receivedSplit[1]), Integer.parseInt(receivedSplit[2]));
        System.out.println(
                "multicast: " +
                        multicastAddress.getHostAddress() + " " + multicastPort + ": " +
                        serviceMessage.getAddress().getHostAddress() + " " + serviceMessage.getPort()
        );
    }

    private static void sendRequest(DatagramSocket socket) throws IOException {
        RequestMessage msg;
        switch (operation){
            case "register":
                msg = new RegisterMessage(dnsName, ipAddress);
                break;
            case "lookup":
                msg = new LookupMessage(dnsName);
                break;
            default:
                return;
        }
        DatagramPacket packet = new DatagramPacket(
                msg.toString().getBytes(), msg.length(),
                serviceMessage.getAddress(), serviceMessage.getPort());

        socket.send(packet);
        printStatus("Sent request");
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

    private static void printStatus(String status){
        if(operation.equals("register"))
            System.out.println("Client: "+ operation + " " + dnsName + " " + ipAddress + " : " + status);
        else if(operation.equals("lookup"))
            System.out.println("Client: "+ operation + " " + dnsName + " : " + status);
    }

    private static String getUsage(){
        return "Usage: java Client <mcast_addr> <mcast_port> register <DNS name> <IP address>\n" +
                "       java Client <mcast_addr> <mcast_port> lookup <DNS name>";
    }
}

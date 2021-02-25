import java.io.IOException;
import java.net.*;

public class Client {

    private static final int TIMEOUT = 3000;
    private static final int MAX_MSG_LEN = 1024;

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println(getUsage());
            System.exit(1);
        }

        InetAddress host = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT);

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
        return "Usage: java Client <host> <port> register <DNS name> <IP address>\n" +
                "       java Client <host> <port> lookup <DNS name>";
    }
}

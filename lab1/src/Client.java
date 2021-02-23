import java.io.IOException;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("Usage: java Client <host> <port> <oper> <opnd>*");
            System.exit(1);
        }

        InetAddress host = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        DatagramSocket socket = new DatagramSocket();

        if(args[2].equals("register"))
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
            System.exit(1);
        }

        socket.close();
    }

    private static void sendRequest(DatagramSocket socket, InetAddress address, int port, Message msg) throws IOException {
        DatagramPacket packet = new DatagramPacket(msg.toString().getBytes(), msg.length(), address, port);
        socket.send(packet);
    }

    private static void getResponse(){

    }

}

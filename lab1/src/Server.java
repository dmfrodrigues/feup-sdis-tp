import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ProtocolException;
import java.net.SocketException;

public class Server {
    static int port;
    static DatagramSocket socket;

    public static void main(String[] args) throws IOException {
        if(args.length != 1){
            System.out.println("ERROR: not enough arguments");
            System.out.print(getUsage());
            return;
        }

        port = Integer.parseInt(args[0]);
        initialize();
        mainLoop();
    }

    private static String getUsage(){
        return
            "Usage:\n"+
            "    java Server PORT\n"+
            "    PORT   Port number that the server shall use to provide the service\n"
        ;
    }

    private static void initialize() throws SocketException {
        socket = new DatagramSocket(port);
    }

    private static void mainLoop() throws IOException {
        while(true){
            RequestMessage message = receiveMessage();
            message.process();
        }
    }

    private static RequestMessage receiveMessage() throws IOException {
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        String data = new String(packet.getData());
        System.out.println(data);

        String[] data_split = data.split(" ");
        String operation = data_split[0];
        RequestMessage request;
        switch(operation){
            case "REGISTER": request = new RegisterMessage(packet.getAddress(), data_split[1], data_split[2]); break;
            case "LOOKUP": request = new LookupMessage(packet.getAddress(), data_split[1]); break;
            default: throw new ProtocolException("Operation " + operation + " not valid");
        }

        return request;
    }
}

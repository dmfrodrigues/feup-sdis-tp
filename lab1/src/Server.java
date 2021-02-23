import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class Server {
    static int port;
    static DatagramSocket socket;
    static Map<String, Inet4Address> table = new HashMap<>();

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
            System.out.println("Server: " + message.toString());
            message.process();
        }
    }

    private static RequestMessage receiveMessage() throws IOException {
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        String data = new String(packet.getData());

        String[] data_split = data.split(" ");
        String operation = data_split[0];
        RequestMessage request;
        switch(operation){
            case "REGISTER": request = new RegisterMessage(packet.getAddress(), packet.getPort(), data_split[1], data_split[2]); break;
            case "LOOKUP": request = new LookupMessage(packet.getAddress(), packet.getPort(), data_split[1]); break;
            default: throw new ProtocolException("Operation " + operation + " not valid");
        }

        return request;
    }

    public static void register(String dns, Inet4Address address){
        table.put(dns, address);
    }

    public static Inet4Address lookup(String dns) {
        Inet4Address address = table.get(dns);
        if(address == null) throw new NoSuchElementException(dns);
        else return address;
    }

    public static int getTableSize() {
        return table.size();
    }

    public static void send(ResponseMessage message) throws IOException {
        DatagramPacket packet = message.toDatagramPacket();
        socket.send(packet);
    }
}

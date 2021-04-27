import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

public class RegisterMessage extends RequestMessage {
    InetAddress from;
    int fromPort;
    String dns;
    String ip;
    public RegisterMessage(String dns, String ip){
        this.dns = dns;
        this.ip = ip;
    }
    public RegisterMessage(InetAddress from, int fromPort, String dns, String ip){
        this.from = from;
        this.fromPort = fromPort;
        this.dns = dns;
        this.ip = ip;
    }

    public String toString(){
        return "REGISTER " + dns + " " + ip;
    }

    private class Response extends ResponseMessage {
        InetAddress to;
        int toPort;
        int result;
        public Response(InetAddress to, int toPort, int result){
            this.to = to;
            this.toPort = toPort;
            this.result = result;
        }

        public String toString(){
            return String.valueOf(result);
        }

        public DatagramPacket toDatagramPacket(){
            return new DatagramPacket(toString().getBytes(), length(), to, toPort);
        }
    }

    public void process(SSLServer.WorkRunnable workRunnable, Socket socket){
        int result = -1;
        try {
            Inet4Address address = (Inet4Address)Inet4Address.getByName(ip);
            workRunnable.register(dns, address);
            result = workRunnable.getTableSize();
        } catch(Exception ignored){
        } finally {
            ResponseMessage response = new Response(from, fromPort, result);
            try {
                workRunnable.send(response, socket);
            } catch (IOException e) {
                System.err.println("Failed to send response "+response);
            }
        }
    }
}

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.NoSuchElementException;

public class LookupMessage extends RequestMessage {
    InetAddress from;
    int fromPort;
    String dns;
    public LookupMessage(String dns){
        this.dns = dns;
    }
    public LookupMessage(InetAddress from, int fromPort, String dns){
        this.from = from;
        this.fromPort = fromPort;
        this.dns = dns;
    }

    public String toString(){
        return "LOOKUP " + dns;
    }

    private class Response extends ResponseMessage {
        InetAddress to;
        int toPort;
        String dns;
        Inet4Address address;
        public Response(InetAddress to, int toPort, String dns, Inet4Address address){
            this.to = to;
            this.toPort = toPort;
            this.dns = dns;
            this.address = address;
        }

        public String toString(){ return dns + " " + address.toString(); }

        public DatagramPacket toDatagramPacket(){
            return new DatagramPacket(toString().getBytes(), length(), to, toPort);
        }
    }

    public void process(){
        Inet4Address address;
        try {
            address = Server.lookup(dns);
        } catch(NoSuchElementException e) {
            System.err.println("No such entry " + dns + " in table");
            return;
        }
        ResponseMessage response = new LookupMessage.Response(from, fromPort, dns, address);
        try {
            Server.send(response);
        } catch (IOException e) {
            System.err.println("Failed to send response "+response.toString());
        }
    }
}

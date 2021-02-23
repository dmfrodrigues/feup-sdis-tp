import java.net.InetAddress;

public class LookupMessage extends RequestMessage {
    InetAddress from;
    String dns;
    public LookupMessage(String dns){
        this.dns = dns;
    }
    public LookupMessage(InetAddress from, String dns){
        this.from = from;
        this.dns = dns;
    }
    public String toString(){
        return "LOOKUP " + dns;
    }

    public void process(){

    }
}

import java.net.InetAddress;

public class RegisterMessage extends RequestMessage {
    InetAddress from;
    String dns;
    String ip;
    public RegisterMessage(String dns, String ip){
        this.dns = dns;
        this.ip = ip;
    }
    public RegisterMessage(InetAddress from, String dns, String ip){
        this.from = from;
        this.dns = dns;
        this.ip = ip;
    }

    public String toString(){
        return "REGISTER " + dns + " " + ip;
    }

    public void process(){

    }
}

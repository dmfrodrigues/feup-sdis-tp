public class RegisterMessage implements Message {
    String dns;
    String ip;
    public RegisterMessage(String dns, String ip){
        this.dns = dns;
        this.ip = ip;
    }

    public String toString(){
        return "REGISTER " + dns + " " + ip;
    }

    public int length() { return this.toString().length(); }
}

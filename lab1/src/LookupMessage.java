public class LookupMessage implements Message {
    String dns;
    public LookupMessage(String dns){
        this.dns = dns;
    }
    public String toString(){
        return "LOOKUP " + dns;
    }
}

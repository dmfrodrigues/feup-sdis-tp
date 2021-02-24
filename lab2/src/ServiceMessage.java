import java.net.InetAddress;

public class ServiceMessage extends RequestMessage {
    private final InetAddress address;
    private final int port;
    public ServiceMessage(InetAddress address, int port){
        this.address = address;
        this.port = port;
    }

    public String toString(){
        return "SERVICE " + address.getHostAddress() + " " + port;
    }

    @Override
    void process(Server.WorkRunnable workRunnable) {
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}

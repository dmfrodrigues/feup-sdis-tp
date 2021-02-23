import java.net.DatagramPacket;

abstract public class ResponseMessage extends Message {
    abstract public DatagramPacket toDatagramPacket();
}

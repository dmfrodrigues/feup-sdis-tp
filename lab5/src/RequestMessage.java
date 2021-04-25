import java.net.Socket;

abstract class RequestMessage extends Message {
    abstract void process(Server.WorkRunnable workRunnable, Socket socket);
}

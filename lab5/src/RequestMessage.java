import java.net.Socket;

abstract class RequestMessage extends Message {
    abstract void process(SSLServer.WorkRunnable workRunnable, Socket socket);
}

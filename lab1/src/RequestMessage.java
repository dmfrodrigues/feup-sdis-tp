abstract class RequestMessage extends Message {
    abstract void process(Server.WorkRunnable workRunnable);
}

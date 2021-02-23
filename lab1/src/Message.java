public abstract class Message {
    abstract public String toString();
    public int length() { return this.toString().length(); }
}

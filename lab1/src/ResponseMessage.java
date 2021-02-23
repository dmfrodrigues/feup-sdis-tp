public class ResponseMessage implements Message {
    String result;

    public ResponseMessage(String result){
        this.result = result;
    }

    public String toString(){
        return result;
    }

    public int length() { return result.length();}
}

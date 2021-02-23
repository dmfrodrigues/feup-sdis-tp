public class ResponseMessage extends Message {
    String result;

    public ResponseMessage(String result){
        this.result = result;
    }

    public String toString(){
        return result;
    }

    public int length() { return result.length();}
}

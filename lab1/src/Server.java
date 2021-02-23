public class Server {
    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("ERROR: not enough arguments");
            System.out.print(getUsage());
            return;
        }

        int port = Integer.parseInt(args[0]);
    }

    private static String getUsage(){
        return
            "Usage:\n"+
            "    java Server PORT\n"+
            "    PORT   Port number that the server shall use to provide the service\n"
        ;
    }

}

import java.io.IOException;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    private static String host;
    private static String remoteObjName;
    private static String operation;
    private static String dnsName;
    private static Inet4Address ipAddress;

    public static void main(String[] args) throws IOException {
        if (!parseArgs(args)) {
            System.out.println(getUsage());
            System.exit(1);
        }
        String response = "";
        try{
            Registry registry = LocateRegistry.getRegistry(host);
            Server.WorkInterface stub = (Server.WorkInterface) registry.lookup(remoteObjName);
            switch (operation){
                case "register":
                    response = Integer.toString(stub.register(dnsName, ipAddress));
                    break;
                case "lookup":
                    response = stub.lookup(dnsName).getHostAddress();
                    break;
                default:
                    break;
            }
        } catch (Exception e){
            printStatus(e.toString());
            System.exit(1);
        }
        printStatus(response);
    }

    private static boolean parseArgs(String[] args) throws UnknownHostException {
        if(args.length != 4 && args.length != 5) return false;

        host = args[0];
        remoteObjName = args[1];
        operation = args[2];

        if(operation.equals("register") && args.length == 5){
            dnsName = args[3];
            ipAddress = (Inet4Address) InetAddress.getByName(args[4]);

        }
        else if(operation.equals("lookup"))
            dnsName = args[3];
        else{
            System.out.println("Invalid operation");
            return false;
        }
        return true;
    }

    private static void printStatus(String status){
        if(operation.equals("register"))
            System.out.println("Client: "+ operation + " " + dnsName + " " + ipAddress.getHostAddress() + " : " + status);
        else if(operation.equals("lookup"))
            System.out.println("Client: "+ operation + " " + dnsName + " : " + status);
    }

    private static String getUsage(){
        return "Usage: java Client <host_name> <remote_object_name> register <DNS name> <IP address>\n" +
                "       java Client <host_name> <remote_object_name> lookup <DNS name>";
    }
}

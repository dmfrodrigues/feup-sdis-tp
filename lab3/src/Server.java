import java.io.IOException;
import java.net.*;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Server {

    public static void main(String[] args) throws IOException, AlreadyBoundException {
        if(args.length != 1){
            System.out.println("ERROR: not enough arguments");
            System.out.print(getUsage());
            return;
        }

        String remote_obj_name = args[0];
        WorkImplementation.createRemoteObj(remote_obj_name);
    }

    private static String getUsage(){
        return
            "Usage:\n"+
            "    java Server REMOTE_OBJ_NAME\n"+
            "    REMOTE_OBJ_NAME    Remote object name\n"
        ;
    }

    public interface WorkInterface extends Remote {
        int register(String dns, Inet4Address address) throws RemoteException;

        Inet4Address lookup(String dns) throws NoSuchElementException, NullPointerException, RemoteException;
    }

    public static class WorkImplementation implements WorkInterface {
        public static void createRemoteObj(String remote_obj_name) throws RemoteException, AlreadyBoundException {
            WorkImplementation obj = new WorkImplementation();
            WorkInterface stub = (WorkInterface) UnicastRemoteObject.exportObject(obj, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.bind(remote_obj_name, stub);
        }

        private final Map<String, Inet4Address> table = new HashMap<>();

        public WorkImplementation() {
        }

        public int register(String dns, Inet4Address address){
            this.log("REGISTER " + dns + " " + address.getHostAddress());
            table.put(dns, address);
            return getTableSize();
        }

        public Inet4Address lookup(String dns) throws NoSuchElementException, NullPointerException {
            this.log("LOOKUP " + dns);
            if(!table.containsKey(dns)) throw new NoSuchElementException(dns);
            Inet4Address address = table.get(dns);
            if(address == null) throw new NullPointerException(dns);
            else return address;
        }

        public int getTableSize() {
            return table.size();
        }

        private void log(String message){
            System.out.println("Server: " + message);
        }
    }
}

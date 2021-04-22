import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SSLClient {

    private static final int TIMEOUT = 3000;

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println(getUsage());
            System.exit(1);
        }

        InetAddress host = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);

        SSLSocket socket;
        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            socket = (SSLSocket) ssf.createSocket(host, port);
            socket.setEnabledCipherSuites(getCypherSuites(args));
            socket.setSoTimeout(TIMEOUT);
        }
        catch( IOException e) {
            System.err.println("Failed to create SSLSocket: " + e.getMessage());
            return;
        }

        socket.startHandshake();

        if(args[2].equals("register") && args.length == 5)
        {
            RegisterMessage msg = new RegisterMessage(args[3], args[4]);
            sendRequest(socket, msg);
        }
        else if(args[2].equals("lookup")){
            LookupMessage msg = new LookupMessage(args[3]);
            sendRequest(socket, msg);
        }
        else{
            System.out.println("Invalid operation");
            System.out.println(getUsage());
            System.exit(1);
        }

        String response = getResponse(socket);
        printResult(args, response);
        socket.close();
        if(response.equals("ERROR")) System.exit(1);
    }

    private static String[] getCypherSuites(String[] args){
        List<String> cyphers = new ArrayList<>();
        if(args[2].equals("register")){
            cyphers.addAll(Arrays.asList(Arrays.copyOfRange(args, 5, args.length)));
        }
        else if(args[2].equals("lookup")){
            cyphers.addAll(Arrays.asList(Arrays.copyOfRange(args, 4, args.length)));
        }
        System.out.println(cyphers);
        return cyphers.toArray(new String[0]);
    }

    private static void sendRequest(SSLSocket socket, Message msg) throws IOException {
        OutputStream os = socket.getOutputStream();
        os.write((msg.toString() + '\n').getBytes());
        os.flush();
    }

    private static String getResponse(SSLSocket socket) throws IOException {
        try {
            InputStream is = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            return bufferedReader.readLine();
        } catch (SocketTimeoutException e) {
            return "ERROR";
        }
    }

    private static void printResult(String[] args, String response){
        if(args[2].equals("register"))
            System.out.println("SSLClient: "+ args[2] + " " + args[3] + " " + args[4] + " : "+ response);
        else if(args[2].equals("lookup"))
            System.out.println("SSLClient: "+ args[2] + " " + args[3] + " : "+ response);
    }

    private static String getUsage(){
        return "Usage: java SSLClient <host> <port> register <DNS name> <IP address> [CYPHER-SUITE]*\n" +
                "      java SSLClient <host> <port> lookup <DNS name> [CYPHER-SUITE]*"+
                "      [CYPHER-SUITE]* Sequence of strings specifying the combination of cryptographic algorithms the server should use, in order of preference\n";
    }
}

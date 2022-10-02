import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        String filePath = args[0];
        InetSocketAddress serverAddress = new InetSocketAddress(args[1], Integer.parseInt(args[2]));

        Client client = new Client(filePath, serverAddress);
        client.startSession();
        client.cleanup();
        System.out.println("Hello from client");
    }
}

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final File fileToTransfer;
    private Socket clientSocket;

    public Client(String filePath, InetSocketAddress serverSocketAddress) {
        this.fileToTransfer = new File(filePath);
        try {
            this.clientSocket = new Socket(serverSocketAddress.getAddress(), serverSocketAddress.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile() {
        Message message = new Message(fileToTransfer.getPath(), (int) fileToTransfer.length());
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
            System.out.println("client sends " + message.getText());
            writer.write(message.getText());
            writer.flush();
            //writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveStatus() {
        try {
            Scanner scanner = new Scanner(clientSocket.getInputStream());
            System.out.println(scanner.nextLine());
            //reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

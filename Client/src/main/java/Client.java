import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client {
    private final static int MAX_NAME_LENGTH = 4096;
    private final static int MAX_SIZE_LENGTH = 13;

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

    public void startSession() {
        Thread thread = new Thread(this::sendFile);
        thread.start();
        receiveSpeed();
        receiveStatus();
    }

    private void sendFile() {
        sendMetaData();
        sendContent();
    }

    private void sendMetaData() {
        HeadMessage headMessage = new HeadMessage(fileToTransfer.getPath(), (int) fileToTransfer.length());
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(headMessage);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendContent() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            DataInputStream fileStream = new DataInputStream(new FileInputStream(fileToTransfer));
            byte[] buf = new byte[4096];
            int count;

            while ((count = fileStream.read(buf)) > 0) {
                System.out.println("write " + count + " bytes");
                outputStream.writeObject(new Message(Arrays.copyOfRange(buf, 0, count)));
            }
            outputStream.writeObject(new Message(new byte[0]));

            outputStream.flush();
        } catch (IOException e) {
                throw new RuntimeException(e);
        }
    }

    public void cleanup() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void receiveSpeed() {
        try {
            SpeedMessage speedMessage;
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

            while((speedMessage = (SpeedMessage) inputStream.readObject()).getSpeedType().equals("instant")) {
                System.out.println("instant speed = " + speedMessage.getSpeedValue() + " bytes / millisecond");
            }

            System.out.println("session (average) speed = " + speedMessage.getSpeedValue() + " bytes / millisecond");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    public void receiveStatus() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            byte[] status = new byte[8];
            inputStream.read(status);
            System.out.println(new String(status, StandardCharsets.UTF_8));
            //reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

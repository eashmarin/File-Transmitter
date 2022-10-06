package main.java;

import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client {
    private final File fileToTransfer;

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public Client(String filePath, InetSocketAddress serverSocketAddress) throws FileNotFoundException {
        this.fileToTransfer = new File(filePath);
        if (!fileToTransfer.exists()) {
            throw new FileNotFoundException();
        }
        try {
            this.socket = new Socket(serverSocketAddress.getAddress(), serverSocketAddress.getPort());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
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
            outputStream.writeObject(headMessage);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendContent() {
        try {
            DataInputStream fileStream = new DataInputStream(new FileInputStream(fileToTransfer));
            byte[] buf = new byte[4096];
            int count;

            while ((count = fileStream.read(buf)) > 0) {
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
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void receiveSpeed() {
        try {
            SpeedMessage speedMessage;

            while((speedMessage = (SpeedMessage) inputStream.readObject()).getSpeedType().equals("instant")) {
                LogManager.getRootLogger().info("instant speed = " + (int) speedMessage.getSpeedValue() + " bytes / second");
            }

            LogManager.getRootLogger().info("Session speed = " + speedMessage.getSpeedValue() + " bytes / second");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    public void receiveStatus() {
        try {
            byte[] status = new byte[8];
            inputStream.read(status);
            System.out.println(new String(status, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

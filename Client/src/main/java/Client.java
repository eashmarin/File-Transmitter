package main.java;

import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client {
    private final File fileToTransfer;

    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public Client(String filePath, InetSocketAddress serverSocketAddress) throws IOException {
        this.fileToTransfer = new File(filePath);
        if (!fileToTransfer.exists()) {
            throw new FileNotFoundException();
        }

        this.socket = new Socket(serverSocketAddress.getAddress(), serverSocketAddress.getPort());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    public void startSession() {
        Thread thread = new Thread(this::sendFile);
        thread.start();

        try {
            receiveSpeed();
            receiveStatus();
        } catch(IOException | ClassNotFoundException e) {
            LogManager.getLogger().error(e.getMessage());
        }
    }

    private void sendFile() {
        try {
            sendMetaData();
            sendContent();
        } catch (IOException e) {
            LogManager.getLogger().error(e.getMessage());
        }
    }

    private void sendMetaData() throws IOException {
        HeadMessage headMessage = new HeadMessage(fileToTransfer.getPath(), (int) fileToTransfer.length());

        outputStream.writeObject(headMessage);
        outputStream.flush();
    }

    private void sendContent() throws IOException {
        DataInputStream fileStream = new DataInputStream(new FileInputStream(fileToTransfer));
        byte[] buf = new byte[4096];
        int count;

        while ((count = fileStream.read(buf)) > 0) {
            outputStream.writeObject(new ContentMessage(Arrays.copyOfRange(buf, 0, count)));
        }
        outputStream.writeObject(new ContentMessage(new byte[0]));  //message with zero length = last message
        outputStream.flush();
        fileStream.close();
    }

    public void cleanup() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void receiveSpeed() throws IOException, ClassNotFoundException {
        SpeedMessage speedMessage;

        while((speedMessage = (SpeedMessage) inputStream.readObject()).getSpeedType().equals("instant")) {
            LogManager.getLogger().info("Instant speed = " + (int) speedMessage.getSpeedValue() + " bytes / second");
        }

        LogManager.getLogger().info("Session speed = " + speedMessage.getSpeedValue() + " bytes / second");
    }

    public void receiveStatus() throws IOException {
        byte[] status = new byte[9];
        inputStream.read(status);
        LogManager.getLogger().info("Status = " + new String(status, StandardCharsets.UTF_8));
    }
}
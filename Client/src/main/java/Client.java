package main.java;

import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        ScheduledExecutorService speedReceiverThread = Executors.newScheduledThreadPool(1);
        speedReceiverThread.scheduleAtFixedRate(this::receiveSpeed, 3, 3, TimeUnit.SECONDS);

        sendFile();

        speedReceiverThread.shutdown();
        receiveSpeed();

        receiveStatus();
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

    private void receiveSpeed() {
        SpeedMessage speedMessage;
        try {
            speedMessage = (SpeedMessage) inputStream.readObject();
            LogManager.getLogger().info("Instant speed = " + (int) speedMessage.getInstantSpeed() + " bytes / second");
            LogManager.getLogger().info("Session speed = " + (int) speedMessage.getSessionSpeed() + " bytes / second");
        } catch (IOException | ClassNotFoundException e) {
            LogManager.getLogger().error(e.getLocalizedMessage());
        }
    }

    public void receiveStatus() {
        try {
            StatusMessage statusMessage = (StatusMessage) inputStream.readObject();

            LogManager.getLogger().info("Status = " + statusMessage.getStatus());
        } catch (IOException | ClassNotFoundException e) {
            LogManager.getLogger().error(e.getLocalizedMessage());
        }

    }
}
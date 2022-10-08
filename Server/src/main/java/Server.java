package main.java;

import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LogManager.getLogger().error("Can't create server socket");
        }
    }

    public String getServerAddress() {
        String address = "";
        try {
            address = InetAddress.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort();
        } catch (UnknownHostException e) {
            LogManager.getLogger().error(e.getMessage());
        }
        return address;
    }

    public void listenClients() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(() -> downloadFile(clientSocket));
                LogManager.getLogger().info("New client has connected - " + clientSocket.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            LogManager.getLogger().error(e.getMessage());
        }
    }

    private void downloadFile(Socket clientSocket) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            FileDownloader fileDownloader = new FileDownloader(clientSocket);

            ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
            scheduledThreadPool.scheduleAtFixedRate(() -> measureSpeed(fileDownloader, outputStream), 3, 3, TimeUnit.SECONDS);

            fileDownloader.download();

            scheduledThreadPool.shutdown();

            measureSpeed(fileDownloader, outputStream);

            sendDownloadStatus(outputStream, fileDownloader.isDownloadCompletedProperly());
        } catch (IOException e) {
            LogManager.getLogger().error(e.getMessage());
        }
    }

    private void measureSpeed(FileDownloader downloader, ObjectOutputStream outputStream) {
        try {
            outputStream.writeObject(new SpeedMessage("instant", downloader.getInstantSpeed()));
            downloader.resetInstantSpeed();

            if (downloader.isComplete()) {
                outputStream.writeObject(new SpeedMessage("session", downloader.getSessionSpeed()));
            }
        } catch (IOException e) {
            LogManager.getLogger().error(e.getMessage());
        }
    }

    private void sendDownloadStatus(ObjectOutputStream outputStream, boolean isFileDownloadedProperly) throws IOException {
        String statusMessage = isFileDownloadedProperly ? "SUCCEEDED" : "FAILED";
        outputStream.write(statusMessage.getBytes());
        outputStream.flush();
    }
}
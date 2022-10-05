import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
            e.printStackTrace();
        }
    }

    public void listenClients() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(() -> downloadFile(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(Socket clientSocket) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            FileDownloader fileDownloader = new FileDownloader(clientSocket);

            ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
            scheduledThreadPool.scheduleAtFixedRate(() -> measureSpeed(fileDownloader, outputStream), 1, 1, TimeUnit.SECONDS);

            fileDownloader.download();

            scheduledThreadPool.shutdown();

            measureSpeed(fileDownloader, outputStream);

            sendDownloadStatus(outputStream, fileDownloader.isDownloadCompletedProperly());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void measureSpeed(FileDownloader downloader, ObjectOutputStream outputStream) {
        try {
            if (!downloader.isComplete()) {
                outputStream.writeObject(new SpeedMessage("instant", downloader.getInstantSpeed()));
                downloader.resetInstantSpeed();
            }
            else {
                outputStream.writeObject(new SpeedMessage("session", downloader.getSessionSpeed()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDownloadStatus(ObjectOutputStream outputStream, boolean isFileComplete) {
        String statusMessage = isFileComplete ? "OK" : "FAULT";
        try {
            outputStream.write(statusMessage.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
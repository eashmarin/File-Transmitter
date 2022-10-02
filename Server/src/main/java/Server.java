import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class Server {
    private final static int MAX_NAME_LENGTH = 4096;
    private final static int MAX_SIZE_LENGTH = 13;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenClients() {
        try {
            //while (!serverSocket.isClosed()) {
            clientSocket = serverSocket.accept();
            downloadFile();
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadFile() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

            try {
                HeadMessage message = (HeadMessage) inputStream.readObject();
                File fileToSave = createFile(message.getFileName());

                downloadFileContent(clientSocket, fileToSave);

                sendDownloadStatus(clientSocket.getOutputStream(), message.getSize() == fileToSave.length());

                System.out.println("fileName = " + message.getFileName() + "; fileSize=" + message.getSize() + ";");

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createFile(String fileName) {
        File uploadsDirectory = new File("uploads");
        if (!uploadsDirectory.exists()) {
            uploadsDirectory.mkdir();
        }

        String filePath = uploadsDirectory.getName() + "/" +  getUnusedFileName(uploadsDirectory, fileName);

        File newFile = new File(filePath);

        try {
            newFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return newFile;
    }

    private String getUnusedFileName(File fileDirectory, String fileName) {
        String nameWithoutExtension = fileName.split("[.]")[0];
        String extension = fileName.split("[.]")[1];
        String newName = fileName;
        int counter = 0;

        while (Files.exists(Path.of(fileDirectory.getPath(), newName))) {
            counter++;
            newName = nameWithoutExtension + "(" + counter + ")" + "." + extension;
        }

        return newName;
    }

    private void downloadFileContent(Socket clientSocket, File file) { //TODO: move part of function in sendSpeed(Socket)
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            long startTime = System.currentTimeMillis();
            long iterationTime = System.currentTimeMillis();
            int bytesReadInMoment;
            int bytesReadTotally = 0;
            Message message;

            while ((message = (Message) inputStream.readObject()).getContent().length > 0) {
                bytesReadInMoment = message.getContent().length;
                bytesReadTotally += bytesReadInMoment;

                fileOutputStream.write(message.getContent());

                if (System.currentTimeMillis() - iterationTime >= 3000) {
                    outputStream.writeObject(new SpeedMessage("instant", (double) bytesReadInMoment / 3000.0));
                    iterationTime = System.currentTimeMillis();
                }
            }
            System.out.println("bytesReadTotally = " + bytesReadTotally);
            double sessionSpeed = (double) bytesReadTotally / (System.currentTimeMillis() - startTime);
            outputStream.writeObject(new SpeedMessage("session", sessionSpeed));
            fileOutputStream.flush();
            //out.close();
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendDownloadStatus(OutputStream outputStream, boolean isFileComplete) {
        String statusMessage = isFileComplete ? "OK" : "FAULT";
        try {
            outputStream.write(statusMessage.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Server {
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
            Scanner scanner = new Scanner(clientSocket.getInputStream());

            String fileName = scanner.nextLine();
            int fileSize = Integer.parseInt(scanner.nextLine());

            File fileToSave = createFile(fileName);
            fillFile(clientSocket.getInputStream(), fileToSave, fileSize);

            sendDownloadStatus(fileSize == fileToSave.length());

            //scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createFile(String fileName) {
        File uploadsDirectory = new File("uploads");
        if (!uploadsDirectory.exists()) {
            uploadsDirectory.mkdir();
        }

        File newFile = new File(uploadsDirectory.getName() + "/" + fileName);

        return newFile;
    }

    private void fillFile(InputStream inputStream, File file, int fileSize) {
        String content = getFileContent(inputStream, file, fileSize);
        writeContentInFile(content, file);
    }

    private String getFileContent(InputStream inputStream, File file, int fileSize) {
        StringBuilder content = new StringBuilder();

        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            content.append(scanner.nextLine());
        }
        //scanner.close();

        return content.toString();
    }

    private void writeContentInFile(String content, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDownloadStatus(boolean isFileComplete) {
        String statusMessage = isFileComplete ? "OK" : "FAULT";
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
            writer.flush();
            writer.write(statusMessage);
            writer.flush();
            //writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
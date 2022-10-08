package main.java;

import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDownloader {
    private int bytesReadTotal = 0;
    private int bytesReadInPeriod = 0;

    private HeadMessage headMessage;

    private File fileToSave;

    private ObjectInputStream inputStream;
    private long startTime = 0;

    public FileDownloader(Socket socket) {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download() {
        startTime = System.currentTimeMillis();

        try {
            downloadMetaData();
            fileToSave = createFile(headMessage.getFileName());
            downloadContent();
        } catch (IOException | ClassNotFoundException e) {
            LogManager.getLogger().error(e.getMessage());
        }

        LogManager.getLogger().info(String.format("time taken = %f sec", (System.currentTimeMillis() - startTime) / 1000.0));
    }

    private void downloadMetaData() throws IOException, ClassNotFoundException {
        headMessage = (HeadMessage) inputStream.readObject();
    }

    private File createFile(String fileName) throws IOException {
        File uploadsDirectory = new File("uploads");
        if (!uploadsDirectory.exists()) {
            uploadsDirectory.mkdir();
        }

        String filePath = uploadsDirectory.getName() + "/" +  generateUnusedFileName(uploadsDirectory, fileName);

        File newFile = new File(filePath);

        newFile.createNewFile();

        return newFile;
    }

    private String generateUnusedFileName(File fileDirectory, String fileName) {
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

    private void downloadContent() throws IOException, ClassNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileToSave);
        ContentMessage message;

        while ((message = (ContentMessage) inputStream.readObject()).getContent().length > 0) {
            bytesReadInPeriod += message.getContent().length;
            bytesReadTotal += message.getContent().length;

            fileOutputStream.write(message.getContent());
        }
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public void resetInstantSpeed() {
        bytesReadInPeriod = 0;
    }

    public int getInstantSpeed() {
        return bytesReadInPeriod;
    }

    public double getSessionSpeed() {
        return (bytesReadTotal / ((double) System.currentTimeMillis() - startTime)) * 1000;
    }

    public boolean isDownloadCompletedProperly() {
        return fileToSave.length() == headMessage.getFileSize();
    }
}

package main.java;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDownloader {
    private int bytesReadTotal = 0;
    private int bytesReadInSecond = 0;

    private HeadMessage headMessage;

    private File fileToSave;

    private ObjectInputStream inputStream;
    private long timeTaken = 0;

    public FileDownloader(Socket socket) {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download() {
        long startTime = System.currentTimeMillis();

        downloadMetaData();
        fileToSave = createFile(headMessage.getFileName());
        downloadContent();

        timeTaken = System.currentTimeMillis() - startTime;
        System.out.println("time taken = " + timeTaken);
    }

    private void downloadMetaData() {
        try {
            headMessage = (HeadMessage) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
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

    private void downloadContent() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileToSave);
            Message message;

            while ((message = (Message) inputStream.readObject()).getContent().length > 0) {
                bytesReadInSecond += message.getContent().length;
                bytesReadTotal += message.getContent().length;

                fileOutputStream.write(message.getContent());
            }
            System.out.println("bytesReadTotal = " + bytesReadTotal);
            fileOutputStream.flush();
            //out.close();
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isComplete() {
        return timeTaken > 0;
    }

    public void resetInstantSpeed() {
        bytesReadInSecond = 0;
    }

    public int getInstantSpeed() {
        return bytesReadInSecond;
    }

    public double getSessionSpeed() {
        return (bytesReadTotal / (double) timeTaken) * 1000;
    }

    public boolean isDownloadCompletedProperly() {
        return fileToSave.length() == headMessage.getFileSize();
    }
}

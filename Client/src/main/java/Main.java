package main.java;

import org.apache.logging.log4j.LogManager;

import java.io.FileNotFoundException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        String filePath = args[0];
        InetSocketAddress serverAddress = new InetSocketAddress(args[1], Integer.parseInt(args[2]));

        try {
            Client client = new Client(filePath, serverAddress);
            client.startSession();
            client.cleanup();
        } catch (FileNotFoundException e) {
            System.out.println();
            LogManager.getRootLogger().error("Wrong path to file: '" + filePath + "'");
        }
    }
}

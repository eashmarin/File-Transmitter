package main.java;

import org.apache.logging.log4j.LogManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        if (args.length == 3) {
            String filePath = args[0];
            InetSocketAddress serverAddress = new InetSocketAddress(args[1], Integer.parseInt(args[2]));

            try {
                Client client = new Client(filePath, serverAddress);
                client.startSession();
                client.cleanup();
            } catch (FileNotFoundException e) {
                LogManager.getLogger().error("Wrong path to file: '" + filePath + "'");
            } catch (IOException e) {
                LogManager.getLogger().error("Can't connect to server's socket");
            }
        } else {
            LogManager.getLogger().error("Invalid arguments: need \"<file_path> <server_address> <server_port>\"");
        }
    }
}

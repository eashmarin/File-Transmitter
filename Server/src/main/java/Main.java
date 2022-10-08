package main.java;

import org.apache.logging.log4j.LogManager;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1) {
            Server server = new Server(Integer.parseInt(args[0]));
            LogManager.getLogger().info("Server is listening on address " + server.getServerAddress());
            server.listenClients();
        } else {
            LogManager.getLogger().error("Invalid arguments: need \"<port>\"");
        }
    }
}

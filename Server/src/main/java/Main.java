package main.java;

import org.apache.logging.log4j.LogManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(Integer.parseInt(args[0]));
        try {
            System.out.println(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("we");

        LogManager.getLogger().info("info");
        LogManager.getLogger().debug("debbbug");
        LogManager.getLogger().warn("warn");
        LogManager.getLogger().error("errrrrrror");
        LogManager.getRootLogger().info("Welcome");
        //LogManager.getRootLogger().debug("welcome");
        server.listenClients();
    }
}

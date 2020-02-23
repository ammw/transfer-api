package eu.ammw.transfer;

import eu.ammw.transfer.rest.ServerConfiguration;

public class Main {
    private final static int PORT = 1234;

    public static void main(String[] args) {
        ServerConfiguration.configureAndStart(PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(ServerConfiguration::stop));
    }
}

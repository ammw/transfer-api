package eu.ammw.transfer;

import eu.ammw.transfer.rest.AccountService;
import eu.ammw.transfer.rest.ServerConfiguration;
import eu.ammw.transfer.rest.TransferService;

public class Main {
    private final static int PORT = 1234;

    public static void main(String[] args) {
        configureServices().configureAndStart(PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(ServerConfiguration::stop));
    }

    private static ServerConfiguration configureServices() {
        AccountService accountService = new AccountService();
        TransferService transferService = new TransferService(accountService);
        return new ServerConfiguration(accountService, transferService);
    }
}

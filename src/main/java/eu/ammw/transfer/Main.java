package eu.ammw.transfer;

import eu.ammw.transfer.domain.AccountService;
import eu.ammw.transfer.domain.TransferService;
import eu.ammw.transfer.rest.AccountController;
import eu.ammw.transfer.rest.ServerConfiguration;
import eu.ammw.transfer.rest.TransferController;

public class Main {
    private final static int PORT = 1234;

    public static void main(String[] args) {
        configureServices().configureAndStart(PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(ServerConfiguration::stop));
    }

    private static ServerConfiguration configureServices() {
        AccountService accountService = new AccountService();
        TransferService transferService = new TransferService(accountService);
        AccountController accountController = new AccountController(accountService);
        TransferController transferController = new TransferController(transferService);
        return new ServerConfiguration(accountController, transferController);
    }
}

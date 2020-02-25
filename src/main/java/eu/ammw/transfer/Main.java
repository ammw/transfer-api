package eu.ammw.transfer;

import eu.ammw.transfer.db.InMemoryDatabase;
import eu.ammw.transfer.db.InMemoryDatabaseService;
import eu.ammw.transfer.domain.AccountService;
import eu.ammw.transfer.domain.TransferService;
import eu.ammw.transfer.rest.AccountController;
import eu.ammw.transfer.rest.ServerConfiguration;
import eu.ammw.transfer.rest.TransferController;

public class Main {
    private final static int DEFAULT_PORT = 1234;

    public static void main(String[] args) {
        initDatabase();
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        configureServices().configureAndStart(port);
        Runtime.getRuntime().addShutdownHook(new Thread(ServerConfiguration::stop));
    }

    private static void initDatabase() {
        InMemoryDatabase.create();
    }

    private static ServerConfiguration configureServices() {
        AccountService accountService = new AccountService(new InMemoryDatabaseService());
        TransferService transferService = new TransferService(new InMemoryDatabaseService(), accountService);
        AccountController accountController = new AccountController(accountService);
        TransferController transferController = new TransferController(transferService);
        return new ServerConfiguration(accountController, transferController);
    }
}

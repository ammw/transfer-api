package eu.ammw.transfer;

import eu.ammw.transfer.db.DataSource;
import eu.ammw.transfer.db.InMemoryDatabase;
import eu.ammw.transfer.db.InMemoryDatabaseService;
import eu.ammw.transfer.domain.AccountService;
import eu.ammw.transfer.domain.TransferService;
import eu.ammw.transfer.rest.AccountController;
import eu.ammw.transfer.rest.ServerConfiguration;
import eu.ammw.transfer.rest.TransferController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class Main {
    private final static int DEFAULT_PORT = 1234;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            initDatabase();
            int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
            configureServices().configureAndStart(port);
            Runtime.getRuntime().addShutdownHook(new Thread(ServerConfiguration::stop));
        } catch (Exception e) {
            LOGGER.error("Error while starting application", e);
            System.exit(1);
        }
    }

    private static void initDatabase() {
        InMemoryDatabase.create();
    }

    private static ServerConfiguration configureServices() throws SQLException {
        DataSource dataSource = new InMemoryDatabaseService(InMemoryDatabase.getConnection());
        AccountService accountService = new AccountService(dataSource);
        TransferService transferService = new TransferService(dataSource, accountService);
        AccountController accountController = new AccountController(accountService);
        TransferController transferController = new TransferController(transferService);
        return new ServerConfiguration(accountController, transferController);
    }
}

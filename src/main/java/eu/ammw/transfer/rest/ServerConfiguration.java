package eu.ammw.transfer.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.UUID;
import java.util.function.Supplier;

import static spark.Spark.*;

public class ServerConfiguration {
    private static final String JSON_TYPE = "application/json";
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfiguration.class);

    private final AccountService accountService;

    public ServerConfiguration(AccountService accountService) {
        this.accountService = accountService;
    }

    public static void stop() {
        LOGGER.info("Stopping server");
        Spark.stop();
    }

    public void configureAndStart(int port) {
        port(port);
        defaultResponseTransformer(new JsonTransformer());

        get("/health", (rq, rs) -> "OK");

        get("/accounts", (rq, rs) -> respondWithJson(rq, rs, accountService::getAccounts));
        post("/accounts", (rq, rs) -> respondWithJson(rq, rs, () -> accountService.createAccount()));
        get("/accounts/:id", (rq, rs) -> respondWithJson(rq, rs, () -> accountService.getAccount(UUID.fromString(rq.params("id")))));

        LOGGER.info("Server startup finished on port {}", port);
    }

    private Object respondWithJson(Request request, Response response, Supplier<?> function) {
        response.type(JSON_TYPE);
        return function.get();
    }
}

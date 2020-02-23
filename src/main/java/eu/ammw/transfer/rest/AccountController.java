package eu.ammw.transfer.rest;

import eu.ammw.transfer.domain.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.post;

public class AccountController implements Controller {

    private static final String JSON_TYPE = "application/json";
    private static final String TEXT_TYPE = "text/plain";

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void registerEndpoints() {
        get("/accounts", this::getAccounts);
        post("/accounts", this::createAccount);
        get("/accounts/:id", this::getAccount);

    }

    Object getAccounts(Request request, Response response) {
        response.type(JSON_TYPE);
        return accountService.getAccounts();
    }

    Object createAccount(Request request, Response response) {
        response.type(JSON_TYPE);
        return accountService.createAccount();
    }

    Object getAccount(Request request, Response response) {
        try {
            UUID id = UUID.fromString(request.params("id"));
            response.type(JSON_TYPE);
            return accountService.getAccount(id);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Could not retrieve account", e);
            response.type(TEXT_TYPE);
            response.status(400);
            return "Invalid account ID!";
        }
    }
}

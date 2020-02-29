package eu.ammw.transfer.rest;

import com.google.gson.Gson;
import eu.ammw.transfer.domain.AccountService;
import eu.ammw.transfer.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.UUID;

import static eu.ammw.transfer.rest.ErrorHandler.handleError;
import static spark.Spark.get;
import static spark.Spark.post;

public class AccountController implements Controller {

    private static final String JSON_TYPE = "application/json";
    private static final String TEXT_TYPE = "text/plain";

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;
    private final Gson gson = new Gson();

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
        try {
            Account account = gson.fromJson(request.body(), Account.class);
            response.type(JSON_TYPE);
            return accountService.createAccount(account.getName());
        } catch (Exception e) {
            return handleError(response, e);
        }
    }

    Object getAccount(Request request, Response response) {
        try {
            UUID id = UUID.fromString(request.params("id"));
            Account account = accountService.getAccount(id);
            response.type(JSON_TYPE);
            return account;
        } catch (Exception e) {
            return handleError(response, e);
        }
    }
}

package eu.ammw.transfer.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import eu.ammw.transfer.domain.TransferService;
import eu.ammw.transfer.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.UUID;

import static eu.ammw.transfer.rest.ErrorHandler.handleError;
import static spark.Spark.get;
import static spark.Spark.post;

public class TransferController implements Controller {

    private static final String JSON_TYPE = "application/json";

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferController.class);

    private final TransferService transferService;
    private final Gson gson = new Gson();

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @Override
    public void registerEndpoints() {
        post("/accounts/:id/transfer", this::transfer);
        post("/accounts/:id/deposit", this::deposit);
        post("/accounts/:id/withdraw", this::withdraw);
        get("/accounts/:id/history", this::getHistory);
    }

    Object transfer(Request request, Response response) {
        try {
            Transfer transfer = gson.fromJson(request.body(), Transfer.class);
            UUID id = UUID.fromString(request.params("id"));
            response.type(JSON_TYPE);
            return transferService.transfer(id, transfer.getTo(), transfer.getAmount());
        } catch (Exception e) {
            return handleError(response, e);
        }
    }

    Object deposit(Request request, Response response) {
        try {
            JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);
            BigDecimal amount = jsonObject.get("amount").getAsBigDecimal();
            UUID id = UUID.fromString(request.params("id"));

            response.type(JSON_TYPE);
            transferService.deposit(id, amount);
            return null;
        } catch (Exception e) {
            return handleError(response, e);
        }
    }

    Object withdraw(Request request, Response response) {
        try {
            JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);
            BigDecimal amount = jsonObject.get("amount").getAsBigDecimal();
            UUID id = UUID.fromString(request.params("id"));

            response.type(JSON_TYPE);
            transferService.withdraw(id, amount);
            return null;
        } catch (Exception e) {
            return handleError(response, e);
        }
    }

    Object getHistory(Request request, Response response) {
        try {
            UUID id = UUID.fromString(request.params("id"));
            response.type(JSON_TYPE);
            return transferService.getHistory(id);
        } catch (Exception e) {
            return handleError(response, e);
        }
    }
}

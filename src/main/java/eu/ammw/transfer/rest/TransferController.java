package eu.ammw.transfer.rest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import eu.ammw.transfer.domain.*;
import eu.ammw.transfer.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.post;

public class TransferController implements Controller {

    private static final String JSON_TYPE = "application/json";
    private static final String TEXT_TYPE = "text/plain";

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferController.class);

    private final TransferService transferService;
    private final Gson gson = new Gson();

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @Override
    public void registerEndpoints() {
        post("/transfer", this::transfer);
        get("/accounts/:id/history", this::getHistory);

    }

    Object transfer(Request request, Response response) {
        try {
            Transfer transfer = gson.fromJson(request.body(), Transfer.class);
            response.type(JSON_TYPE);
            return transferService.transfer(transfer.getFrom(), transfer.getTo(), transfer.getAmount());
        } catch (JsonSyntaxException e) {
            LOGGER.warn(e.getMessage());
            response.type(TEXT_TYPE);
            response.status(400);
            return "Bad request";
        } catch (AccountNotFoundException e) {
            LOGGER.warn(e.getMessage());
            response.type(TEXT_TYPE);
            response.status(404);
            return "Account not found!";
        } catch (InsufficientFundsException | NegativeTransferException e) {
            response.type(TEXT_TYPE);
            response.status(409);
            return e.getMessage();
        } catch (TransferException e) {
            LOGGER.error("Exception in transaction", e);
            response.type(TEXT_TYPE);
            response.status(500);
            return e.getMessage();
        }
    }

    Object getHistory(Request request, Response response) {
        try {
            UUID id = UUID.fromString(request.params("id"));
            response.type(JSON_TYPE);
            return transferService.getHistory(id);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Unparseable account ID", e);
            response.type(TEXT_TYPE);
            response.status(400);
            return "Invalid account ID!";
        } catch (AccountNotFoundException e) {
            LOGGER.warn(e.getMessage());
            response.type(TEXT_TYPE);
            response.status(404);
            return "Account not found!";
        }
    }
}

package eu.ammw.transfer.rest;

import eu.ammw.transfer.domain.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.post;

public class TransferController implements Controller {

    private static final String JSON_TYPE = "application/json";
    private static final String TEXT_TYPE = "text/plain";

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferController.class);

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @Override
    public void registerEndpoints() {
        post("/transfer", this::transfer);
        get("/accounts/:id/history", this::getHistory);

    }

    Object transfer(Request request, Response response) {
        // TODO read body
        response.type(JSON_TYPE);
        return transferService.transfer(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.valueOf(200));
    }

    Object getHistory(Request request, Response response) {
        try {
            UUID id = UUID.fromString(request.params("id"));
            response.type(JSON_TYPE);
            return transferService.getHistory(id);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Could not retrieve account", e);
            response.type(TEXT_TYPE);
            response.status(400);
            return "Invalid account ID!";
        }
    }
}

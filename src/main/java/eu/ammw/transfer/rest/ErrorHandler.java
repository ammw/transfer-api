package eu.ammw.transfer.rest;

import com.google.gson.JsonSyntaxException;
import eu.ammw.transfer.domain.AccountNotFoundException;
import eu.ammw.transfer.domain.InsufficientFundsException;
import eu.ammw.transfer.domain.NegativeTransferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class ErrorHandler {
    private static final String TEXT_TYPE = "text/plain";
    private static final Map<Class, Handler> HANDLER_MAP;
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    static {
        HANDLER_MAP = new HashMap<>(7);
        HANDLER_MAP.put(IllegalArgumentException.class, ErrorHandler::handleBadRequest);
        HANDLER_MAP.put(NumberFormatException.class, ErrorHandler::handleBadRequest);
        HANDLER_MAP.put(JsonSyntaxException.class, ErrorHandler::handleBadRequest);
        HANDLER_MAP.put(NullPointerException.class, ErrorHandler::handleBadRequest);
        HANDLER_MAP.put(AccountNotFoundException.class, ErrorHandler::handleAccountNotFound);
        HANDLER_MAP.put(InsufficientFundsException.class, ErrorHandler::handleImpossibleTransfer);
        HANDLER_MAP.put(NegativeTransferException.class, ErrorHandler::handleImpossibleTransfer);
    }

    private ErrorHandler() {
    }

    public static Object handleError(Response response, Exception e) {
        return HANDLER_MAP.getOrDefault(e.getClass(), ErrorHandler::handleOtherError).handle(response, e);
    }

    private static Object handleBadRequest(Response response, Exception e) {
        LOGGER.warn("Error while processing request", e);
        response.type(TEXT_TYPE);
        response.status(400);
        return "Bad Request";
    }

    private static Object handleAccountNotFound(Response response, Exception e) {
        LOGGER.warn("Could not find account", e);
        response.type(TEXT_TYPE);
        response.status(404);
        return "Account not found!";
    }

    private static Object handleImpossibleTransfer(Response response, Exception e) {
        response.type(TEXT_TYPE);
        response.status(409);
        return e.getMessage();
    }

    private static Object handleOtherError(Response response, Exception e) {
        LOGGER.error("Error while performing request", e);
        response.type(TEXT_TYPE);
        response.status(500);
        return String.format("%s: %s", e.getMessage(), e.getCause().getMessage());
    }

    private static interface Handler {
        Object handle(Response response, Exception e);
    }
}

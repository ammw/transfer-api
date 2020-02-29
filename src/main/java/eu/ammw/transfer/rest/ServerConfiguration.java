package eu.ammw.transfer.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.util.Arrays;
import java.util.List;

import static spark.Spark.*;

public class ServerConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfiguration.class);

    private final List<Controller> controllers;

    public ServerConfiguration(Controller... controllers) {
        this.controllers = Arrays.asList(controllers);
    }

    public static void stop() {
        LOGGER.info("Stopping server");
        Spark.stop();
    }

    public void configureAndStart(int port) {
        port(port);
        defaultResponseTransformer(new JsonTransformer());

        get("/health", (rq, rs) -> "OK");
        controllers.forEach(Controller::registerEndpoints);

        LOGGER.info("Server startup finished on port {}", port);
    }
}

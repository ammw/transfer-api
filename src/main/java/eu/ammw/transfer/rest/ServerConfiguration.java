package eu.ammw.transfer.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import static spark.Spark.get;
import static spark.Spark.port;

public class ServerConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfiguration.class);

    private ServerConfiguration() {
    }

    public static void configureAndStart(int port) {
        port(port);

        get("/health", (rq, rs) -> "OK");

        LOGGER.info("Server startup finished on port {}", port);
    }

    public static void stop() {
        LOGGER.info("Stopping server");
        Spark.stop();
    }
}

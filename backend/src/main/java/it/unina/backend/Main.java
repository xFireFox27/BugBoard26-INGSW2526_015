package it.unina.backend;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    // Nota: 0.0.0.0 è fondamentale per Docker affinché sia raggiungibile dall'esterno
    public static final String BASE_URI = "http://0.0.0.0:8080/api/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in it.unina.backend package
        final ResourceConfig rc = new ResourceConfig().packages("it.unina.backend")
                .register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with endpoints available at "
                + "%s%nRunning in Docker mode (Ctrl-C to stop)...", BASE_URI));

        try {
            // MODIFICA FONDAMENTALE PER DOCKER:
            // Mantiene il thread vivo all'infinito invece di aspettare un input da tastiera (System.in.read)
            // che in Docker non esiste e causerebbe lo spegnimento immediato.
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.stop();
    }
}
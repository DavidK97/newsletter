package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.AdminController;
import app.controllers.HomeController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "Newsletters";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
    private static final HomeController homeController = new HomeController(connectionPool);
    private static final AdminController adminController = new AdminController(connectionPool);


    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
                                 config.staticFiles.add("/public");
                                 config.staticFiles.add(staticFiles -> {
                                     staticFiles.hostedPath = "/files";   // Serve at http://localhost:7000/files
                                     staticFiles.directory = "files";    // Serve from "files" folder in working directory
                                     staticFiles.location = Location.EXTERNAL; // Load from outside the JAR
                                 });
                                 config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
                                 config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
                             })
                             .start(7070);

        // Routing
        app.get("/", ctx -> homeController.home(ctx));
        app.post("/", ctx -> homeController.subscribe(ctx));
        app.get("/searchbar", ctx -> homeController.search(ctx)); //en route der lytter på /searchbar - sættes i index.html
        app.post("/admin/newsletters/subscribe", ctx -> homeController.subscribe(ctx));
        app.get("/admin/newsletters/add", ctx -> ctx.render("admin/add-newsletter.html"));
        app.post("/admin/newsletters/add", ctx -> adminController.addNewsletter(ctx));


        // Proper shutdown hook for cleanup
        app.events(event -> {
            event.serverStopping(() -> {
                LOGGER.log(Level.INFO, "Shutting down ConnectionPool...");
                connectionPool.close();
            });
        });

        Runtime.getRuntime()
               .addShutdownHook(new Thread(() -> {
                   LOGGER.log(Level.INFO, "Application shutting down...");
                   connectionPool.close();
               }));
    }
}

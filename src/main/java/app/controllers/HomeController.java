package app.controllers;

import app.entities.Newsletter;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.NewsletterMapper;
import io.javalin.http.Context;

import java.util.List;

public class HomeController {

    private static ConnectionPool connectionPool;

    public HomeController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public static void home(Context ctx) throws DatabaseException {
        List<Newsletter> newsletters = NewsletterMapper.getAllNewsletters(connectionPool);
        ctx.attribute("newsletters", newsletters);
        ctx.render("index.html");
    }

    public void search(Context ctx) throws DatabaseException {
        String word = ctx.queryParam("searchbar");
        System.out.println("TEST " + word);
        List<Newsletter> newsletterList = NewsletterMapper.wordSearch(word, connectionPool);
        if (!newsletterList.isEmpty()) {
            System.out.println("TEST2");
            ctx.attribute("newsletters", newsletterList);
        } else {
            ctx.attribute("message", "Ordet kunne ikke findes i nogle nyhedsbreve");
        }
        ctx.render("index.html");
    }


    public void subscribe(Context ctx) throws DatabaseException {
        String email = ctx.formParam("email");
        String message = "";
        if (email != null) {
            int result = NewsletterMapper.subscribe(email, connectionPool );
            if (result == 1) {
                message = "Tak for din tilmelding";
            } else if (result == 0) {
                message = "Tak, men du var allerede tilmeldt";
            }
            ctx.attribute("message", message);
            ctx.render("index.html");
        }
    }

}

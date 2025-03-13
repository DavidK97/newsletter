package app.controllers;

import app.entities.Newsletter;
import app.persistence.ConnectionPool;
import app.persistence.NewsletterMapper;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import io.javalin.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminController {

    private static ConnectionPool connectionPool;

    public AdminController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public static void addNewsletter(Context ctx) {
        // Get form parameters
        String password = ctx.formParam("password");
        if (password == null || password.isEmpty() || !password.equals("1234")) {
            ctx.status(400).result("Invalid password");
            return;
        }
        String title = ctx.formParam("title");
        String teaserText = ctx.formParam("teaser_text");
        String publishedDateString = ctx.formParam("published_date");
        LocalDate publishedDate = null;
        try {
            publishedDate = LocalDate.parse(publishedDateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            ctx.status(400).result("Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        if (title.isEmpty() || teaserText.isEmpty()) {
            ctx.status(400).result("Missing required inputs - title and content are required");
            return;
        }

        // Get the uploaded file
        List<UploadedFile> uploadedPdfFiles = ctx.uploadedFiles("newsletter_file");
        List<UploadedFile> uploadedThumbFiles = ctx.uploadedFiles("thumbnail_file");

        if (uploadedPdfFiles.isEmpty()) {
            ctx.status(400).result("No pdf file uploaded!");
            return;
        }

        if (uploadedThumbFiles.isEmpty()) {
            ctx.status(400).result("No thumbnail file uploaded!");
            return;
        }

        UploadedFile uploadedPdfFile = uploadedPdfFiles.get(0); // Get first file
        UploadedFile uploadedThumbFile = uploadedThumbFiles.get(0); // Get first file

        // Validate file type (PDF only)
        if (!"application/pdf".equalsIgnoreCase(uploadedPdfFile.contentType())) {
            ctx.status(400).result("Only PDF files are allowed!");
            return;
        }

        if (!"image/png".equalsIgnoreCase(uploadedThumbFile.contentType())) {
            ctx.status(400).result("Only PNG files are allowed!");
            return;
        }

        try {
            FileUtil.streamToFile(uploadedPdfFile.content(), "files/" + uploadedPdfFile.filename());
            FileUtil.streamToFile(uploadedThumbFile.content(), "files/" + uploadedThumbFile.filename());

            Newsletter newsletter = new Newsletter(title, teaserText, uploadedPdfFile.filename(), uploadedThumbFile.filename(), publishedDate);
            NewsletterMapper.addNewsletter(connectionPool, newsletter);

            ctx.status(200).result("Newsletter and thumbnail uploaded successfully: " + uploadedPdfFile.filename() + " and " + uploadedThumbFile.filename());
        } catch (Exception e) {
            ctx.status(500).result("Error uploading files: " + e.getMessage());
        }

    }

}

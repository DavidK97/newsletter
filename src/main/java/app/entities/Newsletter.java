package app.entities;

import java.time.LocalDate;

public class Newsletter {
    private int id;
    private String title;
    private String teaserText;
    private String pdfFileName;
    private String thumbnailFileName;
    private LocalDate published;

    public Newsletter(String title, String teaserText, String pdfFileName, String thumbnailFileName, LocalDate published) {
        this.title = title;
        this.teaserText = teaserText;
        this.pdfFileName = pdfFileName;
        this.thumbnailFileName = thumbnailFileName;
        this.published = published;
    }

    public Newsletter(int id, String title, String teaserText, String pdfFileName, String thumbnailFileName, LocalDate published) {
        this.id = id;
        this.title = title;
        this.teaserText = teaserText;
        this.pdfFileName = pdfFileName;
        this.thumbnailFileName = thumbnailFileName;
        this.published = published;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTeaserText() {
        return teaserText;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public String getThumbnailFileName() {
        return thumbnailFileName;
    }

    public LocalDate getPublished() {
        return published;
    }

    @Override
    public String toString() {
        return "Newsletter{" + "id=" + id + ", title='" + title + '\'' + ", teaserText='" + teaserText + '\'' + ", pdfFileName='" + pdfFileName + '\'' + ", thumbnailFileName='" + thumbnailFileName + '\'' + ", published=" + published + '}';
    }
}

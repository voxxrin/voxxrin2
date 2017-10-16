package voxxrin2.domain;

import java.util.Date;

public class Tweet {

    private Date createdAt;

    private String text;

    private String authorName;

    private String authorImage;

    public Date getCreatedAt() {
        return createdAt;
    }

    public Tweet setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getText() {
        return text;
    }

    public Tweet setText(String text) {
        this.text = text;
        return this;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Tweet setAuthorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public String getAuthorImage() {
        return authorImage;
    }

    public Tweet setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
        return this;
    }
}

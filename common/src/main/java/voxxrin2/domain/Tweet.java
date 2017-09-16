package voxxrin2.domain;

public class Tweet {

    private String createdAt;

    private String text;

    private String author;

    public String getCreatedAt() {
        return createdAt;
    }

    public Tweet setCreatedAt(String createdAt) {
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

    public String getAuthor() {
        return author;
    }

    public Tweet setAuthor(String author) {
        this.author = author;
        return this;
    }
}

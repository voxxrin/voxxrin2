package voxxrin2.domain;

public class AttachedContent {

    private String url;

    private String description;

    private String userId;

    private String mimeType;

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public AttachedContent setMimeType(final String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public AttachedContent setUserId(final String userId) {
        this.userId = userId;
        return this;
    }

    public AttachedContent setUrl(final String url) {
        this.url = url;
        return this;
    }

    public AttachedContent setDescription(final String description) {
        this.description = description;
        return this;
    }


}

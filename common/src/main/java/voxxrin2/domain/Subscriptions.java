package voxxrin2.domain;

public class Subscriptions {

    private String presentationId;

    private long favoriteCount;

    private long remindersCount;

    public long getFavoriteCount() {
        return favoriteCount;
    }

    public long getRemindersCount() {
        return remindersCount;
    }

    public Subscriptions setFavoriteCount(final long favoriteCount) {
        this.favoriteCount = favoriteCount;
        return this;
    }

    public Subscriptions setRemindersCount(final long remindersCount) {
        this.remindersCount = remindersCount;
        return this;
    }

    public String getPresentationId() {
        return presentationId;
    }

    public Subscriptions setPresentationId(String presentationId) {
        this.presentationId = presentationId;
        return this;
    }
}

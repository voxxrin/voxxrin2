package voxxrin2.domain;

public class EventStats {

    private String eventId;

    private String eventName;

    private int favoritesCount;

    private int remindersCount;

    private int ratingsCount;

    private Presentation topFavoritedPresentation;

    private Presentation topRemindedPresentation;

    private Presentation topRatedPresentation;

    public String getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public int getRemindersCount() {
        return remindersCount;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public Presentation getTopFavoritedPresentation() {
        return topFavoritedPresentation;
    }

    public Presentation getTopRemindedPresentation() {
        return topRemindedPresentation;
    }

    public Presentation getTopRatedPresentation() {
        return topRatedPresentation;
    }

    public EventStats setEventId(final String eventId) {

        this.eventId = eventId;
        return this;
    }

    public EventStats setEventName(final String eventName) {
        this.eventName = eventName;
        return this;
    }

    public EventStats setFavoritesCount(final int favoritesCount) {
        this.favoritesCount = favoritesCount;
        return this;
    }

    public EventStats setRemindersCount(final int remindersCount) {
        this.remindersCount = remindersCount;
        return this;
    }

    public EventStats setRatingsCount(final int ratingsCount) {
        this.ratingsCount = ratingsCount;
        return this;
    }

    public EventStats setTopFavoritedPresentation(final Presentation topFavoritedPresentation) {
        this.topFavoritedPresentation = topFavoritedPresentation;
        return this;
    }

    public EventStats setTopRemindedPresentation(final Presentation topRemindedPresentation) {
        this.topRemindedPresentation = topRemindedPresentation;
        return this;
    }

    public EventStats setTopRatedPresentation(final Presentation topRatedPresentation) {
        this.topRatedPresentation = topRatedPresentation;
        return this;
    }
}

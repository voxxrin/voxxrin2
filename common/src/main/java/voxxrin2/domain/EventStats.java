package voxxrin2.domain;

import java.math.BigDecimal;

public class EventStats {

    /**
     * Event
     */

    private String eventId;

    private String eventName;

    private int talksCount;

    private int speakersCount;

    /**
     * Subscriptions
     */

    private int favoritesCount;

    private int usersWithFavoritesCount;

    private int remindersCount;

    private int usersWithRemindersCount;

    private Presentation topFavoritedPresentation;

    private Presentation topRemindedPresentation;

    /**
     * Ratings
     */

    private int ratingsCount;

    private BigDecimal ratingsAvg;

    private Presentation topRatedPresentation;

    public String getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public int getTalksCount() {
        return talksCount;
    }

    public int getSpeakersCount() {
        return speakersCount;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public int getUsersWithFavoritesCount() {
        return usersWithFavoritesCount;
    }

    public int getUsersWithRemindersCount() {
        return usersWithRemindersCount;
    }

    public int getRemindersCount() {
        return remindersCount;
    }

    public Presentation getTopFavoritedPresentation() {
        return topFavoritedPresentation;
    }

    public Presentation getTopRemindedPresentation() {
        return topRemindedPresentation;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public BigDecimal getRatingsAvg() {
        return ratingsAvg;
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

    public EventStats setTalksCount(final int talksCount) {
        this.talksCount = talksCount;
        return this;
    }

    public EventStats setSpeakersCount(final int speakersCount) {
        this.speakersCount = speakersCount;
        return this;
    }

    public EventStats setFavoritesCount(final int favoritesCount) {
        this.favoritesCount = favoritesCount;
        return this;
    }

    public EventStats setUsersWithFavoritesCount(final int usersWithFavoritesCount) {
        this.usersWithFavoritesCount = usersWithFavoritesCount;
        return this;
    }

    public EventStats setUsersWithRemindersCount(final int usersWithRemindersCount) {
        this.usersWithRemindersCount = usersWithRemindersCount;
        return this;
    }

    public EventStats setRemindersCount(final int remindersCount) {
        this.remindersCount = remindersCount;
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

    public EventStats setRatingsCount(final int ratingsCount) {
        this.ratingsCount = ratingsCount;
        return this;
    }

    public EventStats setRatingsAvg(final BigDecimal ratingsAvg) {
        this.ratingsAvg = ratingsAvg;
        return this;
    }

    public EventStats setTopRatedPresentation(final Presentation topRatedPresentation) {
        this.topRatedPresentation = topRatedPresentation;
        return this;
    }
}

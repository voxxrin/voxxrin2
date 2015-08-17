package voxxrin2.domain;

import org.joda.time.DateTime;
import voxxrin2.domain.technical.Reference;

public class Rating {

    private String userId;

    private Reference<Presentation> presentation;

    private int rate;

    private DateTime dateTime;

    public Reference<Presentation> getPresentation() {
        return presentation;
    }

    public String getUserId() {
        return userId;
    }

    public Rating setUserId(final String userId) {
        this.userId = userId;
        return this;
    }

    public int getRate() {
        return rate;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public Rating setPresentation(final Reference<Presentation> presentation) {
        this.presentation = presentation;
        return this;
    }

    public Rating setRate(final int rate) {
        this.rate = rate;
        return this;
    }

    public Rating setDateTime(final DateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }
}

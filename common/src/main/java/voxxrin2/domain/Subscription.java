package voxxrin2.domain;

import org.joda.time.DateTime;
import voxxrin2.domain.technical.Reference;

public class Subscription {

    private Reference<Presentation> presentation;

    private String userId;

    private DateTime dateTime = DateTime.now();

    public Reference<Presentation> getPresentation() {
        return presentation;
    }

    public String getUserId() {
        return userId;
    }

    public Subscription setPresentation(final Reference<Presentation> presentation) {
        this.presentation = presentation;
        return this;
    }

    public Subscription setUserId(final String userId) {
        this.userId = userId;
        return this;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public Subscription setDateTime(final DateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }
}

package voxxrin2.domain;

import org.joda.time.DateTime;

public class Subscription {

    private String presentationRef;

    private String userId;

    private DateTime dateTime = DateTime.now();

    public String getPresentationRef() {
        return presentationRef;
    }

    public String getUserId() {
        return userId;
    }

    public Subscription setPresentationRef(final String presentationRef) {
        this.presentationRef = presentationRef;
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

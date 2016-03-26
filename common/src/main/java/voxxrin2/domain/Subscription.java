package voxxrin2.domain;

import org.joda.time.DateTime;

public class Subscription implements HasPresentationRef<Subscription> {

    private String presentationRef;

    private String userId;

    private DateTime dateTime = DateTime.now();

    @Override
    public String getPresentationRef() {
        return presentationRef;
    }

    public String getUserId() {
        return userId;
    }

    @Override
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

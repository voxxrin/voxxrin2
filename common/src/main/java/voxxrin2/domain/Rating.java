package voxxrin2.domain;

import org.joda.time.DateTime;

public class Rating implements HasPresentationRef<Rating> {

    private String userId;

    private String presentationRef;

    private int rate;

    private DateTime dateTime;

    @Override
    public String getPresentationRef() {
        return presentationRef;
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

    @Override
    public Rating setPresentationRef(final String presentationRef) {
        this.presentationRef = presentationRef;
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

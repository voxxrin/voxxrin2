package voxxrin2.domain;

import org.joda.time.DateTime;
import voxxrin2.domain.technical.Reference;
import voxxrin2.domain.technical.Referenceable;

public class Day extends Referenceable implements HasExternalId {

    public static final String COLLECTION = "day";

    private String name;

    private DateTime date;

    private Reference<Event> event;

    private String externalId;

    public String getName() {
        return name;
    }

    public DateTime getDate() {
        return date;
    }

    public Reference<Event> getEvent() {
        return event;
    }

    @Override
    public String getExternalId() {
        return externalId;
    }

    public Day setEvent(final Reference<Event> event) {
        this.event = event;
        return this;
    }

    public Day setDate(final DateTime date) {
        this.date = date;
        return this;
    }

    public Day setName(final String name) {
        this.name = name;
        return this;
    }

    public Day setExternalId(final String externalId) {
        this.externalId = externalId;
        return this;
    }
}

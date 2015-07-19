package voxxrin2.domain;

import org.joda.time.DateTime;
import voxxrin2.domain.technical.Referenceable;

public class Event extends Referenceable {

    private String name;

    private DateTime from;

    private DateTime to;

    private String description;

    private String location;

    public String getName() {
        return name;
    }

    public DateTime getFrom() {
        return from;
    }

    public DateTime getTo() {
        return to;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Event setLocation(final String location) {
        this.location = location;
        return this;
    }

    public Event setDescription(final String description) {
        this.description = description;
        return this;
    }

    public Event setTo(final DateTime to) {
        this.to = to;
        return this;
    }

    public Event setFrom(final DateTime from) {
        this.from = from;
        return this;
    }

    public Event setName(final String name) {
        this.name = name;
        return this;
    }
}

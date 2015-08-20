package voxxrin2.domain;

import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;
import restx.jackson.Views;
import voxxrin2.domain.technical.Reference;
import voxxrin2.domain.technical.Referenceable;

import java.util.List;

public class Presentation extends Referenceable {

    private String title;

    private String summary;

    @JsonView({Views.Private.class, voxxrin2.serialization.Views.Presentations.Details.class})
    private Reference<Day> day;

    @JsonView({Views.Private.class, voxxrin2.serialization.Views.Presentations.Details.class})
    private Reference<Event> event;

    private List<Reference<Speaker>> speakers;

    private Reference<Room> location;

    private DateTime from;

    private DateTime to;

    private String kind;

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public Reference<Day> getDay() {
        return day;
    }

    public Reference<Event> getEvent() {
        return event;
    }

    public List<Reference<Speaker>> getSpeakers() {
        return speakers;
    }

    public DateTime getFrom() {
        return from;
    }

    public DateTime getTo() {
        return to;
    }

    public String getKind() {
        return kind;
    }

    public Presentation setKind(final String kind) {
        this.kind = kind;
        return this;
    }

    public Presentation setTo(final DateTime to) {
        this.to = to;
        return this;
    }

    public Presentation setFrom(final DateTime from) {
        this.from = from;
        return this;
    }

    public Presentation setSpeakers(final List<Reference<Speaker>> speakers) {
        this.speakers = speakers;
        return this;
    }

    public Presentation setEvent(final Reference<Event> event) {
        this.event = event;
        return this;
    }

    public Presentation setDay(final Reference<Day> day) {
        this.day = day;
        return this;
    }

    public Presentation setSummary(final String summary) {
        this.summary = summary;
        return this;
    }

    public Presentation setTitle(final String title) {
        this.title = title;
        return this;
    }

    public Reference<Room> getLocation() {
        return location;
    }

    public Presentation setLocation(final Reference<Room> location) {
        this.location = location;
        return this;
    }
}

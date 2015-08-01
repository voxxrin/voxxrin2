package voxxrin2.crawlers;

import voxxrin2.domain.*;

import java.util.ArrayList;
import java.util.List;

public class CrawlingResult {

    private Event event;

    private List<Day> days = new ArrayList<Day>();

    private List<Speaker> speakers = new ArrayList<Speaker>();

    private List<Presentation> presentations = new ArrayList<Presentation>();

    private List<Room> rooms = new ArrayList<Room>();

    public CrawlingResult(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public List<Day> getDays() {
        return days;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public List<Presentation> getPresentations() {
        return presentations;
    }

    public List<Room> getRooms() {
        return rooms;
    }
}

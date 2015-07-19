package voxxrin2.persistence;

import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.domain.Event;

import javax.inject.Named;

@Component
public class EventsDataService extends DataService<Event> {

    public EventsDataService(@Named("event") JongoCollection collection) {
        super(collection, Event.class);
    }

}

package voxxrin2.persistence;

import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.domain.Event;

import javax.inject.Named;

@Component
public class EventsDataService extends DataService<Event> {

    public EventsDataService(@Named("events") JongoCollection collection) {
        super(collection, Event.class);
    }

    @Override
    protected void beforeEntitySave(Event entity) {
        entity.setId(generateEventId(entity));
    }

    private String generateEventId(Event entity) {
        String name = entity.getName();
        return name.toUpperCase().replaceAll("\\s", "").replaceAll("\\.", "");
    }
}

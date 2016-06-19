package voxxrin2.persistence;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.domain.Event;
import voxxrin2.domain.EventTemporality;

import javax.inject.Named;

@Component
public class EventsDataService extends DataService<Event> {

    public EventsDataService(@Named(Event.COLLECTION) JongoCollection collection) {
        super(collection, Event.class);
    }

    public Iterable<Event> findByTemporality(Optional<String> temporality) {
        if (temporality.isPresent()) {
            if (EventTemporality.FUTURE.name().equalsIgnoreCase(temporality.get())) {
                return findAllAndSort("{ to: { $gte: # } }", "{ from: 1 }", DateTime.now().toDate());
            } else if (EventTemporality.PAST.name().equalsIgnoreCase(temporality.get())) {
                return findAllAndSort("{ to: { $lte: # } }", "{ from: 1 }", DateTime.now().toDate());
            }
        }
        return findAllAndSort("{ from: 1 }");
    }
}

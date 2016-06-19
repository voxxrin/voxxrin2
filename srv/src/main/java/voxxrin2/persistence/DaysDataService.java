package voxxrin2.persistence;

import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.domain.Day;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.ElementURI;

import javax.inject.Named;

@Component
public class DaysDataService extends DataService<Day> {

    public DaysDataService(@Named(Day.COLLECTION) JongoCollection collection) {
        super(collection, Day.class);
    }

    public Iterable<Day> findByEvent(String eventId) {
        return findAllAndSort("{ event: # }", "{ date : 1 }", ElementURI.of(Type.event, eventId).toString());
    }
}

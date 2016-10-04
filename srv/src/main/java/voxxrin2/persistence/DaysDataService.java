package voxxrin2.persistence;

import com.google.common.base.Optional;
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

    public Iterable<Day> findByEventAlias(String eventId) {
        return findAllAndSort("{ eventId: # }", "{ date : 1 }", eventId);
    }

    public Optional<Day> findByAlias(String id) {
        return Optional.fromNullable(find("{ externalId: # }", id));
    }
}

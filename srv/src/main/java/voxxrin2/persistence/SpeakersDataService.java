package voxxrin2.persistence;

import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.domain.Speaker;

import javax.inject.Named;

@Component
public class SpeakersDataService extends DataService<Speaker> {

    public SpeakersDataService(@Named(Speaker.COLLECTION) JongoCollection collection) {
        super(collection, Speaker.class);
    }

    public Iterable<Speaker> search(String eventId, String name) {
        return findAll("{ eventId: #, name: { $regex: #, $options: 'i' } }", eventId, name);
    }
}

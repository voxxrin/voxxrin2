package voxxrin2.persistence;

import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.domain.Speaker;

import javax.inject.Named;

@Component
public class SpeakersDataService extends DataService<Speaker> {

    public SpeakersDataService(@Named("speaker") JongoCollection collection) {
        super(collection, Speaker.class);
    }

}

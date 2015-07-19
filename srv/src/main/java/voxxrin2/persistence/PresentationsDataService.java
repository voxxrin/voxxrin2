package voxxrin2.persistence;

import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.domain.Day;
import voxxrin2.domain.Presentation;

import javax.inject.Named;

@Component
public class PresentationsDataService extends DataService<Presentation> {

    public PresentationsDataService(@Named("presentation") JongoCollection collection) {
        super(collection, Presentation.class);
    }

}

package voxxrin2.persistence;

import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.domain.Day;

import javax.inject.Named;

@Component
public class DaysDataService extends DataService<Day> {

    public DaysDataService(@Named(Day.COLLECTION) JongoCollection collection) {
        super(collection, Day.class);
    }

}

package voxxrin2.persistence;

import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.domain.Room;

import javax.inject.Named;

@Component
public class RoomsDataService extends DataService<Room> {

    public RoomsDataService(@Named("room") JongoCollection collection) {
        super(collection, Room.class);
    }
}

package voxxrin2.rest;

import org.bson.types.ObjectId;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import voxxrin2.domain.Room;
import voxxrin2.persistence.RoomsDataService;

import java.util.List;

@Component
@RestxResource
@PermitAll
public class RoomsResource {

    private final RoomsDataService roomsDataService;

    public RoomsResource(RoomsDataService roomsDataService) {
        this.roomsDataService = roomsDataService;
    }

    @GET("/rooms")
    public Iterable<Room> getRooms() {
        return roomsDataService.findAll();
    }

    @GET("/rooms/{id}")
    public Room getRoom(String id) {
        return roomsDataService.find("{ _id : # }", new ObjectId(id));
    }

    @POST("/rooms")
    public Room saveRoom(Room room) {
        return roomsDataService.save(room);
    }

    @POST("/rooms/several")
    public List<Room> saveRooms(List<Room> rooms) {
        for (Room room : rooms) {
            roomsDataService.save(room);
        }
        return rooms;
    }
}

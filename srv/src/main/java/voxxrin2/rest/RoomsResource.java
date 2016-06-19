package voxxrin2.rest;

import com.google.common.base.Optional;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import restx.security.RolesAllowed;
import voxxrin2.domain.Room;
import voxxrin2.persistence.RoomsDataService;

import java.util.List;

@Component
@RestxResource
public class RoomsResource {

    private final RoomsDataService roomsDataService;

    public RoomsResource(RoomsDataService roomsDataService) {
        this.roomsDataService = roomsDataService;
    }

    @GET("/rooms")
    @PermitAll
    public Iterable<Room> getRooms() {
        return roomsDataService.findAll();
    }

    @GET("/rooms/{id}")
    @PermitAll
    public Optional<Room> getRoom(String id) {
        return roomsDataService.findById(id);
    }

    @POST("/rooms")
    @RolesAllowed({"ADM", "restx-admin"})
    public Room saveRoom(Room room) {
        return roomsDataService.save(room);
    }

    @POST("/rooms/several")
    @RolesAllowed({"ADM", "restx-admin"})
    public List<Room> saveRooms(List<Room> rooms) {
        for (Room room : rooms) {
            roomsDataService.save(room);
        }
        return rooms;
    }
}

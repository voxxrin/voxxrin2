package voxxrin2.rest;

import com.google.common.base.Optional;
import org.bson.types.ObjectId;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import restx.security.RolesAllowed;
import voxxrin2.domain.Event;
import voxxrin2.persistence.EventsDataService;

@Component
@RestxResource
public class EventsResource {

    private final EventsDataService eventsDataService;

    public EventsResource(EventsDataService dataService) {
        this.eventsDataService = dataService;
    }

    @GET("/events")
    @PermitAll
    public Iterable<Event> getAllEvents(Optional<String> mode) {
        return eventsDataService.findByTemporality(mode);
    }

    @GET("/events/{id}")
    @PermitAll
    public Optional<Event> getEvent(String id) {
        if (ObjectId.isValid(id)) {
            return eventsDataService.findById(id);
        } else {
            return eventsDataService.findByAlias(id);
        }
    }

    @POST("/events")
    @RolesAllowed({"ADM", "restx-admin"})
    public Event saveEvent(Event event) {
        return eventsDataService.save(event);
    }
}

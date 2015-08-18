package voxxrin2.rest;

import org.bson.types.ObjectId;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
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
    public Iterable<Event> getAllEvents() {
        return eventsDataService.findAll();
    }

    @GET("/events/{id}")
    @PermitAll
    public Event getEvent(String id) {
        return eventsDataService.find("{ _id: # }", new ObjectId(id));
    }

    @POST("/events")
    public Event saveEvent(Event event) {
        return eventsDataService.save(event);
    }
}

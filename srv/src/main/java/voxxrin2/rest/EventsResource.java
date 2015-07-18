package voxxrin2.rest;

import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import voxxrin2.domain.Event;
import voxxrin2.persistence.EventsDataService;

@Component
@RestxResource
@PermitAll
public class EventsResource {

    private final EventsDataService dataService;

    public EventsResource(EventsDataService dataService) {
        this.dataService = dataService;
    }

    @GET("/events")
    public Iterable<Event> getAllEvents() {
        return dataService.findAll();
    }

    @POST("/events")
    public Event saveEvent(Event event) {
        return dataService.save(event);
    }
}

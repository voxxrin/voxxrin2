package voxxrin2.rest;

import org.bson.types.ObjectId;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import voxxrin2.domain.Day;
import voxxrin2.domain.Event;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.ElementURI;
import voxxrin2.persistence.DaysDataService;
import voxxrin2.persistence.EventsDataService;

@Component
@RestxResource
@PermitAll
public class EventsResource {

    private final EventsDataService eventsDataService;
    private final DaysDataService daysDataService;

    public EventsResource(EventsDataService dataService,
                          DaysDataService daysDataService) {
        this.eventsDataService = dataService;
        this.daysDataService = daysDataService;
    }

    @GET("/events")
    public Iterable<Event> getAllEvents() {
        return eventsDataService.findAll();
    }

    @GET("/events/{id}")
    public Event getEvent(String id) {
        return eventsDataService.find("{ _id: # }", new ObjectId(id));
    }

    @GET("/events/{id}/days")
    public Iterable<Day> getEventDays(String id) {
        return daysDataService.findAll("{ event: # }", ElementURI.of(Type.event, id).toString());
    }

    @POST("/events")
    public Event saveEvent(Event event) {
        return eventsDataService.save(event);
    }
}

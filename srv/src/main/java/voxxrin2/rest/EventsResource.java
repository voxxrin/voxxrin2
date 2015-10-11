package voxxrin2.rest;

import com.google.common.base.Optional;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
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

    private enum EventTemporality {
        FUTURE, PAST
    }

    @GET("/events")
    @PermitAll
    public Iterable<Event> getAllEvents(Optional<String> mode) {

        if (mode.isPresent()) {
            if (EventTemporality.FUTURE.name().equalsIgnoreCase(mode.get())) {
                return eventsDataService.findAllAndSort("{ to: { $gte: # } }", "{ from: 1 }", DateTime.now().toDate());
            } else if (EventTemporality.PAST.name().equalsIgnoreCase(mode.get())) {
                return eventsDataService.findAllAndSort("{ to: { $lte: # } }", "{ from: 1 }", DateTime.now().toDate());
            }
        }

        return eventsDataService.findAllAndSort("{ from: 1 }");
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

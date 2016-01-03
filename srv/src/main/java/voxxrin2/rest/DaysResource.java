package voxxrin2.rest;

import org.bson.types.ObjectId;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import restx.security.RolesAllowed;
import voxxrin2.domain.Day;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.ElementURI;
import voxxrin2.persistence.DaysDataService;

@Component
@RestxResource
public class DaysResource {

    private final DaysDataService daysDataService;

    public DaysResource(DaysDataService daysDataService) {
        this.daysDataService = daysDataService;
    }

    @GET("/days")
    @PermitAll
    public Iterable<Day> getDays() {
        return daysDataService.findAllAndSort("{ date : 1 }");
    }

    @GET("/days/{id}")
    @PermitAll
    public Day getDay(String id) {
        return daysDataService.find("{ _id: # }", new ObjectId(id));
    }

    @POST("/days")
    @RolesAllowed({"ADM", "restx-admin"})
    public Day saveDay(Day day) {
        return daysDataService.save(day);
    }

    @GET("/events/{eventId}/days")
    @PermitAll
    public Iterable<Day> getEventDays(String eventId) {
        return daysDataService.findAllAndSort("{ event: # }", "{ date : 1 }", ElementURI.of(Type.event, eventId).toString());
    }
}

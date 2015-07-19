package voxxrin2.rest;

import org.bson.types.ObjectId;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import voxxrin2.domain.Day;
import voxxrin2.domain.Presentation;
import voxxrin2.persistence.DaysDataService;
import voxxrin2.persistence.PresentationsDataService;

@Component
@RestxResource
public class DaysResource {

    private final DaysDataService daysDataService;
    private final PresentationsDataService presentationsDataService;

    public DaysResource(DaysDataService daysDataService,
                        PresentationsDataService presentationsDataService) {
        this.daysDataService = daysDataService;
        this.presentationsDataService = presentationsDataService;
    }

    @GET("/days/{id}")
    public Day getEventDays(String id) {
        return daysDataService.find("{ _id: # }", new ObjectId(id));
    }

    @POST("/days")
    public Day saveDay(Day day) {
        return daysDataService.save(day);
    }

    @GET("/days/{id}/presentations")
    public Iterable<Presentation> getPresentations(String id) {
        return presentationsDataService.findAll("{ day: # }", new ObjectId(id));
    }
}

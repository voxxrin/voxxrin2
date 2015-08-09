package voxxrin2.rest;

import org.bson.types.ObjectId;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.ElementURI;
import voxxrin2.persistence.PresentationsDataService;

import java.util.List;

@Component
@RestxResource
@PermitAll
public class PresentationsResource {

    private final PresentationsDataService presentationsDataService;

    public PresentationsResource(PresentationsDataService presentationsDataService) {
        this.presentationsDataService = presentationsDataService;
    }

    @GET("/presentations")
    public Iterable<Presentation> getPresentations() {
        return presentationsDataService.findAll();
    }

    @GET("/presentations/{id}")
    public Presentation getPresentation(String id) {
        return presentationsDataService.find("{ _id: # }", new ObjectId(id));
    }

    @POST("/presentations")
    public Presentation savePresentation(Presentation presentation) {
        return presentationsDataService.save(presentation);
    }

    @POST("/presentations/several")
    public List<Presentation> savePresentations(List<Presentation> presentations) {
        for (Presentation presentation : presentations) {
            presentationsDataService.save(presentation);
        }
        return presentations;
    }

    @GET("/events/{eventId}/presentations")
    public Iterable<Presentation> getEventPresentations(String eventId) {
        return presentationsDataService.findAll("{ event: # }", ElementURI.of(Type.event, eventId).toString());
    }

    @GET("/days/{dayId}/presentations")
    public Iterable<Presentation> getDayPresentations(String dayId) {
        return presentationsDataService.findAllAndSort("{ day: # }", "{ from: 1 }", ElementURI.of(Type.day, dayId).toString());
    }
}

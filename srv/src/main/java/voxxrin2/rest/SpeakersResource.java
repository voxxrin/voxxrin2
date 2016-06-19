package voxxrin2.rest;

import com.google.common.base.Optional;
import restx.annotations.Consumes;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import restx.security.RolesAllowed;
import voxxrin2.domain.Speaker;
import voxxrin2.persistence.SpeakersDataService;

import java.util.List;

@Component
@RestxResource
public class SpeakersResource {

    private final SpeakersDataService speakersDataService;

    public SpeakersResource(SpeakersDataService speakersDataService) {
        this.speakersDataService = speakersDataService;
    }

    @GET("/speakers")
    @PermitAll
    public Iterable<Speaker> getSpeakers() {
        return speakersDataService.findAll();
    }

    @GET("/speakers/{id}")
    @PermitAll
    public Optional<Speaker> getSpeaker(String id) {
        return speakersDataService.findById(id);
    }

    @POST("/speakers")
    @Consumes("application/json;view=voxxrin2.serialization.Views$Presentations$Details")
    @RolesAllowed({"ADM", "restx-admin"})
    public Speaker saveSpeaker(Speaker speaker) {
        return speakersDataService.save(speaker);
    }

    @POST("/speakers/several")
    @Consumes("application/json;view=voxxrin2.serialization.Views$Presentations$Details")
    @RolesAllowed({"ADM", "restx-admin"})
    public List<Speaker> saveSpeakers(List<Speaker> speakers) {
        for (Speaker speaker : speakers) {
            speakersDataService.save(speaker);
        }
        return speakers;
    }
}

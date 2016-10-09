package voxxrin2.rest;

import restx.annotations.GET;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.Room;
import voxxrin2.domain.Speaker;
import voxxrin2.persistence.PresentationsDataService;
import voxxrin2.persistence.RoomsDataService;
import voxxrin2.persistence.SpeakersDataService;

@Component
@RestxResource(value = "/search")
public class SearchResource {

    private final RoomsDataService roomsDataService;
    private final PresentationsDataService presentationsDataService;
    private final SpeakersDataService speakersDataService;

    public SearchResource(RoomsDataService roomsDataService,
                          PresentationsDataService presentationsDataService,
                          SpeakersDataService speakersDataService) {
        this.roomsDataService = roomsDataService;
        this.presentationsDataService = presentationsDataService;
        this.speakersDataService = speakersDataService;
    }

    @GET("/events/:eventId/rooms")
    @PermitAll
    public Iterable<Room> findRooms(@Param(kind = Param.Kind.PATH) String eventId,
                                    @Param(kind = Param.Kind.QUERY) String fullName) {
        return roomsDataService.search(eventId, fullName);
    }

    @GET("/events/:eventId/presentations")
    @PermitAll
    public Iterable<Presentation> findPresentations(@Param(kind = Param.Kind.PATH) String eventId,
                                                    @Param(kind = Param.Kind.QUERY) String title) {
        return presentationsDataService.search(eventId, title);
    }

    @GET("/events/:eventId/speakers")
    @PermitAll
    public Iterable<Speaker> findSpeakers(@Param(kind = Param.Kind.PATH) String eventId,
                                          @Param(kind = Param.Kind.QUERY) String name) {
        return speakersDataService.search(eventId, name);
    }
}

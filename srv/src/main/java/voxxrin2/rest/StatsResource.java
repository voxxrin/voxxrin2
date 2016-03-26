package voxxrin2.rest;

import org.bson.types.ObjectId;
import restx.RestxRequest;
import restx.annotations.GET;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.RestxSecurityManager;
import voxxrin2.domain.Event;
import voxxrin2.domain.EventStats;
import voxxrin2.persistence.EventStatsService;
import voxxrin2.persistence.EventsDataService;
import voxxrin2.security.Permissions;

@Component
@RestxResource(value = "/stats")
public class StatsResource {

    private final EventStatsService eventStatsService;
    private final EventsDataService eventsDataService;
    private final RestxSecurityManager securityManager;

    public StatsResource(EventStatsService eventStatsService,
                         EventsDataService eventsDataService,
                         RestxSecurityManager securityManager) {
        this.eventStatsService = eventStatsService;
        this.eventsDataService = eventsDataService;
        this.securityManager = securityManager;
    }

    @GET("/event/{id}")
    public EventStats buildEventStats(String id, @Param(kind = Param.Kind.CONTEXT) RestxRequest request) {

        Event event = eventsDataService.find("{ _id: # }", new ObjectId(id));
        if (event == null) {
            return null;
        }

        securityManager.check(request, Permissions.buildEventAdminPermission(event));

        return eventStatsService.build(event);
    }
}

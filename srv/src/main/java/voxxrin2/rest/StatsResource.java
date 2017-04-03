package voxxrin2.rest;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.bson.types.ObjectId;
import restx.RestxRequest;
import restx.annotations.GET;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
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

    private Optional<Event> findEvent(String id) {
        if (ObjectId.isValid(id)) {
            return eventsDataService.findById(id);
        } else {
            return eventsDataService.findByAlias(id);
        }
    }

    @GET("/public/event/{id}")
    @PermitAll
    public Optional<EventStats> buildEventPublicStats(String id) {
        return findEvent(id).transform(new Function<Event, EventStats>() {
            @Override
            public EventStats apply(Event input) {
                return eventStatsService.buildPublic(input);
            }
        });
    }

    @GET("/event/{id}")
    public Optional<EventStats> buildEventStats(String id, @Param(kind = Param.Kind.CONTEXT) RestxRequest request) {
        Optional<Event> event = findEvent(id);
        if (event.isPresent()) {
            securityManager.check(request, Permissions.buildEventAdminPermission(event.get()));
            return Optional.of(eventStatsService.build(event.get()));
        }
        return Optional.absent();
    }
}

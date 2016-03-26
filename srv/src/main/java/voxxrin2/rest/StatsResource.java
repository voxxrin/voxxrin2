package voxxrin2.rest;

import restx.annotations.GET;
import restx.annotations.RestxResource;
import restx.factory.Component;
import voxxrin2.domain.EventStats;
import voxxrin2.persistence.EventStatsService;

@Component
@RestxResource(value = "/stats")
public class StatsResource {

    private final EventStatsService eventStatsService;

    public StatsResource(EventStatsService eventStatsService) {
        this.eventStatsService = eventStatsService;
    }

    @GET("/event/{id}")
    public EventStats buildEventStats(String id) {
        return eventStatsService.build(id);
    }
}

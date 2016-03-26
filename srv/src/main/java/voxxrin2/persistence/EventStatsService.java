package voxxrin2.persistence;

import restx.factory.Component;
import voxxrin2.domain.EventStats;

@Component
public class EventStatsService {

    private final SubscriptionService subscriptionService;

    public EventStatsService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public EventStats build(String eventId) {

        return new EventStats();

    }
}

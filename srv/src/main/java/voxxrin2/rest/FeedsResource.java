package voxxrin2.rest;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import restx.annotations.GET;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import voxxrin2.domain.Event;
import voxxrin2.domain.Tweet;
import voxxrin2.persistence.EventsDataService;
import voxxrin2.services.TwitterFeedService;

import java.util.List;

@Component
@RestxResource("/feeds")
public class FeedsResource {

    private final EventsDataService eventsDataService;
    private final TwitterFeedService feedsService;

    public FeedsResource(EventsDataService eventsDataService, TwitterFeedService feedService) {
        this.eventsDataService = eventsDataService;
        this.feedsService = feedService;
    }

    private Optional<Event> findEvent(String id) {
        if (ObjectId.isValid(id)) {
            return eventsDataService.findById(id);
        } else {
            return eventsDataService.findByAlias(id);
        }
    }

    @GET("/twitter")
    @PermitAll
    public List<Tweet> fetchTweetFeed(String eventId) {
        return findEvent(eventId).transform(feedsService.toTweetsFeed()).or(Lists.<Tweet>newArrayList());
    }
}

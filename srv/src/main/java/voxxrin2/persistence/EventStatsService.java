package voxxrin2.persistence;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import org.bson.types.ObjectId;
import restx.factory.Component;
import voxxrin2.domain.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class EventStatsService {

    private static final Function<HasPresentationRef, String> HAS_PRESENTATION_REF_INDEXER = new Function<HasPresentationRef, String>() {
        @Override
        public String apply(HasPresentationRef input) {
            return input.getPresentationRef();
        }
    };

    private final PresentationsDataService presentationsDataService;
    private final EventsDataService eventsDataService;
    private final RemindersService remindersService;
    private final FavoritesService favoritesService;
    private final RatingService ratingService;

    public EventStatsService(PresentationsDataService presentationsDataService,
                             EventsDataService eventsDataService,
                             RemindersService remindersService,
                             FavoritesService favoritesService,
                             RatingService ratingService) {
        this.presentationsDataService = presentationsDataService;
        this.eventsDataService = eventsDataService;
        this.remindersService = remindersService;
        this.favoritesService = favoritesService;
        this.ratingService = ratingService;
    }

    public EventStats build(String id) {

        Event event = eventsDataService.find("{ _id: # }", new ObjectId(id));
        if (event == null) {
            return null;
        }

        EventStats eventStats = new EventStats();
        List<Subscription> reminders = Lists.newArrayList(remindersService.getReminders(event));
        List<Subscription> favorites = Lists.newArrayList(favoritesService.getFavorites(event));
        List<Rating> ratings = Lists.newArrayList(ratingService.findEventRatings(event.getEventId()));

        return eventStats
                .setEventId(event.getEventId())
                .setEventName(event.getName())
                .setFavoritesCount(favorites.size())
                .setRemindersCount(reminders.size())
                .setRatingsCount(ratings.size())
                .setTopFavoritedPresentation(topOccurence(favorites))
                .setTopRatedPresentation(topOccurence(ratings))
                .setTopRemindedPresentation(topOccurence(reminders));
    }

    private <T extends HasPresentationRef> Presentation topOccurence(List<T> elts) {

        ImmutableListMultimap<String, T> indexedElts = Multimaps.index(elts, HAS_PRESENTATION_REF_INDEXER);

        int max = 0;
        String presentationRef = null;
        for (Map.Entry<String, Collection<T>> entry : indexedElts.asMap().entrySet()) {
            int size = entry.getValue().size();
            if (size > max) {
                max = size;
                presentationRef = entry.getKey();
            }
        }

        if (presentationRef != null) {
            return presentationsDataService.findByRef(presentationRef);
        }

        return null;
    }
}

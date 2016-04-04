package voxxrin2.persistence;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.math.DoubleMath;
import restx.factory.Component;
import voxxrin2.domain.*;
import voxxrin2.domain.technical.Reference;
import voxxrin2.rest.PresentationsResource;

import java.math.BigDecimal;
import java.util.*;

@Component
public class EventStatsService {

    private static final Function<HasPresentationRef, String> HAS_PRESENTATION_REF_INDEXER = new Function<HasPresentationRef, String>() {
        @Override
        public String apply(HasPresentationRef input) {
            return input.getPresentationRef();
        }
    };

    private final PresentationsDataService presentationsDataService;
    private final PresentationsResource presentationsResource;
    private final RemindersService remindersService;
    private final FavoritesService favoritesService;
    private final RatingService ratingService;

    public EventStatsService(PresentationsDataService presentationsDataService,
                             PresentationsResource presentationsResource,
                             RemindersService remindersService,
                             FavoritesService favoritesService,
                             RatingService ratingService) {
        this.presentationsDataService = presentationsDataService;
        this.presentationsResource = presentationsResource;
        this.remindersService = remindersService;
        this.favoritesService = favoritesService;
        this.ratingService = ratingService;
    }

    public EventStats build(Event event) {

        EventStats eventStats = new EventStats();
        List<Subscription> reminders = Lists.newArrayList(remindersService.getReminders(event));
        List<Subscription> favorites = Lists.newArrayList(favoritesService.getFavorites(event));
        List<Rating> ratings = Lists.newArrayList(ratingService.findEventRatings(event.getEventId()));

        ArrayList<Presentation> presentations = Lists.newArrayList(presentationsResource.getEventPresentations(event.getKey()));

        return eventStats
                .setEventId(event.getEventId())
                .setEventName(event.getName())
                .setTalksCount(presentations.size())
                .setSpeakersCount(speakersCount(presentations))
                .setFavoritesCount(favorites.size())
                .setUsersWithFavoritesCount(usersWithSubscriptionCount(favorites))
                .setUsersWithRemindersCount(usersWithSubscriptionCount(reminders))
                .setRemindersCount(reminders.size())
                .setRatingsCount(ratings.size())
                .setRatingsAvg(computeAvg(ratings))
                .setTopFavoritedPresentation(topOccurence(favorites))
                .setTopRatedPresentation(topOccurence(ratings))
                .setTopRemindedPresentation(topOccurence(reminders));
    }

    private int usersWithSubscriptionCount(List<Subscription> subs) {
        return FluentIterable.from(subs).transform(new Function<Subscription, String>() {
            @Override
            public String apply(Subscription input) {
                return input.getUserId();
            }
        }).toSet().size();
    }

    private int speakersCount(ArrayList<Presentation> presentations) {
        Set<String> speakers = new HashSet<>();
        for (Presentation presentation : presentations) {
            for (Reference<Speaker> speakerReference : presentation.getSpeakers()) {
                speakers.add(speakerReference.getUri().getKey());
            }
        }
        return speakers.size();
    }

    private BigDecimal computeAvg(List<Rating> ratings) {

        if (ratings.size() == 0) {
            return BigDecimal.ZERO;
        }

        FluentIterable<Integer> rates = FluentIterable.from(ratings).transform(new Function<Rating, Integer>() {
            @Override
            public Integer apply(Rating input) {
                return input.getRate();
            }
        });

        return BigDecimal.valueOf(DoubleMath.mean(rates.toList()));
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

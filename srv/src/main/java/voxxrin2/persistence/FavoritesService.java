package voxxrin2.persistence;

import com.google.common.collect.Iterables;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.domain.Event;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.Subscription;
import voxxrin2.domain.User;

import javax.inject.Named;

@Component
public class FavoritesService extends SubscriptionService {

    private final JongoCollection favorites;

    public FavoritesService(@Named("favorite") JongoCollection favorites) {
        this.favorites = favorites;
    }

    /**
     * Read
     */

    public boolean isFavorite(User user, Presentation presentation) {
        return isSubscribed(favorites, user, presentation);
    }

    public long getFavoritesCount(Presentation presentation) {
        return Iterables.size(getFavorites(presentation));
    }

    public Iterable<Subscription> getFavorites(Presentation presentation) {
        return getSubscriptions(favorites, presentation);
    }

    public Iterable<Subscription> getFavorites(Event event) {
        return getSubscriptions(favorites, event);
    }


    /**
     * CRUD
     */

    public SubscriptionDbWrite addOrUpdateFavorite(String presentationId) {
        return addOrUpdateSubscription(favorites, presentationId);
    }

    public Subscription deleteFavorite(String presentationId) {
        return deleteSubscription(favorites, presentationId);
    }
}

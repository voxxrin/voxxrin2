package voxxrin2.persistence;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.auth.AuthModule;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.User;
import voxxrin2.rest.SubscriptionResource;

import javax.inject.Named;

@Component
public class PresentationsDataService extends DataService<Presentation> {

    private final SubscriptionResource subscriptionResource;

    private final Function<Presentation, Presentation> USER_PRESENTATION_FUNCTOR = new Function<Presentation, Presentation>() {
        @Override
        public Presentation apply(Presentation input) {
            Optional<User> user = AuthModule.currentUser();
            if (user.isPresent()) {
                input.setReminded(subscriptionResource.isReminded(user.get(), input));
                input.setFavorite(subscriptionResource.isFavorite(user.get(), input));
            }
            input.setRemindMeCount(subscriptionResource.getRemindMeCount(input));
            input.setFavoriteCount(subscriptionResource.getFavoriteCount(input));
            return input;
        }
    };

    public PresentationsDataService(@Named("presentation") JongoCollection collection,
                                    SubscriptionResource remindMeResource) {
        super(collection, Presentation.class);
        this.subscriptionResource = remindMeResource;
    }

    @Override
    public Iterable<Presentation> findAllAndSort(String query, String sorting, Object... params) {
        return Iterables.transform(super.findAllAndSort(query, sorting, params), USER_PRESENTATION_FUNCTOR);
    }

    @Override
    public Presentation find(String query, Object... params) {
        return USER_PRESENTATION_FUNCTOR.apply(super.find(query, params));
    }
}

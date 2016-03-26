package voxxrin2.persistence;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.auth.AuthModule;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.User;

import javax.inject.Named;

@Component
public class PresentationsDataService extends DataService<Presentation> {

    private final RemindersService remindersService;
    private final FavoritesService favoritesService;

    private final Function<Presentation, Presentation> USER_PRESENTATION_FUNCTOR = new Function<Presentation, Presentation>() {
        @Override
        public Presentation apply(Presentation input) {
            Optional<User> user = AuthModule.currentUser();
            if (user.isPresent()) {
                input.setReminded(remindersService.isReminded(user.get(), input));
                input.setFavorite(favoritesService.isFavorite(user.get(), input));
            }
            input.setRemindMeCount(remindersService.getRemindersCount(input));
            input.setFavoriteCount(favoritesService.getFavoritesCount(input));
            return input;
        }
    };

    public PresentationsDataService(@Named("presentation") JongoCollection collection,
                                    RemindersService remindersService,
                                    FavoritesService favoritesService) {
        super(collection, Presentation.class);
        this.remindersService = remindersService;
        this.favoritesService = favoritesService;
    }

    @Override
    public Iterable<Presentation> findAllAndSort(String query, String sorting, Object... params) {
        return Iterables.transform(super.findAllAndSort(query, sorting, params), USER_PRESENTATION_FUNCTOR);
    }

    @Override
    public Presentation find(String query, Object... params) {
        Presentation input = super.find(query, params);
        if (input != null) {
            return USER_PRESENTATION_FUNCTOR.apply(input);
        }
        return null;
    }
}

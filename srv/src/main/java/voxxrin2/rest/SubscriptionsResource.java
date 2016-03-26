package voxxrin2.rest;

import restx.annotations.DELETE;
import restx.annotations.POST;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import voxxrin2.domain.Subscription;
import voxxrin2.persistence.FavoritesService;
import voxxrin2.persistence.RemindersService;

@Component
@RestxResource
public class SubscriptionsResource {

    private final RemindersService remindersService;
    private final FavoritesService favoritesService;

    public SubscriptionsResource(RemindersService remindersService, FavoritesService favoritesService) {
        this.remindersService = remindersService;
        this.favoritesService = favoritesService;
    }

    @POST("/remindme")
    public Subscription requestRemindMe(@Param(kind = Param.Kind.QUERY) String presentationId) {
        return remindersService.addOrUpdateRemindMe(presentationId).subscription;
    }

    @DELETE("/remindme")
    public Subscription deleteRemindMe(@Param(kind = Param.Kind.QUERY) String presentationId) {
        return remindersService.deleteRemindMe(presentationId);
    }

    @POST("/favorite")
    public Subscription requestFavorite(@Param(kind = Param.Kind.QUERY) String presentationId) {
        return favoritesService.addOrUpdateFavorite(presentationId).subscription;
    }

    @DELETE("/favorite")
    public Subscription deleteFavorite(@Param(kind = Param.Kind.QUERY) String presentationId) {
        return favoritesService.deleteFavorite(presentationId);
    }
}

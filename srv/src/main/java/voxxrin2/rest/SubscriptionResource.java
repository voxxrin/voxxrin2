package voxxrin2.rest;

import restx.annotations.POST;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.auth.AuthModule;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.Subscription;
import voxxrin2.domain.Type;
import voxxrin2.domain.User;
import voxxrin2.domain.technical.ElementURI;
import voxxrin2.domain.technical.Reference;

import javax.inject.Named;

@Component
@RestxResource
public class SubscriptionResource {

    private final JongoCollection remindMe;
    private final JongoCollection favorite;

    public SubscriptionResource(@Named("remindMe") JongoCollection remindMe,
                                @Named("favorite") JongoCollection favorite) {
        this.remindMe = remindMe;
        this.favorite = favorite;
    }

    private boolean isSubscribed(User user, String presentationId, JongoCollection collection) {
        return collection
                .get()
                .count("{ presentation: #, userId: # }", ElementURI.of(Type.presentation, presentationId).toString(), user.getId()) > 0;
    }

    public boolean isReminded(User user, String presentationId) {
        return isSubscribed(user, presentationId, remindMe);
    }

    public boolean isFavorite(User user, String presentationId) {
        return isSubscribed(user, presentationId, favorite);
    }

    @POST("/remindme")
    public Subscription requestRemindMe(@Param(kind = Param.Kind.QUERY) String presentationId) {

        User user = AuthModule.currentUser().get();

        Reference<Presentation> presentation = Reference.of(Type.presentation, presentationId);
        Subscription remindMe = new Subscription()
                .setPresentation(presentation)
                .setUserId(user.getId());

        this.remindMe.get()
                .update("{ presentation: #, userId: # }", presentation.getUri().toString(), user.getId())
                .upsert()
                .with(remindMe);

        return remindMe;
    }

    @POST("/favorite")
    public Subscription requestFavorite(@Param(kind = Param.Kind.QUERY) String presentationId) {

        User user = AuthModule.currentUser().get();

        Reference<Presentation> presentation = Reference.of(Type.presentation, presentationId);
        Subscription favorite = new Subscription()
                .setPresentation(presentation)
                .setUserId(user.getId());

        this.favorite.get()
                .update("{ presentation: #, userId: # }", presentation.getUri().toString(), user.getId())
                .upsert()
                .with(favorite);

        return favorite;
    }
}

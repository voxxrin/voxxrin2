package voxxrin2.rest;

import restx.WebException;
import restx.annotations.DELETE;
import restx.annotations.POST;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.http.HttpStatus;
import restx.jongo.JongoCollection;
import voxxrin2.auth.AuthModule;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.Subscription;
import voxxrin2.domain.Type;
import voxxrin2.domain.User;
import voxxrin2.domain.technical.Reference;
import voxxrin2.utils.Utils;

import javax.inject.Named;

@Component
@RestxResource
public class SubscriptionResource {

    private final JongoCollection reminders;
    private final JongoCollection favorites;

    public SubscriptionResource(@Named("remindMe") JongoCollection reminders,
                                @Named("favorite") JongoCollection favorites) {
        this.reminders = reminders;
        this.favorites = favorites;
    }

    private boolean isSubscribed(User user, Presentation presentation, JongoCollection collection) {
        return collection
                .get()
                .count("{ presentationRef: #, userId: # }", Utils.buildPresentationBusinessRef(presentation), user.getId()) > 0;
    }

    private long getSubscriptionCount(Presentation presentation, JongoCollection collection) {
        return collection.get().count("{ presentationRef: # }", Utils.buildPresentationBusinessRef(presentation));
    }

    public boolean isReminded(User user, Presentation presentation) {
        return isSubscribed(user, presentation, reminders);
    }

    public boolean isFavorite(User user, Presentation presentation) {
        return isSubscribed(user, presentation, favorites);
    }

    public long getRemindMeCount(Presentation presentation) {
        return getSubscriptionCount(presentation, reminders);
    }

    public long getFavoriteCount(Presentation presentation) {
        return getSubscriptionCount(presentation, favorites);
    }

    @POST("/remindme")
    public Subscription requestRemindMe(@Param(kind = Param.Kind.QUERY) String presentationId) {
        return addOrUpdateSubscription(reminders, presentationId).subscription;
    }

    @DELETE("/remindme")
    public Subscription deleteRemindMe(@Param(kind = Param.Kind.QUERY) String presentationId) {
        return deleteSubscription(this.reminders, presentationId);
    }

    @POST("/favorite")
    public Subscription requestFavorite(@Param(kind = Param.Kind.QUERY) String presentationId) {
        SubscriptionDbWrite dbWrite = addOrUpdateSubscription(favorites, presentationId);
        return dbWrite.subscription;
    }

    @DELETE("/favorite")
    public Subscription deleteFavorite(@Param(kind = Param.Kind.QUERY) String presentationId) {
        return deleteSubscription(this.favorites, presentationId);
    }

    private SubscriptionDbWrite addOrUpdateSubscription(JongoCollection collection, String presentationId) {

        User user = AuthModule.currentUser().get();

        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        String presentationRef = Utils.buildPresentationBusinessRef(presentation);

        Subscription subscription = new Subscription()
                .setPresentationRef(presentationRef)
                .setUserId(user.getId());

        collection.get()
                .update("{ presentationRef: #, userId: # }", presentationRef, user.getId())
                .upsert()
                .with(subscription);

        return new SubscriptionDbWrite(presentation, subscription);
    }

    private Subscription deleteSubscription(JongoCollection collection, String presentationId) {

        User user = AuthModule.currentUser().get();
        String presentationRef = Utils.getPresentationRef(presentationId);

        Subscription existingSubscription = collection
                .get()
                .findOne("{ presentationRef: #, userId: # }", presentationRef, user.getId()).as(Subscription.class);

        if (existingSubscription == null) {
            throw new WebException(HttpStatus.NOT_FOUND);
        }

        collection
                .get()
                .remove("{ presentationRef: #, userId: # }", presentationRef, user.getId());

        return existingSubscription;
    }

    private static class SubscriptionDbWrite {

        public Presentation presentation;

        public Subscription subscription;

        public SubscriptionDbWrite(Presentation presentation, Subscription subscription) {
            this.presentation = presentation;
            this.subscription = subscription;
        }
    }
}

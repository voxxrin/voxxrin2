package voxxrin2.rest;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.slf4j.Logger;
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
import voxxrin2.domain.technical.PushStatus;
import voxxrin2.domain.technical.Reference;
import voxxrin2.webservices.Push;

import javax.inject.Named;

import static org.slf4j.LoggerFactory.getLogger;

@Component
@RestxResource
public class SubscriptionResource {

    private static final Logger logger = getLogger(SubscriptionResource.class);
    private static final int NOTIFICATION_DELAY = 10;

    private final JongoCollection reminders;
    private final JongoCollection favorites;
    private final Push push;

    public SubscriptionResource(@Named("remindMe") JongoCollection reminders,
                                @Named("favorite") JongoCollection favorites,
                                Push push) {
        this.reminders = reminders;
        this.favorites = favorites;
        this.push = push;
    }

    private boolean isSubscribed(User user, Presentation presentation, JongoCollection collection) {
        return collection
                .get()
                .count("{ presentationRef: #, userId: # }", buildPresentationBusinessRef(presentation), user.getId()) > 0;
    }

    private long getSubscriptionCount(Presentation presentation, JongoCollection collection) {
        return collection.get().count("{ presentationRef: # }", buildPresentationBusinessRef(presentation));
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
    public Subscription requestFavorite(@Param(kind = Param.Kind.QUERY) String presentationId,
                                        @Param(kind = Param.Kind.QUERY) Optional<String> deviceToken) {

        SubscriptionDbWrite dbWrite = addOrUpdateSubscription(favorites, presentationId);

        if (deviceToken.isPresent()) {
            sendPushNotification(dbWrite.presentation, deviceToken.get());
        }

        return dbWrite.subscription;
    }

    @DELETE("/favorite")
    public Subscription deleteFavorite(@Param(kind = Param.Kind.QUERY) String presentationId) {
        return deleteSubscription(this.favorites, presentationId);
    }

    private SubscriptionDbWrite addOrUpdateSubscription(JongoCollection collection, String presentationId) {

        User user = AuthModule.currentUser().get();

        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        String presentationRef = buildPresentationBusinessRef(presentation);

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
        String presentationRef = getPresentationRef(presentationId);

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

    private String getPresentationRef(String presentationId) {
        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        return buildPresentationBusinessRef(presentation);
    }

    private String buildPresentationBusinessRef(Presentation presentation) {
        return String.format("%s:%s", presentation.getEventId(), presentation.getExternalId());
    }

    private void sendPushNotification(Presentation presentation, String deviceToken) {

        String presentationTitle = presentation.getTitle();
        String msg = String.format("Le talk '%s' qui fait partie de vos favoris va bientôt commencer !", presentationTitle);
        DateTime when = presentation.getFrom().minusMinutes(NOTIFICATION_DELAY);

        PushStatus status = push.sendMsg(msg, deviceToken, when);

        logger.info("Push notification sent to device id '{}' concerning favorited presentation '{}' (fired time = {}). " +
                "Status = (code: {}, payload: {})", deviceToken, presentationTitle, when, status.getCode(), status.getPayload());
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

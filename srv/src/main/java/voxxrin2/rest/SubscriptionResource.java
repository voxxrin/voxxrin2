package voxxrin2.rest;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.slf4j.Logger;
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

    private final JongoCollection remindMe;
    private final JongoCollection favorite;
    private final Push push;

    public SubscriptionResource(@Named("remindMe") JongoCollection remindMe,
                                @Named("favorite") JongoCollection favorite,
                                Push push) {
        this.remindMe = remindMe;
        this.favorite = favorite;
        this.push = push;
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
    public Subscription requestFavorite(@Param(kind = Param.Kind.QUERY) String presentationId,
                                        @Param(kind = Param.Kind.QUERY) Optional<String> deviceToken) {

        User user = AuthModule.currentUser().get();

        Reference<Presentation> presentation = Reference.of(Type.presentation, presentationId);
        Subscription favorite = new Subscription()
                .setPresentation(presentation)
                .setUserId(user.getId());

        this.favorite.get()
                .update("{ presentation: #, userId: # }", presentation.getUri().toString(), user.getId())
                .upsert()
                .with(favorite);

        if (deviceToken.isPresent()) {
            sendPushNotification(presentation, deviceToken.get());
        }

        return favorite;
    }

    private void sendPushNotification(Reference<Presentation> presentation, String deviceToken) {

        Presentation resolvedPresentation = presentation.get();
        String presentationTitle = resolvedPresentation.getTitle();
        String msg = String.format("Le talk '%s' qui fait partie de vos favoris va bientôt commencer !", presentationTitle);
        DateTime when = resolvedPresentation.getFrom().minusMinutes(NOTIFICATION_DELAY);

        PushStatus status = push.sendMsg(msg, deviceToken, when);

        logger.info("Push notification sent to device id '{}' concerning favorited presentation '{}' (fired time = {}). " +
                "Status = (code: {}, payload: {})", deviceToken, presentationTitle, when, status.getCode(), status.getPayload());
    }
}

package voxxrin2.webservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import restx.factory.Component;
import restx.jackson.FrontObjectMapperFactory;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.technical.PushNotification;
import voxxrin2.domain.technical.PushStatus;

import javax.inject.Named;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class Push {

    private static final String IONIC_PUSH_URL = "https://push.ionic.io/api/v1/push";
    private static final Logger logger = getLogger(Push.class);
    private static final int NOTIFICATION_DELAY = 10;

    private final String ionicAppId;
    private final String ionicAppPrivateKey;
    private final ObjectMapper mapper;

    public Push(WebServicesSettings webServicesSettings,
                @Named(FrontObjectMapperFactory.MAPPER_NAME) ObjectMapper mapper) {
        this.mapper = mapper;
        this.ionicAppId = webServicesSettings.ionicAppId();
        this.ionicAppPrivateKey = webServicesSettings.ionicAppPrivateKey();
    }

    private PushStatus send(PushNotification notification) {

        try {
            byte[] input = mapper.writeValueAsBytes(notification);

            // See http://docs.ionic.io/docs/push-sending-push
            HttpRequest request = HttpRequest.post(IONIC_PUSH_URL)
                    .header("X-Ionic-Application-Id", ionicAppId)
                    .basic(ionicAppPrivateKey, "")
                    .contentType("application/json")
                    .send(input);

            int code = request.code();
            String body = request.body();

            logger.info("{} : {}", code, body);

            return PushStatus.of(code, body);

        } catch (JsonProcessingException e) {
            logger.error("Unable to serialize push notification", e);
        }

        return PushStatus.of(-1, null);
    }

    public PushStatus sendSubscribedTalkBeginningNotification(Presentation presentation, String deviceToken) {

        String presentationTitle = presentation.getTitle();
        String msg = String.format("Le talk '%s' qui fait partie de vos favoris va bientôt commencer !", presentationTitle);
        DateTime when = presentation.getFrom().minusMinutes(NOTIFICATION_DELAY);

        PushStatus status = send(PushNotification.fromUserIds(msg, Lists.newArrayList(deviceToken), Optional.of(when)));

        logger.info("Push notification sent to device id '{}' concerning favorited presentation '{}' (fired time = {}). " +
                "Status = (code: {}, payload: {})", deviceToken, presentationTitle, when, status.getCode(), status.getPayload());

        return status;
    }

    public PushStatus sendDigitalContentReleasingNotification(Presentation presentation, List<String> userIds, String contentUrl) {

        if (userIds.size() > 0) {

            String msg = String.format("Une vidéo concernant le talk '%s' sur lequel vous vous êtes inscrit vient d'être publiée !", presentation.getTitle());

            PushNotification notification = PushNotification.fromUserIds(msg, userIds, Optional.<DateTime>absent());
            notification.addPayloadContent("contentUrl", contentUrl);
            PushStatus status = send(notification);

            logger.info("Push notification sent to device userIds '{}' (count = {}) concerning favorited presentation '{}'. " +
                    "Status = (code: {}, payload: {})", userIds, userIds.size(), presentation.getTitle(), status.getCode(), status.getPayload());

            return status;
        }

        return null;
    }
}

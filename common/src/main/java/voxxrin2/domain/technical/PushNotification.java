package voxxrin2.domain.technical;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushNotification {

    @JsonProperty("user_ids")
    private final List<String> userIds;

    private final List<String> tokens;

    private final Long scheduled;

    private final NotificationBody notification = new NotificationBody();

    private PushNotification(Optional<DateTime> when, List<String> tokens, List<String> userIds) {
        this.scheduled = toMillis(when);
        this.tokens = tokens;
        this.userIds = userIds;
    }

    private Long toMillis(Optional<DateTime> when) {
        return when.transform(new Function<DateTime, Long>() {
            @Override
            public Long apply(DateTime input) {
                return input.getMillis();
            }
        }).orNull();
    }

    public static PushNotification fromUserIds(String alert, List<String> userIds, Optional<DateTime> when) {
        PushNotification pushNotification = new PushNotification(when, null, userIds);
        pushNotification.getNotification().setAlert(alert);
        return pushNotification;
    }

    public static PushNotification fromTokens(String alert, List<String> tokens, Optional<DateTime> when) {
        PushNotification pushNotification = new PushNotification(when, tokens, null);
        pushNotification.getNotification().setAlert(alert);
        return pushNotification;
    }

    public NotificationBody getNotification() {
        return notification;
    }

    public Long getScheduled() {
        return scheduled;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    @SuppressWarnings("unchecked")
    public void addPayloadContent(String key, String value) {

        HashMap<String, String> payload = new HashMap<>();
        payload.put(key, value);

        Object androidPayload = getNotification().getAndroid().get("payload");
        Object iosPayload = getNotification().getIos().get("payload");

        if (androidPayload == null) {
            getNotification().getAndroid().put("payload", payload);
        } else {
            ((Map<String, Object>) androidPayload).put(key, value);
        }
        if (iosPayload == null) {
            getNotification().getIos().put("payload", payload);
        } else {
            ((Map<String, Object>) iosPayload).put(key, value);
        }
    }

    public static class NotificationBody {

        private String alert;

        private final Map<String, Object> android = new HashMap<>();

        private final Map<String, Object> ios = new HashMap<>();

        public String getAlert() {
            return alert;
        }

        public Map<String, Object> getAndroid() {
            return android;
        }

        public Map<String, Object> getIos() {
            return ios;
        }

        public NotificationBody setAlert(final String alert) {
            this.alert = alert;
            return this;
        }
    }
}

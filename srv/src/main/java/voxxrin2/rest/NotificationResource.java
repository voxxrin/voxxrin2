package voxxrin2.rest;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import restx.annotations.PUT;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.RolesAllowed;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.Subscription;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.PushStatus;
import voxxrin2.domain.technical.Reference;
import voxxrin2.utils.Utils;
import voxxrin2.webservices.Push;

import javax.inject.Named;

@Component
@RestxResource
public class NotificationResource {

    private final Push push;
    private final JongoCollection reminders;

    public NotificationResource(Push push,
                                @Named("remindMe") JongoCollection reminders) {
        this.push = push;
        this.reminders = reminders;
    }

    @PUT("/notify/favorite/{presentationId}")
    @RolesAllowed({"ADM", "restx-admin", "event-admin"})
    public Integer notifySubscribedUsers(@Param(kind = Param.Kind.PATH) String presentationId,
                                            @Param(kind = Param.Kind.QUERY) String contentUrl) {

        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        Iterable<Subscription> subs = reminders.get()
                .find("{ presentationRef: # }", Utils.buildPresentationBusinessRef(presentation))
                .as(Subscription.class);

        ImmutableList<String> userIds = toUserIds(subs);
        push.sendDigitalContentReleasingNotification(presentation, userIds, contentUrl);

        return userIds.size();
    }

    private ImmutableList<String> toUserIds(Iterable<Subscription> subs) {
        return FluentIterable
                .from(subs)
                .transform(new Function<Subscription, String>() {
                    @Override
                    public String apply(Subscription input) {
                        return input.getUserId();
                    }
                })
                .toList();
    }
}

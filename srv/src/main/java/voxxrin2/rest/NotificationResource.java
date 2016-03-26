package voxxrin2.rest;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import restx.annotations.PUT;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.RolesAllowed;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.Subscription;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.Reference;
import voxxrin2.persistence.RemindersService;
import voxxrin2.services.PushService;

@Component
@RestxResource
public class NotificationResource {

    private final PushService push;
    private final RemindersService remindersService;

    public NotificationResource(PushService push, RemindersService remindersService) {
        this.push = push;
        this.remindersService = remindersService;
    }

    @PUT("/notify/favorite/{presentationId}")
    @RolesAllowed({"ADM", "restx-admin", "event-admin"})
    public Integer notifySubscribedUsers(@Param(kind = Param.Kind.PATH) String presentationId,
                                         @Param(kind = Param.Kind.QUERY) String contentUrl) {

        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        Iterable<Subscription> subs = remindersService.getReminders(presentation);

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

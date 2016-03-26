package voxxrin2.rest;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import restx.annotations.POST;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.RolesAllowed;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.AttachedContent;
import voxxrin2.domain.Subscription;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.Reference;
import voxxrin2.persistence.PresentationsDataService;
import voxxrin2.persistence.RemindersService;
import voxxrin2.services.PushService;

@Component
@RestxResource
public class AttachedContentResource {

    private final PushService push;
    private final PresentationsDataService presentationsDataService;
    private final RemindersService remindersService;

    public AttachedContentResource(PushService push,
                                   PresentationsDataService presentationsDataService,
                                   RemindersService remindersService) {
        this.push = push;
        this.presentationsDataService = presentationsDataService;
        this.remindersService = remindersService;
    }

    @POST("/presentation/{presentationId}/attachedContent")
    @RolesAllowed({"ADM", "restx-admin", "event-admin"})
    public AttachedContent attachContentToPresentation(@Param(kind = Param.Kind.PATH) String presentationId,
                                                       @Param(kind = Param.Kind.BODY) AttachedContent content) {

        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        if (presentation == null) {
            return null;
        }
        Iterable<Subscription> subs = remindersService.getReminders(presentation);
        presentationsDataService.attachReleasedContent(presentation, content);

        ImmutableList<String> userIds = toUserIds(subs);
        push.sendReleasedContentNotification(presentation, userIds);

        return content;
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

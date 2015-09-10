package voxxrin2.rest;

import com.google.common.base.Optional;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.auth.AuthModule;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.RemindMe;
import voxxrin2.domain.Type;
import voxxrin2.domain.User;
import voxxrin2.domain.technical.ElementURI;
import voxxrin2.domain.technical.Reference;

import javax.inject.Named;

@Component
@RestxResource
public class RemindMeResource {

    private final JongoCollection remindMe;

    public RemindMeResource(@Named("remindMe") JongoCollection remindMe) {
        this.remindMe = remindMe;
    }

    public boolean isReminded(User user, String presentationId) {
        return remindMe
                .get()
                .count("{ presentation: #, userId: # }", ElementURI.of(Type.presentation, presentationId).toString(), user.getId()) > 0;
    }

    @GET("/remindme")
    public Iterable<RemindMe> getRemindMe(Optional<String> presentationId) {

        User user = AuthModule.currentUser().get();

        if (presentationId.isPresent()) {
            return remindMe
                    .get()
                    .find("{ presentation: #, userId: # }", ElementURI.of(Type.presentation, presentationId.get()).toString(), user.getId())
                    .as(RemindMe.class);
        } else {
            return remindMe
                    .get()
                    .find("{ userId: # }", user.getId())
                    .as(RemindMe.class);
        }
    }

    @POST("/remindme")
    public RemindMe requestRemindMe(@Param(kind = Param.Kind.QUERY) String presentationId) {

        User user = AuthModule.currentUser().get();

        Reference<Presentation> presentation = Reference.of(Type.presentation, presentationId);
        RemindMe remindMe = new RemindMe()
                .setPresentation(presentation)
                .setUserId(user.getId());

        this.remindMe.get()
                .update("{ presentation: #, userId: # }", presentation.getUri().toString(), user.getId())
                .upsert()
                .with(remindMe);

        return remindMe;
    }
}

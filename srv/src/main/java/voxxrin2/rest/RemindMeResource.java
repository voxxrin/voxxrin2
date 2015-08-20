package voxxrin2.rest;

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

    private final JongoCollection reminder;

    public RemindMeResource(@Named("reminder") JongoCollection reminder) {
        this.reminder = reminder;
    }

    @GET("/remindme")
    public RemindMe getReminder(String presentationId) {

        User user = AuthModule.currentUser().get();
        return reminder
                .get()
                .findOne("{ presentation: #, userId: # }", ElementURI.of(Type.presentation, presentationId).toString(), user.getId())
                .as(RemindMe.class);
    }

    @POST("/remindme")
    public RemindMe requestReminder(@Param(kind = Param.Kind.QUERY) String presentationId) {

        User user = AuthModule.currentUser().get();

        Reference<Presentation> presentation = Reference.of(Type.presentation, presentationId);
        RemindMe remindMe = new RemindMe()
                .setPresentation(presentation)
                .setUserId(user.getId());

        reminder.get()
                .update("{ presentation: #, userId: # }", presentation.getUri().toString(), user.getId())
                .upsert()
                .with(remindMe);

        return remindMe;
    }
}

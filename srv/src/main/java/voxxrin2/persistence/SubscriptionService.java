package voxxrin2.persistence;

import restx.WebException;
import restx.http.HttpStatus;
import restx.jongo.JongoCollection;
import voxxrin2.auth.AuthModule;
import voxxrin2.domain.*;
import voxxrin2.domain.technical.Reference;
import voxxrin2.utils.PresentationRef;

public abstract class SubscriptionService {

    /**
     * Read
     */

    protected boolean isSubscribed(JongoCollection collection, User user, Presentation presentation) {
        return collection
                .get()
                .count("{ presentationRef: #, userId: # }", PresentationRef.buildPresentationBusinessRef(presentation), user.getId()) > 0;
    }

    protected Iterable<Subscription> getSubscriptions(JongoCollection collection, Presentation presentation) {
        return collection.get()
                .find("{ presentationRef: # }", PresentationRef.buildPresentationBusinessRef(presentation))
                .as(Subscription.class);
    }

    protected Iterable<Subscription> getSubscriptions(JongoCollection collection, Event event) {
        return collection.get()
                .find("{ presentationRef: { $regex: # } }", String.format("%s:.*", event.getEventId()))
                .as(Subscription.class);
    }


    /**
     * CRUD
     */


    protected SubscriptionDbWrite addOrUpdateSubscription(JongoCollection collection, String presentationId) {

        User user = AuthModule.currentUser().get();

        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        String presentationRef = PresentationRef.buildPresentationBusinessRef(presentation);

        Subscription subscription = new Subscription()
                .setPresentationRef(presentationRef)
                .setUserId(user.getId());

        collection.get()
                .update("{ presentationRef: #, userId: # }", presentationRef, user.getId())
                .upsert()
                .with(subscription);

        return new SubscriptionDbWrite(presentation, subscription);
    }


    protected Subscription deleteSubscription(JongoCollection collection, String presentationId) {

        User user = AuthModule.currentUser().get();
        String presentationRef = PresentationRef.getPresentationRef(presentationId);

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

    public static class SubscriptionDbWrite {

        public Presentation presentation;

        public Subscription subscription;

        public SubscriptionDbWrite(Presentation presentation, Subscription subscription) {
            this.presentation = presentation;
            this.subscription = subscription;
        }
    }
}

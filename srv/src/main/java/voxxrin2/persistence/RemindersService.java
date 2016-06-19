package voxxrin2.persistence;

import com.google.common.collect.Iterables;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import voxxrin2.domain.Event;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.Subscription;
import voxxrin2.domain.User;

import javax.inject.Named;

@Component
public class RemindersService extends SubscriptionService {

    private final JongoCollection reminders;

    public RemindersService(@Named(Subscription.REMINDERS_COLLECTION) JongoCollection reminders) {
        this.reminders = reminders;
    }

    /**
     * Read
     */

    public boolean isReminded(User user, Presentation presentation) {
        return isSubscribed(reminders, user, presentation);
    }

    public long getRemindersCount(Presentation presentation) {
        return Iterables.size(getSubscriptions(reminders, presentation));
    }

    public Iterable<Subscription> getReminders(Presentation presentation) {
        return getSubscriptions(reminders, presentation);
    }

    public Iterable<Subscription> getReminders(Event event) {
        return getSubscriptions(reminders, event);
    }

    /**
     * CRUD
     */

    public SubscriptionDbWrite addOrUpdateRemindMe(String presentationId) {
        return addOrUpdateSubscription(reminders, presentationId);
    }

    public Subscription deleteRemindMe(String presentationId) {
        return deleteSubscription(reminders, presentationId);
    }
}

package voxxrin2.persistence;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import restx.WebException;
import restx.factory.Component;
import restx.http.HttpStatus;
import restx.jongo.JongoCollection;
import voxxrin2.auth.AuthModule;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.Rating;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.Reference;
import voxxrin2.utils.Utils;

import javax.inject.Named;

@Component
public class RatingService {

    private final JongoCollection ratings;

    public RatingService(@Named("rating") JongoCollection ratings) {
        this.ratings = ratings;
    }

    public Iterable<Rating> findAll(String presentationId) {
        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        return ratings.get()
                .find("{ presentationRef: # }", Utils.buildPresentationBusinessRef(presentation))
                .as(Rating.class);
    }

    public Rating ratePresentation(String presentationId, int rate) {

        if (!AuthModule.currentUser().isPresent()) {
            throw new WebException(HttpStatus.UNAUTHORIZED);
        }

        Optional<Presentation> presentation = Reference.<Presentation>of(Type.presentation, presentationId).maybeGet();
        if (!presentation.isPresent()) {
            throw new WebException(HttpStatus.NOT_FOUND);
        }

        String userId = AuthModule.currentUser().get().getId();

        String presentationRef = Utils.buildPresentationBusinessRef(presentation.get());
        Rating rating = new Rating()
                .setDateTime(DateTime.now())
                .setPresentationRef(presentationRef)
                .setRate(rate)
                .setUserId(userId);

        ratings.get()
                .update("{ presentationRef: #, userId: # }", presentationRef, userId)
                .upsert()
                .with(rating);

        return rating;
    }
}

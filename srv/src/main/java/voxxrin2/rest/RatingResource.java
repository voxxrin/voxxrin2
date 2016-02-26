package voxxrin2.rest;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import restx.WebException;
import restx.annotations.GET;
import restx.annotations.PUT;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.http.HttpStatus;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;
import voxxrin2.auth.AuthModule;
import voxxrin2.domain.Presentation;
import voxxrin2.domain.Rating;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.Reference;

import javax.inject.Named;

@Component
@RestxResource
public class RatingResource {

    private final JongoCollection ratings;

    public RatingResource(@Named("rating") JongoCollection ratings) {
        this.ratings = ratings;
    }

    @GET("/ratings/{presentationId}")
    @PermitAll
    public Iterable<Rating> getRatings(@Param(kind = Param.Kind.PATH) String presentationId) {
        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        return ratings.get()
                .find("{ presentationRef: # }", buildPresentationBusinessRef(presentation))
                .as(Rating.class);
    }

    private String buildPresentationBusinessRef(Presentation presentation) {
        return String.format("%s:%s", presentation.getEventId(), presentation.getExternalId());
    }


    @PUT("/ratings/{presentationId}")
    public Rating ratePresentation(@Param(kind = Param.Kind.PATH) String presentationId,
                                   @Param(kind = Param.Kind.QUERY) int rate) {

        if (!AuthModule.currentUser().isPresent()) {
            throw new WebException(HttpStatus.UNAUTHORIZED);
        }

        Optional<Presentation> presentation = Reference.<Presentation>of(Type.presentation, presentationId).maybeGet();
        if (!presentation.isPresent()) {
            throw new WebException(HttpStatus.NOT_FOUND);
        }

        String userId = AuthModule.currentUser().get().getId();

        String presentationRef = buildPresentationBusinessRef(presentation.get());
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

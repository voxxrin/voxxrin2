package voxxrin2.rest;

import com.google.common.base.Strings;
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
import voxxrin2.domain.Presentation;
import voxxrin2.domain.Rating;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.ElementURI;
import voxxrin2.domain.technical.Reference;

import javax.inject.Named;

@Component
@RestxResource
@PermitAll
public class RatingResource {

    private final JongoCollection ratings;

    public RatingResource(@Named("rating") JongoCollection ratings) {
        this.ratings = ratings;
    }

    @GET("/ratings/{presentationId}")
    public Iterable<Rating> getRatings(@Param(kind = Param.Kind.PATH) String presentationId) {
        return ratings.get()
                .find("{ presentation: # }", ElementURI.of(Type.presentation, presentationId).toString())
                .as(Rating.class);
    }

    @PUT("/ratings/{presentationId}")
    public Rating ratePresentation(@Param(kind = Param.Kind.PATH) String presentationId,
                                   @Param(kind = Param.Kind.QUERY) String uuid,
                                   @Param(kind = Param.Kind.QUERY) int rate) {

        if (Strings.isNullOrEmpty(uuid)) {
            throw new WebException(HttpStatus.BAD_REQUEST);
        }

        Reference<Presentation> presentation = Reference.of(Type.presentation, presentationId);
        if (!presentation.maybeGet().isPresent()) {
            throw new WebException(HttpStatus.NOT_FOUND);
        }

        Rating rating = new Rating()
                .setDateTime(DateTime.now())
                .setPresentation(presentation)
                .setRate(rate)
                .setUuid(uuid);
        ratings.get().save(rating);

        return rating;
    }

}

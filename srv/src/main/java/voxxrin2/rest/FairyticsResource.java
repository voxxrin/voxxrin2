package voxxrin2.rest;

import org.slf4j.Logger;
import restx.annotations.POST;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.RolesAllowed;

import javax.inject.Named;
import java.util.HashMap;

import static org.slf4j.LoggerFactory.getLogger;

@Component
@RestxResource
public class FairyticsResource {

    private static final Logger logger = getLogger(FairyticsResource.class);

    private final JongoCollection fairytics;

    public FairyticsResource(@Named("fairytics") JongoCollection fairytics) {
        this.fairytics = fairytics;
    }

    @POST("/fairytics")
    @RolesAllowed({"ADM", "fairytics-publisher"})
    public void onFairyticsData(@Param(kind = Param.Kind.BODY) HashMap<String, Object> payload) {
        logger.info("Received fairytics data {}", payload);
        fairytics.get().save(payload);
    }

}

package voxxrin2.rest;

import restx.annotations.DELETE;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.RolesAllowed;
import voxxrin2.persistence.DataService;

import java.util.Set;

@Component
@RestxResource
public class EntityResource {

    private final Set<DataService> dataServices;

    public EntityResource(Set<DataService> dataServices) {
        this.dataServices = dataServices;
    }

    @RolesAllowed({"ADM", "restx-admin"})
    @DELETE("/entities/crawled")
    public void cleanCrawledData(@Param(kind = Param.Kind.QUERY) String eventId) {
        for (DataService dataService : dataServices) {
            dataService.removeCrawledEntities(eventId);
        }
    }
}

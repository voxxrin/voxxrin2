package voxxrin2.rest;

import restx.WebException;
import restx.annotations.GET;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.http.HttpStatus;
import restx.security.PermitAll;

@Component
@RestxResource("/monitor")
public class MonitorResource {

    @GET("/heartbeat")
    @PermitAll
    public void sendHeartbeat() {
        throw new WebException(HttpStatus.OK);
    }
}

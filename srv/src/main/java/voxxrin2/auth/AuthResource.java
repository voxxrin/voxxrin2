package voxxrin2.auth;

import com.google.common.collect.Maps;
import restx.RestxRequest;
import restx.WebException;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.http.HttpStatus;
import restx.security.PermitAll;
import restx.security.RestxPrincipal;
import restx.security.RestxSession;
import voxxrin2.utils.Functions;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RestxResource("/auth")
public class AuthResource {

    private Map<String, OAuthProvider> oAuthProviders;

    public AuthResource(List<OAuthProvider> oAuthProviders) {
        this.oAuthProviders = Maps.uniqueIndex(oAuthProviders, Functions.PROVIDERS_LIST_TO_MAP_FN);
    }

    @GET("/current")
    public RestxPrincipal currentUser() {
        return RestxSession.current().getPrincipal().get();
    }

    @PermitAll
    @GET("/{provider}")
    @POST("/{provider}")
    public Token authenticate(String provider, @Param(kind = Param.Kind.CONTEXT, value = "request") RestxRequest restxRequest) throws IOException {

        /* this endpoint is used for 2 purpose:
        1) to obtain the twitter authenticate URL, to redirect the user to it
        2) to check the user has properly signed in with twitter and performs the OAuth request
        We distinguish the 2 cases based on the presence of oauth_token and oauth_verifier query strings
         */
        OAuthProvider oAuthProvider = oAuthProviders.get(provider);
        if (oAuthProvider == null) {
            throw new WebException(HttpStatus.NOT_FOUND);
        }
        return oAuthProvider.authenticate(restxRequest.getQueryParams(), restxRequest);
    }
}

package voxxrin2.auth;

import com.google.common.collect.Maps;
import restx.*;
import restx.factory.Component;
import restx.http.HttpStatus;
import voxxrin2.utils.Functions;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class ProviderRedirectRoute extends StdRoute {

    private final Map<String, OAuthProvider> oAuthProviders;

    public ProviderRedirectRoute(List<OAuthProvider> oAuthProviders) {
        super("ProviderRedirectRoute", new StdRestxRequestMatcher("GET", "/auth/redirect/{provider}"));
        this.oAuthProviders = Maps.uniqueIndex(oAuthProviders, Functions.PROVIDERS_LIST_TO_MAP_FN);
    }

    @Override
    public void handle(RestxRequestMatch match, RestxRequest req, RestxResponse resp, RestxContext ctx) throws IOException {
        String providerUrl = oAuthProviders.get(match.getPathParam("provider")).getProviderUrl();
        resp.setHeader("Location", providerUrl);
        resp.setStatus(HttpStatus.FOUND);
    }
}

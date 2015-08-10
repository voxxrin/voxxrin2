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
public class RedirectAuthResourceRoute extends StdRoute {

    private final Map<String, OAuthProvider> oAuthProviders;

    public RedirectAuthResourceRoute(List<OAuthProvider> oAuthProviders) {
        super("RedirectAuthResourceRoute", new StdRestxRequestMatcher("GET", "/auth/redirect/{provider}"));
        this.oAuthProviders = Maps.uniqueIndex(oAuthProviders, Functions.OAUTH_PROVIDER_MAP_INDEXER);
    }

    @Override
    public void handle(RestxRequestMatch match, RestxRequest req, RestxResponse resp, RestxContext ctx) throws
            IOException {
        String providerUrl = oAuthProviders.get(match.getPathParam("provider")).getProviderUrl();
        resp.setHeader("Location", providerUrl);
        resp.setStatus(HttpStatus.FOUND);
    }
}
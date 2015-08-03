package voxxrin2.auth;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import restx.factory.Component;
import voxxrin2.domain.User;

import javax.inject.Named;
import java.util.Map;

@Component
public class TwitterOAuthProvider extends OAuthProvider {

    private OAuthService service;

    public TwitterOAuthProvider(@Named("restx.server.baseUrl") String serverUrl,
                                TwitterSettings twitterSettings) {
        super("twitter", null);
        service = new ServiceBuilder().provider(TwitterApi.class)
                .apiKey(twitterSettings.oauthTwitterApiKey())
                .apiSecret(twitterSettings.oauthTwitterApiSecret())
                .callback("oob")
                .debugStream(System.out)
                .build();
    }

    @Override
    public String getProviderUrl() {
        return service.getAuthorizationUrl(service.getRequestToken());
    }

    @Override
    public User authenticate(Optional<Map<String, ?>> params) {

        String authToken = (String) params.get().get("oauth_token");
        String verifierToken = (String) params.get().get("oauth_verifier");

        Verifier verifier = new Verifier(verifierToken);
        org.scribe.model.Token token = new org.scribe.model.Token(authToken, "");
        org.scribe.model.Token accessToken = service.getAccessToken(token, verifier);

        Map<String, String> userInfo = Splitter.on('&').withKeyValueSeparator('=').split(accessToken.getRawResponse());

        User user = new User();
        user.setLogin(userInfo.get("user_id"));
        user.setDisplayName(userInfo.get("screen_name"));

        return user;
    }

}
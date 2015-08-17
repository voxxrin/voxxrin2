package voxxrin2.auth.providers;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.bson.types.ObjectId;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import restx.factory.Component;
import voxxrin2.auth.OAuthProvider;
import voxxrin2.auth.OAuthSettings;
import voxxrin2.domain.User;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class TwitterOAuthProvider extends OAuthProvider {

    private static final Logger logger = getLogger(TwitterOAuthProvider.class);

    private final OAuthService service;

    public TwitterOAuthProvider(@Named("restx.server.baseUrl") String serverUrl,
                                OAuthSettings twitterSettings) {
        super("twitter", null);
        service = new ServiceBuilder().provider(TwitterApi.class)
                .apiKey(twitterSettings.oauthTwitterApiKey())
                .apiSecret(twitterSettings.oauthTwitterApiSecret())
                .callback(serverUrl + "/api/auth/provider/twitter")
                .debugStream(System.out)
                .build();
        logger.info("Registered Twitter provider - key = {}, secret = {}, callback = {}",
                twitterSettings.oauthTwitterApiKey(), twitterSettings.oauthTwitterApiSecret(),
                serverUrl + "/api/auth/twitter");
    }

    @Override
    public String getProviderUrl() {
        return service.getAuthorizationUrl(service.getRequestToken());
    }

    public <T extends Map<String, ?>> User authenticate(Optional<T> params) {

        Map<String, List<String>> castedParams = castParams(params);
        Optional<String> oauth_token = extractFirstParam(castedParams.get("oauth_token"));
        Optional<String> oauth_verifier = extractFirstParam(castedParams.get("oauth_verifier"));

        Verifier verifier = new Verifier(oauth_verifier.get());
        org.scribe.model.Token token = new org.scribe.model.Token(oauth_token.get(), "");
        org.scribe.model.Token accessToken = service.getAccessToken(token, verifier);

        Map<String, String> userInfo = Splitter.on('&').withKeyValueSeparator('=').split(accessToken.getRawResponse());
        User user = new User()
                .setProviderInfo(userInfo)
                .setDisplayName(userInfo.get("screen_name"))
                .setLogin(userInfo.get("screen_name"))
                .setId(new ObjectId().toString());

        logger.info("logged user is {}", user.getDisplayName());

        return user;
    }

    @SuppressWarnings("unchecked")
    private <T extends Map<String, ?>> Map<String, List<String>> castParams(Optional<T> params) {
        return (Map<String, List<String>>) params.get();
    }

    private Optional<String> extractFirstParam(List<String> list) {
        Optional<String> param;
        if (list != null) {
            param = Optional.fromNullable(Iterables.getFirst(list, null));
        } else {
            param = Optional.absent();
        }
        return param;
    }
}
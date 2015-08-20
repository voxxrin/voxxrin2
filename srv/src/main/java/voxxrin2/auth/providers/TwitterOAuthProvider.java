package voxxrin2.auth.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import restx.factory.Component;
import restx.jackson.FrontObjectMapperFactory;
import voxxrin2.auth.OAuthProvider;
import voxxrin2.auth.OAuthSettings;
import voxxrin2.domain.User;

import javax.inject.Named;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class TwitterOAuthProvider extends OAuthProvider {

    private static final Logger logger = getLogger(TwitterOAuthProvider.class);

    private final OAuthService service;
    private final ObjectMapper mapper;

    public TwitterOAuthProvider(@Named("restx.server.baseUrl") String serverUrl,
                                OAuthSettings twitterSettings,
                                @Named(FrontObjectMapperFactory.MAPPER_NAME) ObjectMapper mapper) {
        super("twitter", null);
        this.mapper = mapper;
        this.service = new ServiceBuilder().provider(TwitterApi.class)
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

        Map<String, String> providerInfo = Maps.newHashMap(Splitter.on('&').withKeyValueSeparator('=').split(accessToken.getRawResponse()));
        providerInfo.putAll(retrieveAccountData(accessToken));

        User user = new User()
                .setProviderInfo(providerInfo)
                .setDisplayName(providerInfo.get("name"))
                .setAvatarUrl(providerInfo.get("profile_image_url_https"))
                .setLogin(providerInfo.get("screen_name"))
                .setTwitterId(providerInfo.get("screen_name"))
                .setId(new ObjectId().toString());

        logger.info("logged user is {}", user.getDisplayName());

        return user;
    }

    private Map<String, String> retrieveAccountData(Token accessToken) {

        Map<String, String> data = new HashMap<>();

        OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
        service.signRequest(accessToken, request);

        try {
            JsonNode jsonNode = mapper.readTree(request.send().getBody());

            addToProviderInfo("id", data, jsonNode);
            addToProviderInfo("screen_name", data, jsonNode);
            addToProviderInfo("name", data, jsonNode);
            addToProviderInfo("location", data, jsonNode);
            addToProviderInfo("description", data, jsonNode);
            addToProviderInfo("profile_image_url_https", data, jsonNode);

        } catch (IOException e) {
            logger.error("Unable to retrieve linked twitter account data", e);
        }

        return data;
    }

    private void addToProviderInfo(String key, Map<String, String> data, JsonNode jsonNode) {
        data.put(key, jsonNode.get(key).asText());
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
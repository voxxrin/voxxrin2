package voxxrin2.auth.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import restx.factory.Component;
import restx.jackson.FrontObjectMapperFactory;
import voxxrin2.auth.OAuthProvider;
import voxxrin2.auth.OAuthSettings;
import voxxrin2.domain.User;

import javax.inject.Named;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class FacebookOAuthProvider extends OAuthProvider {

    private static final Logger logger = getLogger(FacebookOAuthProvider.class);
    private static final String FB_CODE_DIALOG_URL = "https://www.facebook.com/v2.10/dialog/oauth";
    private static final String FB_ACCESS_TOKEN_URL = "https://graph.facebook.com/v2.10/oauth/access_token";
    private static final String VXXR_API_CALLBACK_URL = "/api/auth/provider/facebook";
    private static final String FB_GRAPH_API_URL = "https://graph.facebook.com";

    private final ObjectMapper mapper;
    private final String serverUrl;
    private final String appKey;
    private final String appSecret;

    public FacebookOAuthProvider(@Named("restx.server.baseUrl") String serverUrl,
                                 OAuthSettings oAuthSettings,
                                 @Named(FrontObjectMapperFactory.MAPPER_NAME) ObjectMapper mapper) {
        super("facebook", null);
        this.serverUrl = serverUrl;
        this.appKey = oAuthSettings.oauthFacebookAppId();
        this.appSecret = oAuthSettings.oauthFacebookAppSecret();
        this.mapper = mapper;
        logger.info("Registered Facebook provider - key = {}, secret = {}, callback = {}",
                oAuthSettings.oauthTwitterApiKey(), oAuthSettings.oauthTwitterApiSecret(),
                serverUrl + "/api/auth/provider/facebook");
    }

    @Override
    public String getProviderUrl() {
        return String.format(FB_CODE_DIALOG_URL + "?client_id=%s&redirect_uri=%s", appKey, serverUrl + VXXR_API_CALLBACK_URL);
    }

    @Override
    public <T extends Map<String, ?>> Optional<User> authenticate(Optional<T> params) throws IOException {

        Map<String, List<String>> castedParams = castParams(params);
        Optional<String> code = extractFirstParam(castedParams.get("code"));

        if (!code.isPresent()) {
            return Optional.absent();
        }

        String accessToken = getAccessToken(code.get());

        return fetchUserProfile(accessToken);
    }

    private String getAccessToken(String code) throws IOException {
        HttpRequest request = HttpRequest.get(
                String.format(FB_ACCESS_TOKEN_URL + "?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s",
                        appKey, serverUrl + VXXR_API_CALLBACK_URL, appSecret, code)
        );
        JsonNode jsonNode = mapper.readTree(request.body());
        return jsonNode.get("access_token").asText();
    }

    private Optional<User> fetchUserProfile(String accessToken) throws IOException {
        HttpRequest profileData = HttpRequest.get(String.format(FB_GRAPH_API_URL + "/me?fields=id,first_name,last_name,name,birthday&access_token=%s", accessToken));
        JsonNode jsonNode = mapper.readTree(profileData.body());
        User user = new User()
                .setLogin("facebook:" + jsonNode.get("id").asText())
                .setDisplayName(jsonNode.get("name").asText())
                .setLastName(jsonNode.get("last_name").asText())
                .setFirstName(jsonNode.get("first_name").asText());
        HttpRequest profilePictureData = HttpRequest.get(String.format(FB_GRAPH_API_URL + "/me/picture?redirect=false&width=300&access_token=%s", accessToken));
        jsonNode = mapper.readTree(profilePictureData.body());
        user.setAvatarUrl(jsonNode.get("data").get("url").asText());
        return Optional.of(user);
    }
}

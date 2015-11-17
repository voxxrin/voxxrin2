package voxxrin2.auth.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import restx.WebException;
import restx.factory.Component;
import restx.http.HttpStatus;
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
public class LinkedInOAuthProvider extends OAuthProvider {

    private static final Logger logger = getLogger(LinkedInOAuthProvider.class);

    private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address,formatted-name,picture-url)";
    private static final String LINKEDIN_OAUTH_BASE_URL = "https://www.linkedin.com/uas/oauth2";

    private final String apiSecret;
    private final String clientId;
    private final ObjectMapper mapper;
    private final String redirectUri;

    public LinkedInOAuthProvider(@Named("restx.server.baseUrl") String serverUrl,
                                 OAuthSettings oAuthSettings,
                                 @Named(FrontObjectMapperFactory.MAPPER_NAME) ObjectMapper mapper) {

        super("linkedin", LINKEDIN_OAUTH_BASE_URL + "/authorization" + "?" + "client_id=" + oAuthSettings.oauthLinkedinClientId() +
                "&response_type=code" +
                "&scope=r_basicprofile%20r_emailaddress" +
                "&state=STATE" +
                "&redirect_uri=" + encodeUrl(serverUrl + "/api/auth/provider/linkedin"));

        this.apiSecret = oAuthSettings.oauthLinkedinApiSecret();
        this.clientId = oAuthSettings.oauthLinkedinClientId();
        this.mapper = mapper;
        this.redirectUri = serverUrl + "/api/auth/provider/linkedin";

        logger.info("Registered LinkedIn provider - clientId = {}, secret = {}, callback = {}",
                oAuthSettings.oauthLinkedinClientId(), oAuthSettings.oauthLinkedinApiSecret(),
                redirectUri);
    }

    @Override
    public <T extends Map<String, ?>> User authenticate(Optional<T> params) throws IOException {

        if (!params.isPresent()) {
            throw new WebException(HttpStatus.BAD_REQUEST, "Missing parameters");
        }

        String accessToken = getAccessToken(params.get());

        HttpRequest profileRequest = HttpRequest.get(PROFILE_URL)
                .header("x-li-format", "json")
                .authorization("Bearer " + accessToken);

        int profileRequestCode = profileRequest.code();
        String profileRequestBody = profileRequest.body();

        if (profileRequestCode >= 400) {
            logger.info("Can't request profile {}: {}", profileRequestCode, profileRequestBody);
            throw new WebException(HttpStatus.havingCode(profileRequestCode), profileRequestBody);
        }

        Map<String, String> providerInfo = mapper.readValue(profileRequestBody, buildStringParamsMapType());

        String firstName = providerInfo.get("firstName");
        String lastName = providerInfo.get("lastName");
        String emailAddress = providerInfo.get("emailAddress");
        String displayName = providerInfo.get("formattedName");
        String pictureUrl = providerInfo.get("pictureUrl");

        User user = new User()
                .setProviderInfo(providerInfo)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmailAddress(emailAddress)
                .setDisplayName(displayName)
                .setAvatarUrl(pictureUrl)
                .setLogin("linkedin:" + emailAddress)
                .setId(new ObjectId().toString());

        logger.info("logged user is {}", user.getDisplayName());

        return user;
    }

    private <T extends Map<String, ?>> String getAccessToken(T params) throws IOException {

        HttpRequest request = HttpRequest.post(LINKEDIN_OAUTH_BASE_URL + "/accessToken")
                .form(ImmutableMap.builder()
                        .put("grant_type", "authorization_code")
                        .put("code", ((List<String>) params.get("code")).get(0))
                        .put("redirect_uri", redirectUri)
                        .put("client_id", clientId)
                        .put("client_secret", apiSecret)
                        .build());

        int requestCode = request.code();
        String requestBody = request.body();
        if (requestCode >= 400) {
            throw new WebException(HttpStatus.havingCode(requestCode), requestBody);
        }

        return mapper.<Map<String, String>>readValue(requestBody, buildStringParamsMapType()).get("access_token");
    }

    private MapType buildStringParamsMapType() {
        return TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, String.class);
    }
}
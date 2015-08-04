package voxxrin2.auth;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.joda.time.DateTime;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import restx.RestxRequest;
import restx.factory.Component;
import voxxrin2.domain.User;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Component
public class TwitterOAuthProvider extends OAuthProvider {

    private final TwitterSettings twitterSettings;
    private OAuthService service;

    private static final JWSHeader JWT_HEADER = new JWSHeader(JWSAlgorithm.HS256);

    public TwitterOAuthProvider(@Named("restx.server.baseUrl") String serverUrl,
                                TwitterSettings twitterSettings) {
        super("twitter", null);
        this.twitterSettings = twitterSettings;
        service = new ServiceBuilder().provider(TwitterApi.class)
                .apiKey(twitterSettings.oauthTwitterApiKey())
                .apiSecret(twitterSettings.oauthTwitterApiSecret())
                .callback("http://localhost:8080/api/auth/twitter")
                .debugStream(System.out)
                .build();
    }

    @Override
    public String getProviderUrl() {
        return service.getAuthorizationUrl(service.getRequestToken());
    }

    public <T extends Map<String, ? extends List<String>>> Token authenticate(T params, RestxRequest request) {

        Optional<String> oauth_token = extractFirstParam(params.get("oauth_token"));
        Optional<String> oauth_verifier = extractFirstParam(params.get("oauth_verifier"));

        if (!oauth_token.isPresent() || !oauth_verifier.isPresent()) {
            org.scribe.model.Token requestToken = service.getRequestToken();
            return redirectTo(service.getAuthorizationUrl(requestToken));
        } else {
            Verifier verifier = new Verifier(oauth_verifier.get());
            org.scribe.model.Token token = new org.scribe.model.Token(oauth_token.get(), "");
            org.scribe.model.Token accessToken = service.getAccessToken(token, verifier);

            Map<String, String> userInfo = Splitter.on('&').withKeyValueSeparator('=').split(accessToken.getRawResponse());
            User user = new User().setProviderInfo(userInfo).setDisplayName(userInfo.get("screen_name"));

            return createToken(request.getClientAddress(), user.getName());
        }
    }

    public Token createToken(String host, String subject) {

        JWTClaimsSet claim = new JWTClaimsSet();
        claim.setSubject(subject);
        claim.setIssuer(host);
        claim.setIssueTime(DateTime.now().toDate());
        claim.setExpirationTime(DateTime.now().plusDays(14).toDate());

        JWSSigner signer = new MACSigner(twitterSettings.oauthTwitterApiSecret());
        SignedJWT jwt = new SignedJWT(JWT_HEADER, claim);

        try {
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }

        return new Token(jwt.serialize());
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
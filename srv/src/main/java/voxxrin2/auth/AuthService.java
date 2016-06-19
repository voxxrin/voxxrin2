package voxxrin2.auth;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.samskivert.mustache.Template;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restx.RestxRequest;
import restx.common.Mustaches;
import restx.factory.Component;
import voxxrin2.domain.User;

import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;

@Component
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final JWSHeader JWT_HEADER = new JWSHeader(JWSAlgorithm.HS256);

    private final OAuthSettings oauthSettings;
    private final UserService userService;
    private final Template tmpl;

    public AuthService(OAuthSettings oauthSettings,
                       UserService userService) {
        this.oauthSettings = oauthSettings;
        this.userService = userService;
        this.tmpl = Mustaches.compile("postMessage.mustache");
    }

    public Token createToken(RestxRequest restxRequest, User user) {

        JWTClaimsSet claim = new JWTClaimsSet();
        claim.setSubject(user.getName());
        claim.setIssuer(restxRequest.getClientAddress());
        claim.setIssueTime(new Date());
        claim.setExpirationTime(new DateTime(DateTime.now().plusDays(14)).toDate());

        JWSSigner signer = new MACSigner(oauthSettings.oauthSecretsToken());
        SignedJWT jwt = new SignedJWT(JWT_HEADER, claim);

        try {
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }

        return new Token(jwt.serialize());
    }

    public String buildPostMessageHtml(User user, RestxRequest restxRequest) {

        User dbUser = findOrCreateUser(user);

        Token token = createToken(restxRequest, dbUser);

        StringWriter stringWriter = new StringWriter();
        tmpl.execute(ImmutableMap.of(
                "displayName", user.getDisplayName(),
                "avatarUrl", user.getAvatarUrl(),
                "token", token.getToken(),
                "uid", UUID.randomUUID().toString(),
                "user", dbUser), stringWriter);
        stringWriter.flush();

        return stringWriter.toString();
    }

    private User findOrCreateUser(User user) {

        Optional<User> userOptional = userService.findUserByName(user.getName());

        if (userOptional.isPresent()) {
            return updateUserWith(userOptional.get(), user);
        } else {
            return userService.registerUser(user, Optional.<String>absent());
        }
    }

    private User updateUserWith(User dbUser, User from) {
        dbUser.setLastLoginTime(DateTime.now());
        dbUser.setProviderInfo(from.getProviderInfo());
        userService.updateUser(dbUser);
        return dbUser;
    }
}
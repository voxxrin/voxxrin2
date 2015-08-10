package voxxrin2.auth;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import restx.jackson.FrontObjectMapperFactory;
import voxxrin2.domain.User;

import javax.inject.Named;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;

@Component
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final JWSHeader JWT_HEADER = new JWSHeader(JWSAlgorithm.HS256);

    private final OAuthSettings oauthSettings;
    private final ObjectMapper objectMapper;
    private final Template tmpl;

    public AuthService(OAuthSettings oauthSettings,
                       @Named(FrontObjectMapperFactory.MAPPER_NAME) ObjectMapper objectMapper) {
        this.oauthSettings = oauthSettings;
        this.objectMapper = objectMapper;
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

        Token token = createToken(restxRequest, user);

        String jsonProfile = "";
        try {
            jsonProfile = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            logger.error("Unable to encode profile for social login", e);
        }

        StringWriter stringWriter = new StringWriter();
        tmpl.execute(ImmutableMap.of("token", token.getToken(),
                "uid", UUID.randomUUID().toString(),
                "user", user,
                "profile", jsonProfile), stringWriter);
        stringWriter.flush();

        return stringWriter.toString();
    }
}
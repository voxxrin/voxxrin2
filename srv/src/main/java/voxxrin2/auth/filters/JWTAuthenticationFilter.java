package voxxrin2.auth.filters;

import com.google.common.base.Optional;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restx.*;
import restx.factory.Component;
import restx.http.HttpStatus;
import restx.security.BasicPrincipalAuthenticator;
import restx.security.RestxPrincipal;
import restx.security.RestxSession;
import voxxrin2.auth.OAuthSettings;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

@Component(priority = -195)
public class JWTAuthenticationFilter implements RestxFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private BearerRestxHandler bearerAuthorizationHandler;
    private BearerRestxHandler bearerTokenTypeHandler;

    public JWTAuthenticationFilter(OAuthSettings oauthSettings,
                                   BasicPrincipalAuthenticator authenticator) {

        String secretsToken = oauthSettings.oauthSecretsToken();

        logger.info("Registering JWT filter allowing JWT authentication (instead of cookie)");

        bearerAuthorizationHandler = new BearerRestxHandler(authenticator, secretsToken) {
            @Override
            protected String getToken(RestxRequest req) {
                return req.getHeader("Authorization").get().split(" ")[1];
            }
        };

        bearerTokenTypeHandler = new BearerRestxHandler(authenticator, secretsToken) {
            @Override
            protected String getToken(RestxRequest req) {
                return req.getHeader("access-token").get();
            }
        };
    }

    @Override
    public Optional<RestxHandlerMatch> match(RestxRequest req) {
        if (req.getHeader("access-token").isPresent()) {
            return Optional.of(new RestxHandlerMatch(
                    new StdRestxRequestMatch("*", req.getRestxPath()), bearerTokenTypeHandler));
        }

        Optional<String> authorization = req.getHeader("Authorization");
        if (authorization.isPresent() && authorization.get().toLowerCase(Locale.ENGLISH).startsWith("bearer ")) {
            return Optional.of(new RestxHandlerMatch(
                    new StdRestxRequestMatch("*", req.getRestxPath()), bearerAuthorizationHandler));
        }

        return Optional.absent();
    }

    private static abstract class BearerRestxHandler implements RestxHandler {

        private BasicPrincipalAuthenticator authenticator;
        private String secretsToken;

        public BearerRestxHandler(BasicPrincipalAuthenticator authenticator, String secretsToken) {
            this.authenticator = authenticator;
            this.secretsToken = secretsToken;
        }

        protected abstract String getToken(RestxRequest req);

        @Override
        public void handle(RestxRequestMatch match, RestxRequest req, RestxResponse resp, RestxContext ctx) throws
                IOException {
            JWTClaimsSet claimsSet;
            String token = getToken(req);
            try {
                claimsSet = decodeToken(token);
            } catch (ParseException | JOSEException e) {
                logger.error(String.format("Can't decode token: %s", token), e);
                throw new WebException(HttpStatus.BAD_REQUEST, "Invalid JWT Token - " + e.getMessage());
            }

            if (claimsSet.getExpirationTime().before(new Date())) {
                throw new WebException(HttpStatus.UNAUTHORIZED, "JWT Token expired");
            } else {
                Optional<? extends RestxPrincipal> principal = authenticator.findByName(claimsSet.getSubject());
                if (principal.isPresent()) {
                    logger.debug("JWT authenticated '{}'", principal.get().getName());

                    RestxSession.current().authenticateAs(principal.get());

                    ctx.nextHandlerMatch().handle(req, resp, ctx);
                } else {
                    throw new WebException(HttpStatus.UNAUTHORIZED, "Principal unknown");
                }
            }
        }

        private JWTClaimsSet decodeToken(String token) throws ParseException, JOSEException {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (!signedJWT.verify(new MACVerifier(secretsToken))) {
                throw new JOSEException("Signature verification failed");
            }
            return (JWTClaimsSet) signedJWT.getJWTClaimsSet();
        }
    }
}

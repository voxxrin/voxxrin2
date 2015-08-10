package voxxrin2.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;
import restx.RestxRequest;
import restx.WebException;
import restx.annotations.*;
import restx.factory.Component;
import restx.http.HttpStatus;
import restx.jackson.FrontObjectMapperFactory;
import restx.security.PermitAll;
import restx.security.RestxPrincipal;
import restx.security.RestxSession;
import voxxrin2.domain.User;
import voxxrin2.utils.Functions;

import javax.inject.Named;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@Component
@RestxResource("/auth")
public class AuthResource {

    private final AuthService authService;
    private final ObjectMapper mapper;
    private final Map<String, OAuthProvider> oAuthProviders;

    public AuthResource(List<OAuthProvider> oAuthProviders,
                        AuthService authService,
                        @Named(FrontObjectMapperFactory.MAPPER_NAME) ObjectMapper mapper) {
        this.authService = authService;
        this.mapper = mapper;
        this.oAuthProviders = Maps.uniqueIndex(oAuthProviders, Functions.OAUTH_PROVIDER_MAP_INDEXER);
    }

    @GET("/current")
    public RestxPrincipal currentUser() {
        return RestxSession.current().getPrincipal().get();
    }

    @PermitAll
    @GET("/provider/{provider}")
    @POST("/provider/{provider}")
    public String authenticate(String provider,
                               @Param(kind = Param.Kind.CONTEXT, value = "request") RestxRequest restxRequest) throws IOException {
        Optional<Map<String, ?>> params = extractParams(restxRequest);
        User user = oAuthProviders.get(provider).authenticate(params);
        return authService.buildPostMessageHtml(user, restxRequest);
    }

    @PermitAll
    @GET("/validate")
    public User validateToken() {
        Optional<User> user = AuthModule.currentUser();
        if (!user.isPresent()) {
            throw new WebException(HttpStatus.UNAUTHORIZED, "Unknown user");
        }
        return user.get();
    }

    private Optional<Map<String, ?>> extractParams(RestxRequest restxRequest) throws IOException {

        if (Strings.isNullOrEmpty(restxRequest.getContentType())) {
            return Optional.<Map<String, ?>>of(restxRequest.getQueryParams());
        } else {
            MediaType contentType = MediaType.parse(restxRequest.getContentType());
            if (contentType.is(MediaType.JSON_UTF_8)) {
                return Optional.<Map<String, ?>>of(readJsonPayload(restxRequest));
            } else if (contentType.is(MediaType.FORM_DATA)) {
                String paramString = CharStreams.toString(new InputStreamReader(restxRequest.getContentStream()));
                if (paramString.isEmpty()) {
                    return Optional.absent();
                }
                return Optional.<Map<String, ?>>fromNullable(Splitter.on('&').withKeyValueSeparator('=').split(paramString));
            } else {
                throw new WebException(HttpStatus.NOT_IMPLEMENTED, "content type not handled: " + contentType);
            }
        }
    }

    private Map<String, ?> readJsonPayload(RestxRequest restxRequest) throws IOException {
        return mapper.readValue(restxRequest.getContentStream(), new TypeReference<Map<String, String>>() {
        });
    }
}
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

    private ObjectMapper mapper;

    private Map<String, OAuthProvider> oAuthProviders;

    public AuthResource(List<OAuthProvider> oAuthProviders,
                        @Named(FrontObjectMapperFactory.MAPPER_NAME) ObjectMapper mapper) {
        this.mapper = mapper;
        this.oAuthProviders = Maps.uniqueIndex(oAuthProviders, Functions.PROVIDERS_LIST_TO_MAP_FN);
    }

    @GET("/current")
    public RestxPrincipal currentUser() {
        return RestxSession.current().getPrincipal().get();
    }

    @PermitAll
    @POST("/provider/{provider}")
    public User authenticate(String provider, @Param(kind = Param.Kind.CONTEXT, value = "request") RestxRequest restxRequest) throws IOException {
        User user = oAuthProviders.get(provider).authenticate(extractParams(restxRequest));
        RestxSession.current().authenticateAs(user);
        return user;
    }

    private Optional<Map<String, ?>> extractParams(RestxRequest restxRequest) throws IOException {

        if (Strings.isNullOrEmpty(restxRequest.getContentType())) {
            return Optional.<Map<String, ?>>of(restxRequest.getQueryParams());
        } else {
            MediaType contentType = MediaType.parse(restxRequest.getContentType());

            if (contentType.is(MediaType.JSON_UTF_8)) {
                Map<String, String> params = mapper.readValue(restxRequest.getContentStream(), new TypeReference<Map<String, String>>() {
                });
                return Optional.<Map<String, ?>>of(params);
            } else if (contentType.is(MediaType.FORM_DATA)) {
                String paramString = CharStreams.toString(new InputStreamReader(restxRequest.getContentStream()));
                if (paramString.isEmpty()) {
                    return Optional.absent();
                }
                Map<String, String> params = Splitter.on('&').withKeyValueSeparator('=').split(paramString);
                return Optional.<Map<String, ?>>fromNullable(params);
            } else {
                throw new WebException(HttpStatus.NOT_IMPLEMENTED, "content type not handled: " + contentType);
            }
        }
    }

}

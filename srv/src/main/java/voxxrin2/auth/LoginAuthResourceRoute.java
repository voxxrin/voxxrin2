package voxxrin2.auth;

import com.google.common.base.Optional;
import restx.*;
import restx.common.Types;
import restx.entity.EntityRequestBodyReader;
import restx.entity.EntityRequestBodyReaderRegistry;
import restx.entity.EntityResponseWriter;
import restx.entity.EntityResponseWriterRegistry;
import restx.factory.Component;
import restx.http.HttpStatus;
import voxxrin2.domain.User;

import java.io.IOException;
import java.util.Map;

@Component
public class LoginAuthResourceRoute extends StdRoute {

    private final EntityRequestBodyReader<Map<String, String>> entityRequestBodyReader;
    private final EntityResponseWriter<User> entityResponseWriter;
    private final UserService userService;
    private final AuthService authService;

    public LoginAuthResourceRoute(UserService userService,
                                  AuthService authService,
                                  EntityRequestBodyReaderRegistry readerRegistry,
                                  EntityResponseWriterRegistry writerRegistry) {
        super("LoginAuthResourceRoute", new StdRestxRequestMatcher("POST", "/auth/login"));
        this.userService = userService;
        this.authService = authService;

        entityRequestBodyReader = readerRegistry.build(Types.newParameterizedType(Map.class, String.class, String.class), Optional.<String>absent());
        entityResponseWriter = writerRegistry.build(User.class, Optional.<String>absent());
    }

    @Override
    public void handle(RestxRequestMatch match, RestxRequest req, RestxResponse resp, RestxContext ctx) throws
            IOException {
        Map<String, String> userMap = entityRequestBodyReader.readBody(req, ctx);

        User user = login(userMap);

        Token token = authService.createToken(req, user);
        resp.setHeader("token-type", "Bearer");
        resp.setHeader("access-token", token.getToken());

        entityResponseWriter.sendResponse(HttpStatus.OK, user, req, resp, ctx);

    }

    private User login(Map<String, String> userMap) {
        Optional<User> dbUser = userService.findAndCheckCredentials(userMap.get("login"), userMap.get("password"));
        if (dbUser.isPresent()) {
            return dbUser.get();
        } else {
            throw new WebException(HttpStatus.UNAUTHORIZED, "Invalid login");
        }
    }
}

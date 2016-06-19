package voxxrin2.auth;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import restx.*;
import restx.admin.AdminModule;
import restx.common.MoreResources;
import restx.factory.Module;
import restx.factory.Provides;
import restx.security.*;
import voxxrin2.domain.User;

import javax.inject.Named;
import java.io.IOException;

@Module
public class AuthModule {

    public static Optional<User> currentUser() {
        Optional<RestxSession> restxSessionOptional = Optional.fromNullable(RestxSession.current());
        if (restxSessionOptional.isPresent()) {
            return castUser(restxSessionOptional);
        }
        return Optional.absent();
    }

    @SuppressWarnings("unchecked")
    private static Optional<User> castUser(Optional<RestxSession> restxSessionOptional) {
        return (Optional<User>) restxSessionOptional.get().<User>getPrincipal();
    }

    @Provides
    public BasicPrincipalAuthenticator basicPrincipalAuthenticator(UserService userService,
                                                                   SecuritySettings securitySettings) {
        return new StdBasicPrincipalAuthenticator(userService, securitySettings);
    }

    @Provides
    @Named("restx.activation::restx.security.RestxSessionCookieFilter::RestxSessionCookieFilter")
    public String disableCookieAuthentication() {
        return "false";
    }

    @Provides
    @Named("restx.activation::restx.security.RestxSessionBareFilter::RestxSessionBareFilter")
    public String enableBareFilter() {
        return "true";
    }

    @Provides
    public CredentialsStrategy credentialsStrategy() {
        return new BCryptCredentialsStrategy();
    }

    @Named("restx.admin.password")
    @Provides
    public String restxAdminPassword() {
        return "voxxrin2015";
    }

    @Provides
    public User defaultAdminUser() {
        User user = new User();
        user.setId(null);
        user.setLogin("admin");
        user.setRoles(Sets.newHashSet(AdminModule.RESTX_ADMIN_ROLE));
        return user;
    }

    @Provides
    public RestxRoute loginJs() {
        return new StdRoute("loginJs", new StdRestxRequestMatcher("GET", "/@/ui/js/login.js")) {
            @Override
            public void handle(RestxRequestMatch match, RestxRequest req, RestxResponse resp, RestxContext ctx) throws IOException {
                resp.setContentType("application/javascript");
                Resources.asByteSource(MoreResources.getResource("restx_overrides/login.js", true))
                        .copyTo(resp.getOutputStream());
            }
        };
    }

    @Provides
    public RestxRoute adminJs() {
        return new StdRoute("adminJs", new StdRestxRequestMatcher("GET", "/@/ui/js/admin.js")) {
            @Override
            public void handle(RestxRequestMatch match, RestxRequest req, RestxResponse resp, RestxContext ctx) throws IOException {
                resp.setContentType("application/javascript");
                Resources.asByteSource(MoreResources.getResource("restx_overrides/admin.js", true))
                        .copyTo(resp.getOutputStream());
            }
        };
    }
}

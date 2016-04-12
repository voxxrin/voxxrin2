package crawlers.web;

import com.google.common.collect.Sets;
import restx.admin.AdminModule;
import restx.factory.Module;
import restx.factory.Provides;
import restx.security.*;
import voxxrin2.domain.User;

@Module
public class AuthModule {

    @Provides
    public BasicPrincipalAuthenticator basicPrincipalAuthenticator(UserService userService,
                                                                   SecuritySettings securitySettings) {
        return new StdBasicPrincipalAuthenticator(userService, securitySettings);
    }

    @Provides
    public CredentialsStrategy credentialsStrategy() {
        return new BCryptCredentialsStrategy();
    }

    @Provides
    public User defaultAdminUser() {
        User user = new User();
        user.setId(null);
        user.setLogin("admin");
        user.setRoles(Sets.newHashSet(AdminModule.RESTX_ADMIN_ROLE));
        return user;
    }
}

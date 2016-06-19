package voxxrin2.auth;

import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.jongo.JongoUserRepository;
import restx.security.CredentialsStrategy;
import voxxrin2.domain.User;

import javax.inject.Named;

@Component
public class DBUserRepository extends JongoUserRepository<User> {

    private static final UserRefStrategy<User> USER_REF_STRATEGY = new UserRefStrategy<User>() {
        @Override
        public String getNameProperty() {
            return "login";
        }

        @Override
        public String getUserRef(User user) {
            return user.getId();
        }

        @Override
        public Object toId(String userRef) {
            return userRef;
        }
    };

    public DBUserRepository(@Named("users") JongoCollection users,
                            @Named("credentials") JongoCollection usersCredentials,
                            CredentialsStrategy credentialsStrategy,
                            User defaultAdminUser) {
        super(users, usersCredentials, USER_REF_STRATEGY, credentialsStrategy, User.class, defaultAdminUser);
    }
}

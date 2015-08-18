package voxxrin2.auth;

import com.google.common.base.Optional;
import restx.factory.Component;
import restx.security.CredentialsStrategy;
import restx.security.StdUserService;
import voxxrin2.auth.db.DBUserRepository;
import voxxrin2.domain.User;

import javax.inject.Named;

@Component
public class UserService extends StdUserService<User> {

    private final DBUserRepository repository;

    public UserService(DBUserRepository repository,
                       CredentialsStrategy checker,
                       @Named("restx.admin.passwordHash") String defaultAdminPasswordHash) {
        super(repository, checker, defaultAdminPasswordHash);
        this.repository = repository;
    }

    public User registerUser(User user, Optional<String> password) {

        User dbUser = repository.createUser(user);

        if (password.isPresent()) {
            repository.setCredentials(dbUser.getLogin(), password.get());
        }

        return dbUser;
    }

    public User updateUser(User user) {
        return repository.updateUser(user);
    }
}

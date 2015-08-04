package voxxrin2.domain;

import com.google.common.collect.ImmutableSet;
import restx.security.RestxPrincipal;

import java.util.Map;

public class User implements RestxPrincipal {

    private String login;

    private String twitterId;

    private String lastName;

    private String firstName;

    private String displayName;

    private Map<String, String> providerInfo;

    @Override
    public String getName() {
        return login;
    }

    public String getLogin() {
        return login;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public User setLogin(final String login) {
        this.login = login;
        return this;
    }

    public User setTwitterId(final String twitterId) {
        this.twitterId = twitterId;
        return this;
    }

    public User setLastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public User setFirstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public User setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Map<String, String> getProviderInfo() {
        return providerInfo;
    }

    public User setProviderInfo(final Map<String, String> providerInfo) {
        this.providerInfo = providerInfo;
        return this;
    }

    @Override
    public ImmutableSet<String> getPrincipalRoles() {
        return null;
    }
}

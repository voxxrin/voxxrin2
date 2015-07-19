package voxxrin2.domain;

import voxxrin2.domain.technical.Referenceable;

public class Speaker extends Referenceable {

    private String firstName;

    private String lastName;

    private String company;

    private String avatarUrl;

    private String twitterId;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCompany() {
        return company;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public Speaker setTwitterId(final String twitterId) {
        this.twitterId = twitterId;
        return this;
    }

    public Speaker setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public Speaker setCompany(final String company) {
        this.company = company;
        return this;
    }

    public Speaker setLastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Speaker setFirstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }
}

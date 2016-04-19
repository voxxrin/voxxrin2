package voxxrin2.domain;

import voxxrin2.domain.technical.Referenceable;

public class Speaker extends Referenceable {

    private String name;

    private String firstName;

    private String lastName;

    private String company;

    private String bio;

    private String avatarUrl;

    private String twitterId;

    private String uuid;

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCompany() {
        return company;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public String getUuid() {
        return uuid;
    }

    public Speaker setName(final String name) {
        this.name = name;
        return this;
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

    public Speaker setBio(final String bio) {
        this.bio = bio;
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

    public Speaker setUuid(final String uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public String toString() {
        return "Speaker{" +
                "name='" + name + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", company='" + company + '\'' +
                ", bio='" + bio + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", twitterId='" + twitterId + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}

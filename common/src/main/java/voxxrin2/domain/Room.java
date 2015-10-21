package voxxrin2.domain;

import voxxrin2.domain.technical.Referenceable;

public class Room extends Referenceable {

    private String name;

    private String fullName;

    public String getName() {
        return name;
    }

    public Room setName(final String name) {
        this.name = name;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public Room setFullName(final String fullName) {
        this.fullName = fullName;
        return this;
    }
}

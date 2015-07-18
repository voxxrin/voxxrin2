package voxxrin2.domain;

import org.jongo.marshall.jackson.oid.Id;

public class Referenceable {

    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

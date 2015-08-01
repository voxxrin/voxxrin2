package voxxrin2.domain.technical;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.joda.time.DateTime;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class Referenceable {

    @Id
    @ObjectId
    private String key;

    private DateTime creationDate;

    private DateTime updateDate;

    public String getKey() {
        return key;
    }

    public Referenceable setKey(String key) {
        this.key = key;
        return this;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public DateTime getUpdateDate() {
        return updateDate;
    }

    public Referenceable setUpdateDate(final DateTime updateDate) {
        this.updateDate = updateDate;
        return this;
    }

    public Referenceable setCreationDate(final DateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }
}

package voxxrin2.domain.technical;

import voxxrin2.domain.Type;

public class ElementURI {

    private Type type;

    private String key;

    private ElementURI(Type type, String key) {
        this.type = type;
        this.key = key;
    }

    public static ElementURI of(Type type, String key) {
        return new ElementURI(type, key);
    }

    public Type getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public ElementURI setType(final Type type) {
        this.type = type;
        return this;
    }

    public ElementURI setKey(final String key) {
        this.key = key;
        return this;
    }

    @Override
    public String toString() {
        return String.format("ref://%s/%s", type.name(), key);
    }
}

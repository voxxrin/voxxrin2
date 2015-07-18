package voxxrin2.persistence;

import restx.jongo.JongoCollection;
import voxxrin2.domain.Referenceable;

public abstract class DataService<T extends Referenceable> {

    private final JongoCollection collection;
    private final Class<T> clazz;

    public DataService(JongoCollection collection, Class<T> clazz) {
        this.collection = collection;
        this.clazz = clazz;
    }

    public Iterable<T> findAll() {
        return collection.get().find().as(clazz);
    }

    public Iterable<T> find(String query) {
        return collection.get().find(query).as(clazz);
    }

    public T save(T entity) {
        beforeEntitySave(entity);
        collection.get().save(entity);
        return entity;
    }

    protected abstract void beforeEntitySave(T entity);

}

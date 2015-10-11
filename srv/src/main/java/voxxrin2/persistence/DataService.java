package voxxrin2.persistence;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import restx.jongo.JongoCollection;
import voxxrin2.domain.technical.Referenceable;

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

    public Iterable<T> findAll(String query, Object... params) {
        return collection.get().find(query, params).as(clazz);
    }

    public Iterable<T> findAllAndSort(String sorting) {
        return collection.get().find().sort(sorting).as(clazz);
    }

    public Iterable<T> findAllAndSort(String query, String sorting, Object... params) {
        return collection.get().find(query, params).sort(sorting).as(clazz);
    }

    public T find(String query, Object... params) {
        return collection.get().findOne(query, params).as(clazz);
    }

    public T save(T entity) {
        beforeEntitySave(entity);
        collection.get().save(entity);
        return entity;
    }

    protected void beforeEntitySave(T entity) {
        DateTime now = DateTime.now();
        if (entity.getKey() == null) {
            entity.setCreationDate(now);
            entity.setKey(new ObjectId().toString());
        }
        entity.setUpdateDate(now);
    }

}

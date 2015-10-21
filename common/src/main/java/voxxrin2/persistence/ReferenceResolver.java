package voxxrin2.persistence;

import org.bson.types.ObjectId;
import restx.factory.Component;
import restx.factory.Factory;
import restx.factory.Name;
import restx.jongo.JongoCollection;
import voxxrin2.domain.technical.ElementURI;
import voxxrin2.domain.technical.Referenceable;

@Component
public class ReferenceResolver {

    private Factory factory;

    public ReferenceResolver(Factory factory) {
        this.factory = factory;
    }

    public <T extends Referenceable> T resolve(ElementURI uri) {

        JongoCollection collection = factory.getComponent(Name.of(JongoCollection.class, uri.getType().name()));

        return (T) collection.get().findOne("{ _id: # }", new ObjectId(uri.getKey())).as(Referenceable.class);
    }
}

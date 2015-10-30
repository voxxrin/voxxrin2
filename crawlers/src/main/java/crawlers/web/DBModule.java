package crawlers.web;

import com.google.common.base.Optional;
import com.mongodb.MongoClient;
import org.slf4j.Logger;
import restx.factory.AutoStartable;
import restx.factory.Module;
import restx.factory.Provides;
import restx.mongo.MongoModule;

import javax.inject.Named;

import static org.slf4j.LoggerFactory.getLogger;

@Module
public class DBModule {

    private static final Logger logger = getLogger(DBModule.class);

    @Provides
    @Named("mongo.db")
    public String dbName() {
        return "crawlers";
    }

    @Provides
    public AutoStartable mongoConnectionLogger(final @Named("restx.server.id") Optional<String> serverId,
                                               final @Named("mongo.db") Optional<String> dbName,
                                               final @Named(MongoModule.MONGO_CLIENT_NAME) MongoClient client) {
        return new AutoStartable() {
            @Override
            public void start() {
                logger.info("{} - connected to Mongo {} @ {}", serverId.or("-"), dbName.or("-"), client.getAllAddress());
            }
        };
    }

}

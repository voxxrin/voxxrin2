package crawlers.configuration;

import restx.factory.Component;
import restx.jongo.JongoCollection;

import javax.inject.Named;

@Component
public class CrawlingConfigurationManager {

    private final JongoCollection configurations;

    public CrawlingConfigurationManager(@Named("configurations") JongoCollection configurations) {
        this.configurations = configurations;
    }

    public CrawlingConfiguration findConfiguration(String eventId) {
        return configurations.get().findOne("{ _id: # }", eventId).as(CrawlingConfiguration.class);
    }
}

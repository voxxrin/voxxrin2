package voxxrin2.crawlers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import java.io.IOException;
import java.util.List;

public abstract class AbstractHttpCrawler {

    {
        configureMapper(MAPPER);
    }

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    protected abstract CrawlingResult crawl() throws IOException;

    protected void configureMapper(ObjectMapper mapper) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JodaModule());
    }

    protected CollectionType buildCollectionType(Class<?> clazz) {
        return MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
    }
}

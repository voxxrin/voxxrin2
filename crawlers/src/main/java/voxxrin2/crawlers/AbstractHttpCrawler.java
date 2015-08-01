package voxxrin2.crawlers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class AbstractHttpCrawler {

    {
        configureMapper(MAPPER);
    }

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    protected abstract CrawlingResult crawl() throws IOException;

    protected void configureMapper(ObjectMapper mapper) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}

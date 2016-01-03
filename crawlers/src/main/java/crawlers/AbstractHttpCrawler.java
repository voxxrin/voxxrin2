package crawlers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableList;
import crawlers.configuration.CrawlingConfiguration;
import org.joda.time.DateTime;
import voxxrin2.domain.Presentation;

import java.io.IOException;
import java.util.List;

public abstract class AbstractHttpCrawler {

    {
        configureMapper(MAPPER);
    }

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    private final String id;
    private final List<String> roles;

    public AbstractHttpCrawler(String id, List<String> roles) {
        this.id = id;
        this.roles = concatWithAdminRoles(roles);
    }

    private List<String> concatWithAdminRoles(List<String> roles) {
        return ImmutableList.<String>builder()
                .addAll(roles)
                .add("ADM")
                .add("restx-admin")
                .build();
    }

    public abstract CrawlingResult crawl(CrawlingConfiguration configuration) throws IOException;

    public CrawlingResult setup(CrawlingResult result, CrawlingConfiguration configuration) {
        if (configuration.getEventName() != null) {
            result.getEvent().setName(configuration.getEventName());
        }
        result.getEvent().setEventId(configuration.getEventId());
        return result;
    }

    protected void configureMapper(ObjectMapper mapper) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JodaModule());
    }

    protected CollectionType buildCollectionType(Class<?> clazz) {
        return MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
    }

    public String getId() {
        return id;
    }

    public List<String> getRoles() {
        return roles;
    }

    protected void setEventTemporalLimits(CrawlingResult crawlingResult) {

        DateTime from = null;
        DateTime to = null;

        for (Presentation presentation : crawlingResult.getPresentations()) {

            if (from == null) {
                from = presentation.getFrom();
            }
            if (to == null) {
                to = presentation.getTo();
            }
            if (presentation.getFrom().isBefore(from)) {
                from = presentation.getFrom();
            }
            if (presentation.getTo().isAfter(from)) {
                to = presentation.getTo();
            }
        }

        crawlingResult.getEvent()
                .setFrom(from)
                .setTo(to);
    }
}

package voxxrin2.crawlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.github.kevinsawicki.http.HttpRequest;
import org.jongo.marshall.jackson.oid.ObjectIdDeserializer;
import org.jongo.marshall.jackson.oid.ObjectIdSerializer;
import org.slf4j.Logger;
import restx.jackson.FixedPrecisionDeserializer;
import restx.jackson.FixedPrecisionSerializer;
import voxxrin2.domain.*;
import voxxrin2.domain.technical.Referenceable;
import voxxrin2.serialization.ReferenceSerializer;

import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

public class HttpDataFiller {

    private static final Logger logger = getLogger(HttpDataFiller.class);
    private static final ObjectMapper MAPPER = buildObjectMapper();

    private static final String EVENTS_URL = "/events";
    private static final String DAYS_URL = "/days";
    private static final String ROOMS_URL = "/rooms";
    private static final String SPEAKERS_URL = "/speakers";
    private static final String PRESENTATIONS_URL = "/presentations";
    private final String baseUrl;

    public HttpDataFiller(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void fill(CrawlingResult result) throws JsonProcessingException {

        String crawlId = generateCrawlId(result.getEvent());

        send(EVENTS_URL, result.getEvent(), crawlId);

        for (Day day : result.getDays()) {
            send(DAYS_URL, day, crawlId);
        }

        for (Room room : result.getRooms()) {
            send(ROOMS_URL, room, crawlId);
        }

        for (Speaker speaker : result.getSpeakers()) {
            send(SPEAKERS_URL, speaker, crawlId);
        }

        for (Presentation presentation : result.getPresentations()) {
            send(PRESENTATIONS_URL, presentation, crawlId);
        }
    }

    private String generateCrawlId(Event event) {
        return String.format("%s-%s", event.getName().replaceAll("\\s", ""), UUID.randomUUID().toString());
    }

    private <T extends Referenceable> void send(String url, T entity, String crawlId) throws JsonProcessingException {
        entity.setCrawlId(crawlId);
        int code = HttpRequest
                .post(baseUrl + url).acceptJson()
                .basic("admin", System.getProperty("voxxrin.http.basic.pwd"))
                .send(MAPPER.writeValueAsString(entity)).code();
        logger.info("Request sent to {} - Response code {}", baseUrl + url, code);
    }

    private static ObjectMapper buildObjectMapper() {

        return new ObjectMapper()
                .registerModule(new JodaModule())
                .registerModule(new GuavaModule())
                .registerModule(new SimpleModule().addSerializer(new ReferenceSerializer()))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.EAGER_DESERIALIZER_FETCH)
                .disable(SerializationFeature.EAGER_SERIALIZER_FETCH)
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
                    @Override
                    public Object findSerializer(Annotated am) {
                        Object serializer = super.findSerializer(am);
                        if (ObjectIdSerializer.class == serializer
                                || FixedPrecisionSerializer.class == serializer) {
                            return null;
                        }
                        return serializer;
                    }

                    @Override
                    public Class<? extends JsonDeserializer<?>> findDeserializer(Annotated a) {
                        Class<? extends JsonDeserializer<?>> deserializer = super.findDeserializer(a);
                        if (ObjectIdDeserializer.class == deserializer
                                || FixedPrecisionDeserializer.class == deserializer) {
                            return null;
                        }
                        return deserializer;
                    }
                });
    }
}

package voxxrin2.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import voxxrin2.domain.technical.Reference;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.slf4j.LoggerFactory.getLogger;

public class ReferenceDeserializer extends JsonDeserializer<Reference> {

    private static final Logger logger = getLogger(ReferenceDeserializer.class);

    @Override
    public Reference deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        String uri = jp.getValueAsString();
        try {
            return Reference.of(ElementURIParser.parse(uri));
        } catch (URISyntaxException e) {
            logger.error("Unable to unmarshall provided uri " + uri, e);
        }

        return null;
    }
}

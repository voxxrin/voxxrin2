package voxxrin2.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Optional;
import restx.jackson.Views;
import voxxrin2.domain.technical.Reference;

import java.io.IOException;

public class ReferenceSerializer extends StdSerializer<Reference> {

    public ReferenceSerializer() {
        super(Reference.class);
    }

    @Override
    public void serialize(Reference value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        Optional optional = value.maybeGet();

        if (optional.isPresent()) {
            if (provider.getActiveView() != null && provider.getActiveView().isAssignableFrom(Views.Private.class)) {
                jgen.writeString(value.getUri().toString());
            } else {
                provider.defaultSerializeValue(optional.get(), jgen);
            }
        } else {
            jgen.writeString(value.getUri().toString());
        }
    }
}

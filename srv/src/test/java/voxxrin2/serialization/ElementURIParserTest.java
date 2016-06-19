package voxxrin2.serialization;

import org.junit.Test;
import voxxrin2.domain.technical.ElementURI;
import voxxrin2.domain.Type;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class ElementURIParserTest {

    @Test
    public void should_parse_properly_well_formed_uri() throws URISyntaxException {

        ElementURI uri = ElementURIParser.parse("ref://event/BDXIO-2015");

        assertThat(uri).isNotNull();
        assertThat(uri.getType()).isEqualTo(Type.event);
        assertThat(uri.getKey()).isEqualTo("BDXIO-2015");
    }

}
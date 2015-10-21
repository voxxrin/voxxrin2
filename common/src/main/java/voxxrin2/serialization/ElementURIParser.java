package voxxrin2.serialization;

import voxxrin2.domain.Type;
import voxxrin2.domain.technical.ElementURI;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElementURIParser {

    private static final Pattern URI_PATTERN = Pattern.compile("ref://([a-z]+)/([^?]+)(?:\\?.+)?");

    public static ElementURI parse(String rawURI) throws URISyntaxException {

        // Check URI syntax
        Matcher matcher = URI_PATTERN.matcher(rawURI);
        if (!matcher.matches()) {
            throw new URISyntaxException(rawURI, "Syntax error ");
        }

        // Build the URI
        return ElementURI.of(Type.valueOf(matcher.group(1)), matcher.group(2));
    }
}

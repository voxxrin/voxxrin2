package voxxrin2.utils;

import voxxrin2.domain.Presentation;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.Reference;

import java.util.regex.Pattern;

/**
 * Describes a "business" presentation REF : ie a ref linked to a presentation which is built on
 * external ref provided by crawled data. This ref allows to ALWAYS target the same presentation
 * accross multiple data crawling (contrary to a plain old TECHNICAL ref, like OID for instance).
 */

public class PresentationRef {

    public static final Pattern PATTERN = Pattern.compile("([^:]+):(.*)");

    public static String getPresentationRef(String presentationId) {
        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        return buildPresentationBusinessRef(presentation);
    }

    public static String buildPresentationBusinessRef(Presentation presentation) {
        return String.format("%s:%s", presentation.getEventId(), presentation.getExternalId());
    }
}

package voxxrin2.utils;

import voxxrin2.domain.Presentation;
import voxxrin2.domain.Type;
import voxxrin2.domain.technical.Reference;

public class Utils {

    public static String getPresentationRef(String presentationId) {
        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        return buildPresentationBusinessRef(presentation);
    }

    public static String buildPresentationBusinessRef(Presentation presentation) {
        return String.format("%s:%s", presentation.getEventId(), presentation.getExternalId());
    }
}

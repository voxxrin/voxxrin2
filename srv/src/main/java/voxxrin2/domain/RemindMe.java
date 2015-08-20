package voxxrin2.domain;

import voxxrin2.domain.technical.Reference;

public class RemindMe {

    private Reference<Presentation> presentation;

    private String userId;

    public Reference<Presentation> getPresentation() {
        return presentation;
    }

    public String getUserId() {
        return userId;
    }

    public RemindMe setPresentation(final Reference<Presentation> presentation) {
        this.presentation = presentation;
        return this;
    }

    public RemindMe setUserId(final String userId) {
        this.userId = userId;
        return this;
    }
}

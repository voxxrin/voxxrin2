package voxxrin2.domain;

import voxxrin2.domain.technical.Reference;

public class RemindMe {

    private Reference<Presentation> presentation;

    private String userId;

    private String email;

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

    public String getEmail() {
        return email;
    }

    public RemindMe setEmail(final String email) {
        this.email = email;
        return this;
    }
}

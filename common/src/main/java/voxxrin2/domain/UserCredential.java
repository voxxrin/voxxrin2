package voxxrin2.domain;

import org.jongo.marshall.jackson.oid.ObjectId;

public class UserCredential {

    @ObjectId
    private String userId;

    private String pwd;

    public String getUserId() {
        return userId;
    }

    public String getPwd() {
        return pwd;
    }

    public UserCredential setUserId(final String userId) {
        this.userId = userId;
        return this;
    }

    public UserCredential setPwd(final String pwd) {
        this.pwd = pwd;
        return this;
    }
}

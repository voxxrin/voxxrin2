package voxxrin2.domain.technical;

public class PushStatus {

    private boolean sent;

    private int code;

    private String payload;

    public PushStatus(boolean sent, int code, String payload) {
        this.sent = sent;
        this.code = code;
        this.payload = payload;
    }

    public boolean isSent() {
        return sent;
    }

    public int getCode() {
        return code;
    }

    public String getPayload() {
        return payload;
    }

    public static PushStatus of(int code, String body) {
        return new PushStatus(code == 202, code, body);
    }
}

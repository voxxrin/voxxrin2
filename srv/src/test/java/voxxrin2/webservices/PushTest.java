package voxxrin2.webservices;

import org.joda.time.DateTime;
import org.junit.Test;
import restx.factory.Factory;
import restx.factory.Name;
import voxxrin2.domain.technical.PushStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class PushTest {

    @Test
    public void shouldPropertlySendPushNotification() throws Exception {

        Push push = Factory.getInstance().getComponent(Name.of(Push.class));
        PushStatus status = push.sendMsg("Test", "DEV-822f12af-8a7a-471f-be17-e79507d3fddf", DateTime.now());

        assertThat(status.isSent()).isTrue();
    }
}
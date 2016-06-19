package voxxrin2.crawlers;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

public class DateTimeParsingTest {

    @Test
    public void should_parse_properly_datetime() throws Exception {

        String dateTimeString = "2015-10-16 09:00:00.0";
        DateTime dateTime = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.S").parseDateTime(dateTimeString);

        System.out.print(dateTime);
    }
}

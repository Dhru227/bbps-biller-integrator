package bharat.connect.biller.common;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CommonUtils {
    private static final DateTimeFormatter formatterUTC = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            .withZone(ZoneOffset.UTC);

    public static String getFormattedCurrentTimestamp() {
        return getFormattedTimestamp(new Date());
    }

    public static String getFormattedTimestamp(Date date) {
        return formatterUTC.format(date.toInstant());
    }
}

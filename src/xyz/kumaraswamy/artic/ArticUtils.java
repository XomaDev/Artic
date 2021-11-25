package xyz.kumaraswamy.artic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class ArticUtils {
    public static String TIME_FORMAT = "yyyy-MMM-dd HH:mm:ss";

    public static long getUniversalTime() throws ParseException {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return new SimpleDateFormat(TIME_FORMAT)
                .parse(simpleDateFormat.format(
                        new Date())
                ).getTime();
    }

    public static String makeUniqueIdentifier() {
        final char[] chars = new char[] {
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

        final Random random = new Random();
        final int maxLen = chars.length;
        final StringBuilder identifier = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            identifier.append(chars[random.nextInt(maxLen)]);
        }
        return identifier.toString();
    }
}

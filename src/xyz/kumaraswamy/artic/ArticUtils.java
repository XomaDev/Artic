package xyz.kumaraswamy.artic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import static android.content.Context.ALARM_SERVICE;

public class ArticUtils {

    /**
     * The Time Format
     */

    public static String TIME_FORMAT = "yyyy-MMM-dd HH:mm:ss";

    /**
     * Gets the UTC
     *
     * @return UTC time in Millis
     */

    public static long getUniversalTime() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return new SimpleDateFormat(TIME_FORMAT)
                .parse(simpleDateFormat.format(
                        new Date())
                ).getTime();
    }

    /**
     * Generates a unique identifier string
     * for our message
     */

    public static String makeUniqueIdentifier() {
        final char[] chars = new char[]{
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

        final Random random = new Random();
        final int maxLen = chars.length;
        final StringBuilder identifier = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            identifier.append(chars[
                    random.nextInt(maxLen)]);
        }
        return identifier.toString();
    }

    /**
     * Initializes next alarm service using broadcast receiver
     *
     * @param context Application Context
     * @param time    latency of the alarm
     */

    public static void setNextAlarm(Context context, long time) {
        final long triggerAtMillis =
                System.currentTimeMillis() + time;

        Intent intent = new Intent(context, ArticAlarm.class);

        // we will update if any alarm is already running
        PendingIntent pd = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        // initialize the alarm
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pd);
    }
}

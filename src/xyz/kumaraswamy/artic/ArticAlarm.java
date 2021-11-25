package xyz.kumaraswamy.artic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ArticAlarm extends BroadcastReceiver {

    /**
     * Log tag for the class
     */

    private static final String LOG_TAG = "ArticAlarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "Artic Alarm Received");
        // initialize the artic alarm
        ArticService.initialize(context);
    }
}

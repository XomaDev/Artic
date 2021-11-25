package xyz.kumaraswamy.artic;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ArticService extends JobService {

    /**
     * The Job Id of the service
     */

    public static final int JOB = 123;

    /**
     * The log tag for the class
     */

    private static final String LOG_TAG = "ArticService";

    /**
     * Initializes the Artic Service
     * @param context Application Context
     */

    public static int initialize(Context context) {
        ComponentName componentName = new ComponentName(context, ArticService.class);

        JobInfo.Builder jobInfo = new JobInfo.Builder(JOB, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setOverrideDeadline(1000);

        JobScheduler jobScheduler = (JobScheduler)
                context.getSystemService(JOB_SCHEDULER_SERVICE);
        return jobScheduler.schedule(jobInfo.build());
    }

    /**
     * The Job service is initialized here
     */

    @Override
    public boolean onStartJob(JobParameters parms) {
        Log.d(LOG_TAG, "Artic Service Initialized");

        // do activities on background thread
        // to keep it smooth as possible

        new Thread(new Runnable() {
            @Override
            public void run() {
                // do our work here
                runJobInBackground();
            }
        }).start();

        return true;
    }

    private void runJobInBackground() {
        // important to set the context so firebase
        // can initialize
        Artic.initialize(this);

        SharedPreferences preferences = Artic.articSharedPreferences(this);

        Artic artic = new Artic(this);
        artic.listen(preferences.getString("topic", ""));
    }

    /**
     * Say goodbye to our JobService
     */

    @Override
    public boolean onStopJob(JobParameters parms) {
        Log.d(LOG_TAG, "Stopping Job Service");

        // attempt to restart the service through broadcast,
        // so we can run again

        ArticService.initialize(this);
        return false;
    }
}

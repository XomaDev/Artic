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

    public static final int JOB = 123;

    public static int initialize(Context context) {
        ComponentName componentName = new ComponentName(context, ArticService.class);

        JobInfo.Builder jobInfo = new JobInfo.Builder(123, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setOverrideDeadline(1000);

        JobScheduler jobScheduler = (JobScheduler)
                context.getSystemService(JOB_SCHEDULER_SERVICE);
        return jobScheduler.schedule(jobInfo.build());
    }

    private static final String TAG = "ArticService";

    @Override
    public boolean onStartJob(JobParameters parms) {
        Log.d(TAG, "Artic Service Initialized");

        new Thread(new Runnable() {
            @Override
            public void run() {
                runJobInBackground();
            }
        }).start();

        return true;
    }

    private void runJobInBackground() {
        Artic.initialize(this);
        SharedPreferences preferences = Artic.articSharedPreferences(this);
        Artic artic = new Artic(this);
        artic.listen(preferences.getString("topic", ""));
    }

    @Override
    public boolean onStopJob(JobParameters parms) {
        return false;
    }
}

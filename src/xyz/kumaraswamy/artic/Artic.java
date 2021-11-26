package xyz.kumaraswamy.artic;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.firebase.client.Firebase;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.kumaraswamy.artic.database.listeners.ArticChildListener;
import xyz.kumaraswamy.artic.database.ArticDatabase;
import xyz.kumaraswamy.artic.database.listeners.AuthListener;
import xyz.kumaraswamy.artic.database.listeners.DataChangedInterface;

import java.text.ParseException;

import static xyz.kumaraswamy.artic.ArticUtils.getUniversalTime;
import static xyz.kumaraswamy.artic.ArticUtils.makeUniqueIdentifier;

@SuppressWarnings("unused")
public class Artic {

    /**
     * the log tag used for debugging
     */

    public static final String NAME = "Artic";

    /**
     * simple name for the bucket
     */

    public static final String SIMPLE_NAME = NAME.toLowerCase();

    /*
     an instance of artic database which
     will manage data
    */

    private final ArticDatabase database;

    private final Context context;
    private final String token;
    /**
     * Firebase component
     */

    private Firebase firebase;
    private final String url;

    /**
     * Artic DataChange listener that reports back
     * when any data is changed
     */

    private final ArticChildListener articChildListener;

    /**
     * Artic notification manager that handles the data change events
     */

    private final ArticNotification articNotification;

    static Artic getInstance(Context context) {
        SharedPreferences preferences = articSharedPreferences(context);
        return new Artic(context,
                preferences.getString("token", ""),
                preferences.getString("url", ""));
    }

    public Artic(Context context, String token, String url) {
        if (!isInitialized) {
            throw new IllegalArgumentException("Artic Not Initialized");
        }
        this.context = context;
        this.token = token;
        // initialize the firebase with
        // the bucket 'artic'
        firebase = new Firebase(url).child(SIMPLE_NAME);
        this.url = url;

        // initialize the auth listener that will
        // connect the token for us

        new AuthListener(firebase, token);
        this.database = new ArticDatabase(firebase);

        articNotification = new ArticNotification(context);

        // keep a copy in field, so we don't have to create it again
        articChildListener = new ArticChildListener(new DataChangedInterface() {
            @Override
            public void whenNewMessage(String key, String value) {
                try {
                    dispatchDataChangeEvent(key, value);
                } catch (JSONException e) {
                    Log.e(NAME + " Artic Listener", "Unable to " +
                            "handle data change event: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void dispatchDataChangeEvent(String key, String value) throws JSONException {
        articNotification.handle(key, new JSONObject(value));
    }


    /**
     * Sends a simple message
     *
     * @param topic   the topic (key) of the message
     * @param title   Title of the notification
     * @param message Content of the notification
     */

    public void simpleMessage(final String topic, String title, String message) throws JSONException, ParseException {
        message(topic, title, message, context.getPackageName(), "<artic>");
    }

    /**
     * Sends a message with activity and startValue,
     *
     * @param topic   the topic (key) of the message
     * @param title   Title of the notification
     * @param message Content of the notification
     */

    public void message(String topic, String title, String message, String activity, String start) throws JSONException, ParseException {
        database.push(topic,
                new JSONObject()
                        .put("title", title)
                        .put("text", message)
                        .put("intent",
                                new JSONObject()
                                        .put("activity", activity)
                                        .put("startValue", start))
                        .put("id", makeUniqueIdentifier())
                        .toString());

        // store the details that tells us when the
        // database was last updated

        database.push("last_push",
                new JSONObject()
                        .put("topic", topic)
                        .put("time", getUniversalTime())
                        .toString());
    }

    /**
     * Listen to a topic
     *
     * @param topic Topic name
     */

    protected void listen(String topic) {
        if (!topic.isEmpty()) {
            for (String child : topic.split("/")) {
                firebase = firebase.child(child);
            }
        }
        firebase.addChildEventListener(articChildListener);
    }

    public void listen(Context context, String topic) {
        SharedPreferences.Editor preferences = articSharedPreferences(context).edit();
        preferences.putString("token", token)
                .putString("url", url)
                .putString("topic", topic)
                .putBoolean("enabled", true);
        preferences.commit();
        ArticService.initialize(context);
    }

    public void cancel() {
        cancelJob();
        articSharedPreferences(context).edit().
                putBoolean("enabled", false).commit();
    }

    private void cancelJob() {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(
                Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(ArticService.JOB);
    }

    /**
     * Returns the Artic Shared Preferences
     */

    static SharedPreferences articSharedPreferences(Context context) {
        return context.getSharedPreferences(
                SIMPLE_NAME +
                        '$' + "watch", Context.MODE_PRIVATE);
    }

    /**
     * Initializes the Firebase
     */

    public static boolean isInitialized = false;

    public static void initialize(Context context) {
        isInitialized = true;
        Firebase.setAndroidContext(context);
    }
}

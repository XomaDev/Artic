package xyz.kumaraswamy.artic;

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

    /**
     * Firebase component
     */

    private Firebase firebase;

    /**
     * Artic DataChange listener that reports back
     * when any data is changed
     */

    private final ArticChildListener articChildListener;

    /**
     * Artic notification manager that handles the data change events
     */

    private final ArticNotification articNotification;

    public Artic(Context context) {
        this(context, articSharedPreferences(context).getString("token", ""),
                articSharedPreferences(context).getString("url", ""));
    }

    public Artic(Context context, String token, String url) {
        // initialize the firebase with
        // the bucket 'artic'
        firebase = new Firebase(url).child(SIMPLE_NAME);

        // initialize the auth listener that will
        // connect the token for us

        new AuthListener(firebase, token);
        this.database = new ArticDatabase(firebase);

        articNotification = new ArticNotification(context);

        // keep a copy in field, so we don't have to create it again
        articChildListener = new ArticChildListener(new DataChangedInterface() {
            @Override
            public void dataChanged(String key, String value) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    articNotification.handle(key, jsonObject);
                } catch (JSONException e) {
                    Log.e(NAME + " Artic Listener", "Unable to " +
                            "handle data change event: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Stores a simple message in the database, when the data is
     * updated notification is accordingly managed
     *
     * @param topic the topic (key) of the message
     * @param title Title of the notification
     * @param message Content of the notification
     */

    public void simpleMessage(final String topic, String title, String message) throws JSONException, ParseException {
        database.push(topic,
                new JSONObject()
                        .put("title", title)
                        .put("text", message)
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
     * @param topic Topic name
     */

    public void listen(String topic) {
        if (!topic.isEmpty()) {
            for (String child : topic.split("/")) {
                firebase = firebase.child(child);
            }
        }
        firebase.addChildEventListener(articChildListener);
    }

    public static void watch(Context context, String token, String url, String topic) {
        SharedPreferences.Editor preferences = articSharedPreferences(context).edit();
        preferences.putString("token", token)
                .putString("url", url)
                .putString("topic", topic);
        preferences.commit();
        ArticUtils.setNextAlarm(context, 1000);
    }

    public static SharedPreferences articSharedPreferences(Context context) {
        return context.getSharedPreferences(
                SIMPLE_NAME +
                '$' + "watch", Context.MODE_PRIVATE);
    }

    /**
     * Initializes the Firebase
     */

    public static void initialize(Context context) {
        Firebase.setAndroidContext(context);
    }
}

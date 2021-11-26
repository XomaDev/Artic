package xyz.kumaraswamy.artic;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

@SuppressWarnings("deprecation")
public class ArticNotification {

    private static final String LOG_TAG = "ArticNotification";

    private final SharedPreferences preferences;
    private final Context context;

    public ArticNotification(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("Artic", Context.MODE_PRIVATE);
    }

    public void handle(String key, JSONObject object) throws JSONException {
        String Id = object.getString("id");
        final String IdNow = key + '$' + Id;

        boolean isAlreadyDone = preferences.getBoolean(IdNow, false);

        if (!isAlreadyDone) {
            Log.i(LOG_TAG, "Handle new response " + IdNow);
            preferences.edit().putBoolean(IdNow, true).commit();
            showNotification(key, object);
        }
    }

    private void showNotification(String name, JSONObject jsonObject) throws JSONException {
        String title = jsonObject.getString("title");
        String content = jsonObject.getString("text");

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        boolean isAboveOrO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
        if (isAboveOrO) {
            manager.createNotificationChannel(
                    new NotificationChannel(Artic.NAME, Artic.NAME,
                            NotificationManager.IMPORTANCE_DEFAULT)
            );
        }

        final Notification.Builder builder = isAboveOrO ?
                // was added in API 26
                new Notification.Builder(context, Artic.NAME) :
                // for older versions
                new Notification.Builder(context);

        JSONObject intentAction = new JSONObject(
                jsonObject.getString("intent"));
        String action = intentAction.getString("activity");

        if (!action.contains(".")) {
            String clazzName = context.getPackageName() + "." + action;
            try {
                // check if the clazz exists
                Class.forName(clazzName);
                action = clazzName;
            } catch (ClassNotFoundException e) {
                // we just ignore this
            }
        }

        Log.d(LOG_TAG, "Action Name " + action);

        Intent intent = new Intent("android.intent.action.VIEW",
                Uri.parse(action));
        intent.putExtra("APP_INVENTOR_START",
                intentAction.getString("startValue"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setCategory(Notification.CATEGORY_MESSAGE)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // specialized styled notification
            createMessageStyle(title, content, builder);
        } else {
            builder.setContentText(title)
                    .setContentText(content);
        }

        manager.notify(name.hashCode() +
                        new Random().nextInt(5),
                builder.build());
    }

    private void createMessageStyle(String title, String content, Notification.Builder builder) {
        Notification.MessagingStyle style = new Notification.MessagingStyle(
                new Person.Builder().setName(
                        title).build()
        );

        final Notification.MessagingStyle.Message message = new Notification.
                MessagingStyle.Message(
                content,
                System.currentTimeMillis(), (Person) null
        );

        builder.setStyle(style.addMessage(message));
    }
}

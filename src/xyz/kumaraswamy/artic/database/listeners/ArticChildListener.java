package xyz.kumaraswamy.artic.database.listeners;

import android.util.Log;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class ArticChildListener implements ChildEventListener {

    private static final String LOG_TAG = "ArticChildListener";

    private final DataChangedInterface changedInterface;

    public ArticChildListener(DataChangedInterface changedInterface) {
        this.changedInterface = changedInterface;
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String previousKey) {
        Log.d(LOG_TAG, "Child Added, key: " + snapshot.getKey());
        postDataChangedEvent(snapshot.getKey(), (String) snapshot.getValue());
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String previousKey) {
        Log.d(LOG_TAG, "Child Changed, key: " + snapshot.getKey());
        postDataChangedEvent(snapshot.getKey(), (String) snapshot.getValue());
    }

    private void postDataChangedEvent(String key, String value) {
        if ("last_push".equals(value)) {
            // we can ignore that
            return;
        }
        changedInterface.dataChanged(key, value);
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
    }

    @Override
    public void onChildMoved(DataSnapshot snapshot, String previousKey) {
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Log.d(LOG_TAG, "Firebase Error Occurred " + firebaseError.getMessage());
    }
}

package xyz.kumaraswamy.artic.database.listeners;

import android.util.Log;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class AuthListener implements Firebase.AuthStateListener {

    private static final String LOG_TAG = "AuthListener";

    private final Firebase firebase;
    private final String token;

    public AuthListener(Firebase firebase, String token) {
        this.firebase = firebase;
        this.token = token;
        firebase.addAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(AuthData data) {
        Log.i(LOG_TAG, "onAuthStateChanged: data = " + data);
        if (data == null) {
            firebase.authWithCustomToken(token, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData data) {
                    Log.i(LOG_TAG, "Auth successful.");
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.e(LOG_TAG, "Auth failed: " + firebaseError.getMessage());
                }
            });
        }
    }
}

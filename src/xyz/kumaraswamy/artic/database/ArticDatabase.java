package xyz.kumaraswamy.artic.database;

import com.firebase.client.Firebase;

public class ArticDatabase {

    private final Firebase firebase;

    public ArticDatabase(Firebase firebase) {
        this.firebase = firebase;
    }

    public void push(String tag, Object value) {
        firebase.child(tag).setValue(value);
    }
}

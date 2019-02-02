package com.example.rival.moneytracker;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FireBaseNotifications extends FirebaseMessagingService {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "MyFirebaseMsgService";
    String uid;
    public FireBaseNotifications() {
        super();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            try {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                Log.d(TAG, "Message data payload data: " + remoteMessage.getData().get("code"));
                if (remoteMessage.getData().get("code").equals("001"))
                    new CreateFriendCache(getApplicationContext()).LocalSaveOfFriend();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //  Toast.makeText(this, "Hi", Toast.LENGTH_LONG).show();

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void onNewToken(String s) {

        super.onNewToken(s);
        try{
            databaseReference.child("users").child(uid).child("firebaseToken").setValue(s);
        }
        catch (Exception e)
        {

        }
    }
}
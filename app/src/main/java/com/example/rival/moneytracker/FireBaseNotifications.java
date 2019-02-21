package com.example.rival.moneytracker;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
    String CHANNEL_ID="Money";
    private String uid;
    private String title;
    private String body;
    private String code;
    private String id;
    private String name;
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
            Log.d(TAG, remoteMessage.getData().get("code"));
            try {
                code=remoteMessage.getData().get("code");
                if(code.equals("4") || code.equals("5") ||code.equals("8"))
                {
                    id=remoteMessage.getData().get("id");
                    name=remoteMessage.getData().get("name");
                }
                title=remoteMessage.getData().get("title");
                body=remoteMessage.getData().get("body");;
                ShowNotification();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //  Toast.makeText(this, "Hi", Toast.LENGTH_LONG).show();

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
    public void ShowNotification()
    {
        Intent intent=new Intent(this, DashBoard.class);
        Log.d(TAG, "Context:" + this);
        switch (code)
        {
            case "1":
                intent=new Intent(this, IncomingRequest.class);
                break;
            case "2":
                intent=new Intent(this, Friend.class);
                new CreateFriendCache(this).LocalSaveOfFriend();
                break;
            case "3":
                intent=new Intent(this, DashBoard.class);
                intent.putExtra("OpenPending", true);
                break;
            case "4":
                intent=new Intent(this, WithFriendRecord.class);
                intent.putExtra("OppnUid", id);
                intent.putExtra("Name", name);
                break;
            case "5":
                intent=new Intent(this, WithFriendRecord.class);
                intent.putExtra("OppnUid", id);
                intent.putExtra("Name", name);
                break;
            case "6":
                intent=new Intent(this, DashBoard.class);
                break;
            case "7":
                intent=new Intent(this, DashBoard.class);
                break;
            case "8":
                intent=new Intent(this, WithFriendRecord.class);
                intent.putExtra("OppnUid", id);
                intent.putExtra("Name", name);
                break;
            default:
                Log.d(TAG, "Hmmmm.... ");
        }
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        createNotificationChannel();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.add)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{1000, 1000,1000,1000,1000})
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(2, mBuilder.build());
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "All", importance);
            channel.setDescription("description");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
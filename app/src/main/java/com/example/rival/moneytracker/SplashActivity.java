package com.example.rival.moneytracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String TAG="SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null) {
            if(getIntent().getExtras()!=null)
            {
                if(getIntent().getExtras().containsKey("code")==true) {
                    Log.d("SplashActivity", getIntent() + "");
                    String code = getIntent().getExtras().get("code").toString();
                    Log.d("SplashActivity", code);
                    String name, id;
                    Intent intent = new Intent(this, DashBoard.class);

                    switch (code) {
                        case "1":
                            intent = new Intent(getApplicationContext(), IncomingRequest.class);
                            break;
                        case "2":
                            intent = new Intent(getApplicationContext(), Friend.class);
                            new CreateFriendCache(getApplicationContext()).LocalSaveOfFriend();
                            break;
                        case "3":
                            intent = new Intent(getApplicationContext(), DashBoard.class);
                            intent.putExtra("OpenPending", true);
                            break;
                        case "4":
                            intent = new Intent(getApplicationContext(), WithFriendRecord.class);
                            name = getIntent().getExtras().get("name").toString();
                            id = getIntent().getExtras().get("id").toString();
                            intent.putExtra("OppnUid", id);
                            intent.putExtra("Name", name);
                            break;
                        case "5":
                            intent = new Intent(getApplicationContext(), WithFriendRecord.class);
                            name = getIntent().getExtras().get("name").toString();
                            id = getIntent().getExtras().get("id").toString();
                            intent.putExtra("OppnUid", id);
                            intent.putExtra("Name", name);
                            break;
                        case "6":
                            intent = new Intent(getApplicationContext(), DashBoard.class);
                            break;
                        case "7":
                            intent = new Intent(getApplicationContext(), DashBoard.class);
                            break;
                        case "8":
                            intent = new Intent(getApplicationContext(), WithFriendRecord.class);
                            name = getIntent().getExtras().get("name").toString();
                            id = getIntent().getExtras().get("id").toString();
                            intent.putExtra("OppnUid", id);
                            intent.putExtra("Name", name);
                            break;
                        default:
                            Log.d(TAG, "Hmmmm.... ");
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return;
                }
                else
                {
                    Log.d("SplashActivity", "else");
                    Intent intent = new Intent(this, DashBoard.class);
                    startActivity(intent);
                    finish();
                }
            }
            else {
                Log.d("SplashActivity", "else");
                Intent intent = new Intent(this, DashBoard.class);
                startActivity(intent);
                finish();
            }
        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

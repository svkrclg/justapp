package com.rcorp.app.futurewallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String TAG="SplashActivity";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs= getSharedPreferences(getResources().getString(R.string.shared_pref_name), MODE_PRIVATE);
        editor=prefs.edit();
        boolean keyContains=prefs.contains("firstTime");
        if(keyContains==false)
        {
            Log.d("Tour", keyContains+"");
            editor.putBoolean("firstTime", true);
            startActivity(new Intent(this, Introduce.class));
            editor.commit();
            finish();
            return;
        }
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null) {
            Log.d("Tour", "1");
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
            Log.d("Tour", "2");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

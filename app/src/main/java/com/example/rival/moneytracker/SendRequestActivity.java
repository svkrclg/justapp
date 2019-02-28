package com.example.rival.moneytracker;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.LoginFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;

public class SendRequestActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private String uid, phone;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    CircularProgressButton addfrndButton;
    EditText phoneno;
    String TAG="SendRequestActivity";
    String recpuid,recpname;
    boolean startedType,nameFound,requestSend,goBack, founduid=false;
    private Snackbar snackbar;
    private InternetStatusReciever internetStatusReciever;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        prefs= getSharedPreferences(getResources().getString(R.string.shared_pref_name), MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_send_request);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        uid=firebaseAuth.getUid();
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("users/"+uid);
        scoresRef.keepSynced(true);
        addfrndButton=(CircularProgressButton) findViewById(R.id.sendReq);

        addfrndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        phoneno=(EditText) findViewById(R.id.toAddPhone);
        phoneno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                nameFound=false;
                goBack=false;
                if(requestSend==true)
                {
                    addfrndButton.revertAnimation(new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd() {
                            addfrndButton.setBackgroundColor(Color.TRANSPARENT);
                            addfrndButton.setText("Searching");
                            addfrndButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        }
                    });
                    requestSend=false;
                }
                if(s.toString().length()<=5)
                {
                    return;
                }
                addfrndButton.setText("Searching");
                Log.d("EditText Study", "afterTextChanged: "+s.toString());
                String phoneNo=s.toString();
                SearchPhoneNumber(phoneNo);

            }
        });

        snackbar= Snackbar.make(findViewById(android.R.id.content), "You are offline", Snackbar.LENGTH_INDEFINITE);
        internetStatusReciever=new InternetStatusReciever(snackbar);
        registerReceiver(internetStatusReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }
    public void SearchPhoneNumber(String phone)
    {
        databaseReference.child("userUidByPhone").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    recpuid=dataSnapshot.getValue(String.class);
                    addfrndButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    if(recpuid!=null)
                    {
                        founduid=true;
                        if(recpuid.equals(uid))
                            addfrndButton.setText("You can't add yourself");
                        else
                        getnamefromUid(recpuid);
                    }
                    else
                    {
                        founduid=false;
                        addfrndButton.setText("Not found");
                        addfrndButton.setBackgroundColor(Color.TRANSPARENT);
                    }
                    Log.d(TAG, "onDataChange: " + dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onDataerror:"+databaseError.toString());
            }
        });
    }
    public void sendRequest()
    {
        Log.d(TAG, nameFound+" "+goBack);
      if(nameFound==true)
      {
          addfrndButton.startAnimation();

         databaseReference.child("users").child(uid).child("pendingSendRequest").child(recpuid).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 Toast.makeText(SendRequestActivity.this, "Friend Request Send", Toast.LENGTH_SHORT).show();
                 addfrndButton.doneLoadingAnimation(getResources().getColor(R.color.colorPrimaryDark), BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow));
                 requestSend=true;
                 goBack=true;
                 nameFound=false;

             }
         });
      }
      else if(goBack==true)
      {
          Log.d(TAG, goBack+"");
          onBackPressed();
      }

    }
    public void getnamefromUid(final String recpuid)
    {
        databaseReference.child("userNameByUid").child(recpuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recpname=dataSnapshot.getValue(String.class);
                Log.d(TAG, recpname+" found");
                //Check if already added
                databaseReference.child("users").child(uid).child("pendingSendRequest").child(recpuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(Boolean.class)!=null)
                        {
                            addfrndButton.setText("You already added "+recpname);
                            Log.d(TAG, "already added"+dataSnapshot.getValue(Boolean.class));
                        }
                         else
                        {
                            //Check if incoming request already
                            databaseReference.child("users").child(uid).child("incomingRequest").child(recpuid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue(Boolean.class)!=null)
                                    {
                                        addfrndButton.setText(recpname+" already added you, Go to your request section");
                                        Log.d(TAG, "he already added you"+dataSnapshot.getValue(Boolean.class));
                                    }
                                    else
                                    {
                                        databaseReference.child("users").child(uid).child("friend").child(recpuid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.getValue(Boolean.class)!=null)
                                                {
                                                    addfrndButton.setText(recpname+" already in your friend list");
                                                    Log.d(TAG, "in list"+dataSnapshot.getValue(Boolean.class));
                                                }
                                                else
                                                {
                                                    addfrndButton.setText("Add "+recpname);
                                                    addfrndButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                                    addfrndButton.setTextColor(Color.WHITE);
                                                    nameFound=true;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                         Log.d(TAG, "Got error " +databaseError.getDetails());
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(internetStatusReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(internetStatusReciever);
    }
}

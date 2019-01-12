package com.example.rival.moneytracker;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

    boolean startedType,nameFound,founduid=false;
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
                addfrndButton.setText("Searching");
                if(s.toString().length()<=5)
                {
                    startedType=false;
                    nameFound=false;
                    return;
                 }
                 nameFound=false;
                startedType=true;
                Log.d("EditText Study", "afterTextChanged: "+s.toString());
                String phoneNo=s.toString();
                SearchPhoneNumber(phoneNo);

            }
        });


    }
    public void SearchPhoneNumber(String phone)
    {
        databaseReference.child("userUidByPhone").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    recpuid=dataSnapshot.getValue(String.class);
                    if(recpuid!=null)
                    {
                        founduid=true;
                        getnamefromUid(recpuid);
                    }
                    else
                        founduid=false;
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
      if(nameFound==true)
      {
          addfrndButton.startAnimation();

         databaseReference.child("users").child(uid).child("pendingSendRequest").child(recpuid).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 addfrndButton.revertAnimation(new OnAnimationEndListener() {
                     @Override
                     public void onAnimationEnd() {
                         addfrndButton.setBackgroundColor(Color.GREEN);
                         addfrndButton.setText("Request send");

                     }
                 });
             }
         });
      }

    }
    public void getnamefromUid(String recpuid)
    {
        databaseReference.child("userNameByUid").child(recpuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recpname=dataSnapshot.getValue(String.class);
                addfrndButton.setText("Add "+recpname);
                nameFound=true;
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
}

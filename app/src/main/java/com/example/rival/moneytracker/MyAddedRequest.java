package com.example.rival.moneytracker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAddedRequest extends AppCompatActivity {

    private ArrayList<Character> firstLetter=new ArrayList<Character>();
    private ArrayList<String> Name=new ArrayList<String>();
    private ArrayList<String> incomingUid=new ArrayList<String>();
    private ArrayList<String> incomingphone=new ArrayList<String>();
    public static FirebaseDatabase firebaseDatabase;
    public static FirebaseAuth firebaseAuth;
    public static DatabaseReference databaseReference;
    String TAG= "IncomingRequest";
    public static  String uid;
    CustomAdapterSendRequest customAdapter;
    RecyclerView recyclerView;
    HashMap<String, Integer > toDeleteUid=new HashMap<>();
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_added_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_my_added_request);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        uid=firebaseAuth.getUid();
        recyclerView = (RecyclerView) findViewById(R.id.SendRequestrecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        customAdapter = new CustomAdapterSendRequest(MyAddedRequest.this);
        databaseReference.child("users").child(uid).child("pendingSendRequest").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d(TAG, dataSnapshot.toString());
                final String fromuid = dataSnapshot.getKey();
                toDeleteUid.put(fromuid, i);
                i++;
                customAdapter.uid.add(fromuid);
                Boolean boo = dataSnapshot.getValue(Boolean.class);
                Log.d(TAG, fromuid + ", " + boo + ", ");
                databaseReference.child("userNameByUid").child(fromuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Name: " + name);
                        customAdapter.name.add(name);
                        customAdapter.firstLetter.add(name.toUpperCase().charAt(0));
                        databaseReference.child("userPhoneByUid").child(fromuid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String phone = dataSnapshot.getValue(String.class);
                                Log.d(TAG, "Phone " + phone);
                                customAdapter.phone.add(phone);

                                recyclerView.setAdapter(customAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String uidDeleted=dataSnapshot.getKey().toString();
                int pos=customAdapter.uid.indexOf(uidDeleted);
                customAdapter.deleteItem(pos);
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

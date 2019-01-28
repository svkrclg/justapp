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

public class IncomingRequest extends AppCompatActivity {

    private ArrayList<Character> firstLetter=new ArrayList<Character>();
    private ArrayList<String> Name=new ArrayList<String>();
    private ArrayList<String> incomingUid=new ArrayList<String>();
    private ArrayList<String> incomingphone=new ArrayList<String>();
    public static FirebaseDatabase firebaseDatabase;
    public static FirebaseAuth firebaseAuth;
    public static DatabaseReference databaseReference;
    String TAG= "IncomingRequest";
    public static  String uid;
    CustomAdapterIncomingRequest customAdapter;
    RecyclerView recyclerView;
    ArrayList<String> toDeleteUid=new ArrayList<>();
    ArrayList<IncomingRequestPOJO> mArraylist=new ArrayList<>();
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_incoming_request);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        uid=firebaseAuth.getUid();
        recyclerView = (RecyclerView) findViewById(R.id.IncomingRequestrecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        customAdapter = new CustomAdapterIncomingRequest(IncomingRequest.this, mArraylist);
        recyclerView.setAdapter(customAdapter);
        databaseReference.child("users").child(uid).child("incomingRequest").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    Log.d(TAG, dataSnapshot.toString());
                    final String fromuid = dataSnapshot.getKey();
                    Boolean boo = dataSnapshot.getValue(Boolean.class);
                    Log.d(TAG, fromuid + ", " + boo + ", ");
                    databaseReference.child("userNameByUid").child(fromuid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String name = dataSnapshot.getValue(String.class);
                            Log.d(TAG, "Name: " + name);
                            databaseReference.child("userPhoneByUid").child(fromuid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String phone = dataSnapshot.getValue(String.class);
                                    Log.d(TAG, "Phone " + phone);
                                    mArraylist.add(new IncomingRequestPOJO(name.toUpperCase().charAt(0), name, phone, fromuid ));
                                    toDeleteUid.add(fromuid);
                                    customAdapter.notifyDataSetChanged();

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
                int pos=toDeleteUid.indexOf(uidDeleted);
                toDeleteUid.remove(pos);
                mArraylist.remove(pos);
                customAdapter.notifyDataSetChanged();
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

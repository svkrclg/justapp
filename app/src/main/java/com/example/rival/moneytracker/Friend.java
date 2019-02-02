package com.example.rival.moneytracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Friend extends AppCompatActivity {

    private ArrayList<FriendPOJO> mArrayList= new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    String TAG= "IncomingRequest";
    public static  String uid;
    CustomAdapterFriend customAdapter;
    RecyclerView recyclerView;
    ArrayList<String> indexing=new ArrayList<>();
    int i=0;
    ProgressBar progressBar;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_friend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        uid=firebaseAuth.getUid();
        recyclerView = (RecyclerView) findViewById(R.id.FriendrecyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv=(TextView) findViewById(R.id.notFound);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        customAdapter = new CustomAdapterFriend(Friend.this, mArrayList);
        recyclerView.setAdapter(customAdapter);
        databaseReference.child("users").child(uid).child("friend").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if(dataSnapshot.getChildrenCount()==0)
                {
                    tv.setVisibility(View.VISIBLE);
                    Log.d(TAG, "wtF: "+dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("users").child(uid).child("friend").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                tv.setVisibility(View.GONE);
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
                                mArrayList.add(new FriendPOJO(name.toUpperCase().charAt(0), name, fromuid, phone));
                                indexing.add(fromuid);
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
                int pos=indexing.indexOf(uidDeleted);
                mArrayList.remove(pos);
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

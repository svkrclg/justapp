package com.rcorp.app.futurewallet;

import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MyAddedRequest extends AppCompatActivity {

    private ArrayList<SendRequestPOJO> mArrayList=new ArrayList<>();
    public static FirebaseDatabase firebaseDatabase;
    public static FirebaseAuth firebaseAuth;
    public static DatabaseReference databaseReference;
    String TAG= "IncomingRequest";
    public static  String uid;
    CustomAdapterSendRequest customAdapter;
    RecyclerView recyclerView;
    ArrayList<String> toDeleteUid=new ArrayList<>();
    int i=0;
    ProgressBar progressBar;
    TextView tv;
    private  Snackbar snackbar;
    private  InternetStatusReciever internetStatusReciever;
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv=(TextView) findViewById(R.id.notFound);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        customAdapter = new CustomAdapterSendRequest(MyAddedRequest.this,mArrayList);
        recyclerView.setAdapter(customAdapter);
        databaseReference.child("users").child(uid).child("pendingSendRequest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);
                if(dataSnapshot.getChildrenCount()==0)
                {
                    tv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "wtF: "+dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("users").child(uid).child("pendingSendRequest").addChildEventListener(new ChildEventListener() {
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
                                mArrayList.add(new SendRequestPOJO(name, fromuid,phone, name.toUpperCase().charAt(0)));
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
                mArrayList.remove(pos);
                customAdapter.notifyDataSetChanged();;
                if(mArrayList.isEmpty())
                    tv.setVisibility(View.VISIBLE);
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        snackbar= Snackbar.make(findViewById(android.R.id.content), "You are offline", Snackbar.LENGTH_INDEFINITE);
        internetStatusReciever=new InternetStatusReciever(snackbar);
        registerReceiver(internetStatusReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

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

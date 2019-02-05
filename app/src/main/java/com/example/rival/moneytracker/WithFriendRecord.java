package com.example.rival.moneytracker;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class WithFriendRecord extends AppCompatActivity {

    String TAG="WithFriendRecord";
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String uid;
    RecyclerView recyclerView;
    CustomAdapterFriendRecord customAdapterFriendRecord;
    ArrayList<friendRecordPOJO> mArrayList=new ArrayList<>();
    private String oppnUid;
    SimpleDateFormat formatter;
    Calendar c;
    SimpleDateFormat sdf;
    String name;
    ProgressBar progressBar;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_friend_record);
        Intent i=getIntent();
        name=i.getStringExtra("Name");
        oppnUid=i.getStringExtra("OppnUid");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_friend_record);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        Log.d(TAG, name+" "+oppnUid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        uid=firebaseAuth.getUid();
        c = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        recyclerView = (RecyclerView) findViewById(R.id.FriendRecordrecyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv=(TextView) findViewById(R.id.notFound);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        customAdapterFriendRecord=new CustomAdapterFriendRecord(this, mArrayList );
        recyclerView.setAdapter(customAdapterFriendRecord);
        databaseReference.child("users").child(uid).child("myTransactions").child(oppnUid).child("transactions").addValueEventListener(new ValueEventListener() {
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
        LoadData();
    }
    private void LoadData(){
       databaseReference.child("users").child(uid).child("myTransactions").child(oppnUid).child("transactions").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               tv.setVisibility(View.GONE);
               for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    final String StimeStamp=ds.getKey();
                    Long timestamp=Long.parseLong(StimeStamp);
                    databaseReference.child("confirmedTransactions").child(StimeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
                        String test=StimeStamp;
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "Test: "+StimeStamp);
                            String addedBy=dataSnapshot.child("addedBy").getValue(String.class);
                            int amount=dataSnapshot.child("amount").getValue(Integer.class);
                            Long confirmTimeStamp=dataSnapshot.child("confirmTimeStamp").getValue(Long.class);
                            String from=dataSnapshot.child("from").getValue(String.class);
                            String to=dataSnapshot.child("to").getValue(String.class);
                            String reason=dataSnapshot.child("reason").getValue(String.class);
                            String time="--";
                            Log.d(TAG, confirmTimeStamp+"");
                            try {
                                c.setTimeInMillis(confirmTimeStamp);
                                Date d = c.getTime();
                                time = sdf.format(d);
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "Excp:"+e.toString());
                            }
                            String ObjReason, Objtime, Objdirection;
                            Boolean ObjisAddedByMe;
                            int ObjAmount;
                            if(addedBy.equals(uid))
                                    ObjisAddedByMe=true;
                            else
                                    ObjisAddedByMe=false;
                            ObjReason=reason;
                            Objtime=time;
                            ObjAmount=amount;
                            if(to.equals(uid))
                                Objdirection="coming";
                            else
                                Objdirection="going";
                            mArrayList.add(new friendRecordPOJO(ObjReason, Objtime, ObjisAddedByMe, ObjAmount, Objdirection));
                            customAdapterFriendRecord.notifyDataSetChanged();



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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

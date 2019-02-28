package com.example.rival.moneytracker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
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
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;

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
    TextView tv, historyStatus;
    private Dialog dialog;
    Menu mMenu;
    private Snackbar snackbar;
    private InternetStatusReciever internetStatusReciever;
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
        historyStatus=(TextView)findViewById(R.id.historyStatus);
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
        snackbar= Snackbar.make(findViewById(android.R.id.content), "You are offline", Snackbar.LENGTH_INDEFINITE);
        internetStatusReciever=new InternetStatusReciever(snackbar);
        registerReceiver(internetStatusReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
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
                            recyclerView.scrollToPosition(mArrayList.size()-1);


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
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record_menu, menu);
        databaseReference.child("users").child(uid).child("deleteHistory").child(oppnUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Boolean.class)!=null)
                {
                    menu.findItem(R.id.delete_record).setVisible(false);
                    historyStatus.setVisibility(View.VISIBLE);
                }
                else
                {
                    menu.findItem(R.id.delete_record).setVisible(true);
                    historyStatus.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.delete_record)
        {

            final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("Delete history with "+name);
            alertDialog.setMessage(name+" must have to accept your request for deleting transaction history with you.");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(CheckInternet.isInternet==false)
                    {
                        Toast.makeText(WithFriendRecord.this, "Internet not available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    databaseReference.child("users").child(uid).child("deleteHistory").child(oppnUid).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            Toast.makeText(getApplicationContext(), "Request send for deleting", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    });
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog=alertDialog.create();
            dialog.show();
        }
        if (id==R.id.addhere)
        {
            dialog = new Dialog(WithFriendRecord.this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.add_transaction_dialog);
            final Button btnRequest = (Button) dialog.findViewById(R.id.request);
            TextView tile=dialog.findViewById(R.id.title);
            tile.setText("Add transaction for "+ name);
            final EditText edtAmount=(EditText) dialog.findViewById(R.id.amount);
            final EditText edtReason=(EditText) dialog.findViewById(R.id.reason);
            final RadioButton iwillget=(RadioButton) dialog.findViewById(R.id.iwillget);
            final RadioButton iwillgive=(RadioButton) dialog.findViewById(R.id.iwillgive);
            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CheckInternet.isInternet==false)
                    {
                        Toast.makeText(WithFriendRecord.this, "Internet not available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String amount = edtAmount.getText().toString().trim();
                    String reason = edtReason.getText().toString().trim();
                    if (amount.length()>0) {
                        if(iwillget.isChecked()==false && iwillgive.isChecked()==false)
                        {
                            Toast.makeText(WithFriendRecord.this, "Please check any of them", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String fromUid, toUid;
                        if (iwillget.isChecked() == true ||iwillgive.isChecked()==true) {
                            fromUid = oppnUid;
                            toUid = uid;
                        }
                        else {
                            toUid = oppnUid;
                            fromUid = uid;
                        }
                        btnRequest.setBackgroundResource(R.drawable.button_bg_onclick);
                        btnRequest.setTextColor(Color.WHITE);
                        Map<String, Object> transaction = new HashMap<>();
                        int amt = Integer.parseInt(amount);
                        transaction.put("from", fromUid);
                        transaction.put("to", toUid);
                        transaction.put("addedBy", uid);
                        if(reason.length()==0)
                        transaction.put("reason", "No reason");
                        else
                            transaction.put("reason", reason);
                        transaction.put("amount", amt);
                        long timeinMillis = System.currentTimeMillis();
                        Log.d(TAG, transaction.toString());
                        databaseReference.child("transactions").child(timeinMillis + "").updateChildren(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(WithFriendRecord.this, "Transaction added.", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(WithFriendRecord.this, "Amount field cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                         });
                dialog.show();
                     }
            return super.onOptionsItemSelected(item);



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

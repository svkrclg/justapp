package com.example.rival.moneytracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DashBoard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PendingTransaction.OnFragmentInteractionListener, ConfirmedTransaction.OnFragmentInteractionListener {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public String uid;
    public String name;
    public String phone,email;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String token;
    private  TabLayout tabLayout;
    String TAG="DashBoard";
    private ViewPager viewPager;
    ChildEventListener cel;
    private Toolbar toolbar;
    private Snackbar snackbar;
    private  InternetStatusReciever internetStatusReciever;
    private InterstitialAd mInterstitialAd;
    private Boolean firstTime;
    private Context context;
    private Handler handler;
    private Runnable r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
        context=this;
        prefs= getSharedPreferences(getResources().getString(R.string.shared_pref_name), MODE_PRIVATE);
        editor=prefs.edit();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setStatusBarBackgroundColor(Color.TRANSPARENT);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Current Status"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager)findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        name=prefs.getString("name", "NAME");
        phone=prefs.getString("phone", "phoneNo");
        email=prefs.getString("email", "email");
        firstTime=prefs.getBoolean("firstTime", false);
        uid=firebaseAuth.getUid();
        if(!firebaseAuth.getUid().toString().equals(prefs.getString("uid", "uid")))
        {
          editor.putString("uid", uid);
        }
        handler = new Handler();
        final int delay = 5000; //milliseconds

        r=new Runnable(){
            public void run(){
                Log.d("Test2", "In runnable");
                new  CreateFriendCache(context).LocalSaveOfFriend();
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(r, delay);

        Log.d("DashBoard", name+", "+phone+", "+email);
        View header=navigationView.getHeaderView(0);
        TextView nav_bar_first_letter=(TextView) header.findViewById(R.id.nav_bar_first_letter);
        nav_bar_first_letter.setText(name.toUpperCase().charAt(0)+"");
        TextView nav_bar_name=(TextView)header.findViewById(R.id.nav_bar_name);
        TextView nav_bar_phone=(TextView)header.findViewById(R.id.nav_bar_phone);
        nav_bar_name.setText(name);
        nav_bar_phone.setText(phone);
        Intent i=getIntent();
        Log.d(TAG, i+"");
        if(i!=null)
        {
            Log.d(TAG, "intent: "+i +"\n "+i.getBooleanExtra("OpenPending", false));
            if(i.getBooleanExtra("OpenPending", false)==true)
                viewPager.setCurrentItem(1, true);
        }
        //Storing token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }
                        token = task.getResult().getToken();
                        databaseReference.child("users").child(uid).child("firebaseToken").setValue(token);
                        editor.putString("firebaseToken", token);
                    }
                });
         CheckForDeleteHistory();
         ListenForPendingTranasactionCount();
         snackbar=Snackbar.make(findViewById(android.R.id.content), "You are offline", Snackbar.LENGTH_INDEFINITE);
         internetStatusReciever=new InternetStatusReciever(snackbar);
         registerReceiver(internetStatusReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
         ShowDialogToAskForTour();
        mInterstitialAd=new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial5));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_qr) {
            Intent i=   new Intent(getApplicationContext(), QRcode.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_addTran) {
            CreateTransactionPageOpen();
            return true;
        }
        else if (id == R.id.action_addFriend) {
            SendRequestActivityOpen();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void SendRequestActivityOpen()
    {

        Intent i=   new Intent(getApplicationContext(), SendRequestActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }
    public void CreateTransactionPageOpen()
    {
        Intent i=   new Intent(getApplicationContext(), AddTransaction.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.incoming_request) {
            Intent i=   new Intent(getApplicationContext(), IncomingRequest.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        } else if (id == R.id.send_request) {
            Intent i=   new Intent(getApplicationContext(), MyAddedRequest.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        } else if (id == R.id.Friends) {
            Intent i=   new Intent(getApplicationContext(), Friend.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            databaseReference.child("users").child(uid).child("firebaseToken").setValue("NULL").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    editor.clear().commit();
                    editor.putBoolean("firstTime", false).commit();
                    handler.removeCallbacks(r);
                    firebaseAuth.signOut();
                    Intent i= new Intent(getApplicationContext(), LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    progressDialog.dismiss();
                    startActivity(i);
                    if(mInterstitialAd.isLoaded())
                        mInterstitialAd.show();
                    finish();
                }
            });
        } else if (id == R.id.about) {
            startActivity(new Intent(DashBoard.this, AboutPage.class));
        }
        else if (id==R.id.app_tour)
        {
            startActivity(new Intent(this, AppTour.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void CheckForDeleteHistory() {

        cel = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String oppnuid = dataSnapshot.getKey();
                databaseReference.child("userNameByUid").child(oppnuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.getValue(String.class);
                        showAlertDialog(name, oppnuid);
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

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
    public void showAlertDialog(String name , final String oppUid)
    {
        final AlertDialog.Builder alertDialog =new AlertDialog.Builder(DashBoard.this);
        alertDialog.setTitle("Request for delete history");
        alertDialog.setCancelable(false);
        alertDialog.setMessage(name+" wants to delete transaction history with you");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child("users").child(uid).child("deleteRequestArrived").child(oppUid).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child("users").child(uid).child("deleteRequestArrived").child(oppUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Rejected", Toast.LENGTH_LONG).show();
                            }
                        });
                dialog.cancel();
            }
        });
        AlertDialog dialog=alertDialog.create();

        dialog.show();
    }
    private void ListenForPendingTranasactionCount(){
        databaseReference.child("users").child(uid).child("pendingTransactions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long n=dataSnapshot.getChildrenCount();
                if(n>0)
                    tabLayout.getTabAt(1).setText("Pending"+"("+n+")");
                else
                    tabLayout.getTabAt(1).setText("Pending");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        databaseReference.child("users").child(uid).child("deleteRequestArrived").addChildEventListener(cel);
        registerReceiver(internetStatusReciever,  new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onPause");
        unregisterReceiver(internetStatusReciever);
        databaseReference.child("users").child(uid).child("deleteRequestArrived").removeEventListener(cel);
    }
    public void ShowDialogToAskForTour()
    {
        Log.d("Tour", firstTime+"");
        if(firstTime==false)
           return;
         prefs.edit().putBoolean("firstTime", false).commit();
        final AlertDialog.Builder alertDialog =new AlertDialog.Builder(DashBoard.this);
        alertDialog.setTitle("Welcome!");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Get used to app by going through app introduction");
        alertDialog.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    startActivity( new Intent(context, AppTour.class));
                    dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                      dialog.cancel();
            }
        });
        AlertDialog dialog=alertDialog.create();

        dialog.show();
    }

}

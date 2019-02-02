package com.example.rival.moneytracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    private FloatingActionMenu floatingActionMenu;
    private String token;
    String TAG="DashBoard";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
        prefs= getSharedPreferences(getResources().getString(R.string.shared_pref_name), MODE_PRIVATE);
        editor=prefs.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setStatusBarBackgroundColor(Color.TRANSPARENT);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Current Status"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
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
        uid=firebaseAuth.getUid();
        if(!firebaseAuth.getUid().toString().equals(prefs.getString("uid", "uid")))
        {
          editor.putString("uid", uid);
        }
        new CreateFriendCache(this).LocalSaveOfFriend();
        Log.d("DashBoard", name+", "+phone+", "+email);
        View header=navigationView.getHeaderView(0);
        TextView nav_bar_first_letter=(TextView) header.findViewById(R.id.nav_bar_first_letter);
        nav_bar_first_letter.setText(name.toUpperCase().charAt(0)+"");
        TextView nav_bar_name=(TextView)header.findViewById(R.id.nav_bar_name);
        TextView nav_bar_phone=(TextView)header.findViewById(R.id.nav_bar_phone);
        nav_bar_name.setText(name);
        nav_bar_phone.setText(phone);
        //Setting floting action menu
        floatingActionMenu=(FloatingActionMenu) findViewById(R.id.fabmenu);
        floatingActionMenu.setClosedOnTouchOutside(true);
        FloatingActionButton addfrnd=(FloatingActionButton) findViewById(R.id.menu_item1);
        FloatingActionButton creatTran=(FloatingActionButton) findViewById(R.id.menu_item2);
        addfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendRequestActivityOpen();
            }
        });
        creatTran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateTransactionPageOpen();
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void SendRequestActivityOpen()
    {
        floatingActionMenu.close(true);
        Intent i=   new Intent(getApplicationContext(), SendRequestActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }
    public void CreateTransactionPageOpen()
    {
        floatingActionMenu.close(true);
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
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_logout) {
            databaseReference.child("users").child(uid).child("firebaseToken").setValue("NULL").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    editor.clear().commit();
                    firebaseAuth.signOut();
                    Intent i= new Intent(getApplicationContext(), LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                    finish();
                }
            });

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    int i=0;
    public  void CheckLocal(View view)
    {
        HashMap<String,String> outputMap = new HashMap<>();
        String jsonString =prefs.getString("friendMap", "Not found");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Iterator<String> keysItr = jsonObject.keys();
        while(keysItr.hasNext()) {
            String k = keysItr.next();
            String v = null;
            try {
                v = (String) jsonObject.get(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            outputMap.put(k,v);
        }
        Log.d(TAG, outputMap.toString());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

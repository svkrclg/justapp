package com.rcorp.app.futurewallet;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;

public class LoginActivity extends AppCompatActivity {

    private Button ToRegister;
    private EditText email, password;
    CircularProgressButton login;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String uid;
    private String name;
    private String phone;
    private String emailid;// emailid to putString in sharedPref
    private  SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    AnimationDrawable animationDrawable;
    private Snackbar snackbar;
    private InternetStatusReciever internetStatusReciever;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            Intent i= new Intent(getApplicationContext(), DashBoard.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        animationDrawable =(AnimationDrawable)findViewById(R.id.relativelayout).getBackground();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        prefs= getSharedPreferences(getResources().getString(R.string.shared_pref_name), MODE_PRIVATE);
        editor = getSharedPreferences(getResources().getString(R.string.shared_pref_name), MODE_PRIVATE).edit();
        final CircularProgressButton btn = (CircularProgressButton) findViewById(R.id.btn_Login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckInternet.isInternet==false)
                {
                    Toast.makeText(LoginActivity.this, "Internet not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                btn.startAnimation();
            }
        });
        ToRegister=(Button) findViewById(R.id.To_Register);
        ToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
        login=(CircularProgressButton) findViewById(R.id.btn_Login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LoginActivity", "IN oCLick");
                Login();
            }
        });
        email=(EditText) findViewById(R.id.loginEmail);
        password=(EditText) findViewById(R.id.loginPassword);
        Log.d("LoginActivity", "IN oncreate");
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        snackbar= Snackbar.make(findViewById(android.R.id.content), "You are offline", Snackbar.LENGTH_INDEFINITE);
        internetStatusReciever=new InternetStatusReciever(snackbar);
        registerReceiver(internetStatusReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        mInterstitialAd=new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial4));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }
    public  void Login()
    {
        Log.d("LoginActivity", "in Login");
        login.startAnimation();
        loginUser();

    }

    public void loginUser()
    {
        String semail= email.getText().toString().trim();
        String spassword=password.getText().toString();
        if(semail.length()==0)
        {
            Toast.makeText(getApplicationContext(), "Email cannot be empty.", Toast.LENGTH_LONG).show();
            login.revertAnimation();
            login.setBackground(getResources().getDrawable(R.drawable.circular_border_shape));

            return;
        }
        else if(spassword.length()==0)
        {
            Toast.makeText(getApplicationContext(), "Password cannot be empty.", Toast.LENGTH_LONG).show();
            login.revertAnimation();
            login.setBackground(getResources().getDrawable(R.drawable.circular_border_shape));
            return;
        }
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        Log.d("LoginActivity", semail+", "+spassword);
        firebaseAuth.signInWithEmailAndPassword(semail, spassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            uid=firebaseAuth.getUid();
                            databaseReference.child("users").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    name=dataSnapshot.getValue().toString();
                                    databaseReference.child("users").child(uid).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            phone = dataSnapshot.getValue().toString();
                                            databaseReference.child("users").child(uid).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    emailid = dataSnapshot.getValue().toString();
                                                    editor.putString("name", name);
                                                    editor.putString("uid", uid);
                                                    editor.putString("phone", phone);
                                                    editor.putString("email", emailid);
                                                    editor.apply();
                                                    Intent i = new Intent(getApplicationContext(), DashBoard.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                    startActivity(i);
                                                    if(mInterstitialAd.isLoaded())
                                                        mInterstitialAd.show();
                                                    finish();
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
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else
                        {
                           if (task.getException() instanceof FirebaseAuthInvalidUserException)
                            Toast.makeText(getApplicationContext(), "Email not registered", Toast.LENGTH_LONG).show();
                           else if( task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                               Toast.makeText(getApplicationContext(), "Email and password combination is not correct", Toast.LENGTH_LONG).show();
                           login.revertAnimation(new OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd() {
                                    login.setText("Try Again");
                                    login.setBackground(getResources().getDrawable(R.drawable.circular_border_shape));

                                }
                            });


                        }
                    }
                });
    }
    public void ResetPassword(View v)
    {
        startActivity(new Intent(this, ResetPassword.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (animationDrawable != null && !animationDrawable.isRunning())
            animationDrawable.start();
        registerReceiver(internetStatusReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animationDrawable != null && animationDrawable.isRunning())
            animationDrawable.stop();
        unregisterReceiver(internetStatusReciever);
    }
}
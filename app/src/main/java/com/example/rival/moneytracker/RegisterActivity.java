package com.example.rival.moneytracker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.euicc.EuiccInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;

public class RegisterActivity extends AppCompatActivity {
    private Button Tologin;
    private EditText name, email, password, cpassword, phone;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private String uid;
    private String TAG="FireBaseRegister";
    CircularProgressButton register;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String verificationId;
    boolean isSigninInProgres=false;
    PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    String Sname,Sphone, Semail, Spassword, SCpassword;
    private Dialog dialog;
    AnimationDrawable animationDrawable;
    private  Snackbar snackbar;
    private  InternetStatusReciever internetStatusReciever;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        animationDrawable =(AnimationDrawable)findViewById(R.id.relativelayout).getBackground();
        firebaseAuth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        Tologin=(Button) findViewById(R.id.To_login);
        ImageButton back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        register=(CircularProgressButton) findViewById(R.id.btn_Register);
        name=(EditText) findViewById(R.id.name);
        email=(EditText) findViewById(R.id.email);
        phone=(EditText) findViewById(R.id.phone);
        password=findViewById(R.id.password);
        cpassword=findViewById(R.id.cpassword);
        phone.setText("+91");
        phone.setSelection(3);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
             if(s.length()<3)
             {
                 phone.setText("+91");
                 phone.setSelection(3);
             }
            }
        });
        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
              /*  if(isSigninInProgres==true)
                    return;
                isSigninInProgres=true;*/
                Log.d(TAG, "onerifcationcomplete: ");
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if(e instanceof FirebaseAuthInvalidCredentialsException)
                   Toast.makeText(RegisterActivity.this, "Phone number invalid", Toast.LENGTH_SHORT).show();
                else if(e instanceof FirebaseTooManyRequestsException)
                    Toast.makeText(RegisterActivity.this, "Too many request. Try again later", Toast.LENGTH_SHORT).show();
                register.revertAnimation();
                Log.w(TAG, "onVerificationFailed", e);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                /*if(isSigninInProgres==true)
                    return;
                isSigninInProgres=true;*/
                mForceResendingToken=forceResendingToken;
                super.onCodeSent(s, forceResendingToken);
                verificationId=s;
                showOTPdialog();
            }
        };
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("userUidByPhone");
        scoresRef.keepSynced(true);
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        snackbar= Snackbar.make(findViewById(android.R.id.content), "You are offline", Snackbar.LENGTH_INDEFINITE);
        internetStatusReciever=new InternetStatusReciever(snackbar);
        registerReceiver(internetStatusReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }
    public void Register(View view)
    {
        if(CheckInternet.isInternet==false)
        {
            Toast.makeText(this, "Internet not available", Toast.LENGTH_SHORT).show();
            return;
        }
        Sname=name.getText().toString().trim().toUpperCase();
        Sphone=phone.getText().toString().trim();
        Semail=email.getText().toString().trim();
        Spassword=password.getText().toString();
        SCpassword=cpassword.getText().toString();
        Log.d(TAG, "@#:"+Sname.length());
        if(Sname.length()==0)
        {
            Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(Sphone.length()!=13)
        {
            Toast.makeText(this, "Invalid phone", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(Semail.length()==0)
        {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(Spassword.length()<6)
        {
            Toast.makeText(this, "Password should be atleast 6 character", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(Spassword.equals(SCpassword)==false)
        {
            Toast.makeText(this, "Confirm password doesn't match", Toast.LENGTH_SHORT).show();
            return;
        }
        register.startAnimation();
        mRef=database.getReference();
        firebaseAuth.fetchSignInMethodsForEmail(Semail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().getSignInMethods().size()==0)
                    {
                        //not registered
                        mRef.child("userUidByPhone").child(Sphone.substring(3, Sphone.length())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() !=null)
                                {
                                    Log.d(TAG, "In if part");
                                    Toast.makeText(RegisterActivity.this, "Phone number already in use", Toast.LENGTH_LONG).show();
                                    register.revertAnimation();
                                    return;
                                }
                                else
                                {
                                    Log.d(TAG, "In else part" );
                                    verifiyPhoneNumber(Sphone);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "Email already registered.", Toast.LENGTH_SHORT).show();
                        register.revertAnimation();
                    }
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(RegisterActivity.this, "Email not in correct format", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public void verifiyPhoneNumber(String s)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                s,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }
    public void verifiyPhoneNumber(String s, PhoneAuthProvider.ForceResendingToken x)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                s,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks, x);
    }
    public void showOTPdialog()
    {
        dialog = new Dialog(RegisterActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.otp_dialog);
        final Button dialogButton = (Button) dialog.findViewById(R.id.btsubmit);
        final EditText edt=(EditText) dialog.findViewById(R.id.edtOTP);
        final TextView detail=dialog.findViewById(R.id.detail);
        detail.setText("OTP sent to: "+Sphone);
        TextView resendotp=(TextView) dialog.findViewById(R.id.resendOtp);
        TextView changeNumber= dialog.findViewById(R.id.changeNumber);
        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                register.revertAnimation();
            }
        });
        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  verifiyPhoneNumber(Sphone, mForceResendingToken);
            }
        });
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String  s=edt.getText().toString();
                if(s.length()==0)
                {
                    Toast.makeText(RegisterActivity.this, "OTP is invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialogButton.setBackgroundResource(R.drawable.button_bg_onclick);
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId, s);
                    signInWithPhoneAuthCredential(credential);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseAuth.signOut();
                                                Log.d(TAG, "User account deleted.");
                                                //Sign in using email and password
                                                firebaseAuth.createUserWithEmailAndPassword(Semail, Spassword)
                                                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Log.d(TAG, "Inside task successful");
                                                                    //Updating Data
                                                                    mRef=database.getReference();
                                                                    uid=firebaseAuth.getCurrentUser().getUid();
                                                                    Map<String,Object> taskMap = new HashMap<>();
                                                                    taskMap.put("name", Sname);
                                                                    taskMap.put("phone", Sphone.substring(3, Sphone.length()));
                                                                    taskMap.put("email", Semail);
                                                                    mRef.child("users").child(uid).updateChildren(taskMap);
                                                                    Toast.makeText(getApplicationContext(), "Registration Successfully", Toast.LENGTH_LONG).show();
                                                                    SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.shared_pref_name), MODE_PRIVATE).edit();
                                                                    editor.putString("name", Sname);
                                                                    editor.putString("uid", uid);
                                                                    editor.putString("phone", Sphone);
                                                                    editor.putString("email", Semail);
                                                                    editor.apply();
                                                                    register.revertAnimation(new OnAnimationEndListener() {
                                                                        @Override
                                                                        public void onAnimationEnd() {
                                                                            dialog.dismiss();
                                                                            register.setBackgroundColor(Color.GREEN);
                                                                            register.setText("Done");
                                                                            // register.doneLoadingAnimation(Color.rgb(0,255,0), BitmapFactory.decodeResource(getResources(), R.drawable.correct));
                                                                            Intent i= new Intent(getApplicationContext(), DashBoard.class);
                                                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                            startActivity(i);
                                                                            finish();
                                                                        }
                                                                    });
                                                                }
                                                                else
                                                                {
                                                                    if(task.getException() instanceof FirebaseAuthWeakPasswordException)
                                                                        Toast.makeText(RegisterActivity.this, "Password weak", Toast.LENGTH_LONG).show();
                                                                    else
                                                                        Toast.makeText(RegisterActivity.this, "Try again later", Toast.LENGTH_LONG).show();
                                                                    register.revertAnimation();
                                                                    Log.w(TAG, "hmm::: ", task.getException());
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                            // ...
                        } else {
                            if(dialog.isShowing())
                            dialog.dismiss();
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                               Toast.makeText(RegisterActivity.this, "Unable to verify phone number", Toast.LENGTH_LONG).show();
                            }
                            register.revertAnimation();
                        }
                    }

                });

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

package com.example.rival.moneytracker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        Tologin=(Button) findViewById(R.id.To_login);
        Tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
                isSigninInProgres=true;
                Log.d(TAG, "onerifcationcomplete: ");
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                if(isSigninInProgres==true)
                     return;
                mForceResendingToken=forceResendingToken;
                super.onCodeSent(s, forceResendingToken);
                verificationId=s;
                showOTPdialog();
            }
        };
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("userUidByPhone");
        scoresRef.keepSynced(true);

    }
    public void Register(View view)
    {
        register.startAnimation();
        getNewUserRegistered();
    }
    public void getNewUserRegistered()
    {
        Sname=name.getText().toString().trim().toUpperCase();
        Sphone=phone.getText().toString().trim();
        Semail=email.getText().toString().trim();
        Spassword=password.getText().toString().trim();
        SCpassword=cpassword.getText().toString().trim();
        Log.d(TAG, "Inside f");
        //Check mobile number of reuseablilty
        mRef=database.getReference();
        mRef.child("userUidByPhone").child(Sphone.substring(3, Sphone.length())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "PHone: "+Sphone);
                Log.d(TAG, "dataSnapshot.getValue()"+dataSnapshot.getValue());
                Log.d(TAG, "database.hasChildren()"+dataSnapshot.hasChildren());
                Log.d(TAG, "dataSnapshot.getValue(String.class)"+dataSnapshot.getValue(String.class));
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
        Button dialogButton = (Button) dialog.findViewById(R.id.btsubmit);
        EditText edt=(EditText) dialog.findViewById(R.id.edtOTP);
        final String s=edt.getText().toString();
        TextView resendotp=(TextView) dialog.findViewById(R.id.resendOtp);
        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  verifiyPhoneNumber(Sphone, mForceResendingToken);
            }
        });
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId, s);
                signInWithPhoneAuthCredential(credential);
                Toast.makeText(getApplicationContext(), "Got it", Toast.LENGTH_SHORT).show();
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
                                                                    Toast.makeText(RegisterActivity.this, "Try again later", Toast.LENGTH_LONG).show();
                                                                    //Delete authenticated user by phone number

                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                               Toast.makeText(RegisterActivity.this, "Unable to verify phone number", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                });

    }
    }

package com.example.rival.moneytracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.euicc.EuiccInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        password=(EditText) findViewById(R.id.password);
        cpassword=(EditText) findViewById(R.id.cpassword);
        phone=(EditText) findViewById(R.id.phone);

    }
    public void Register(View view)
    {
        register.startAnimation();
        getNewUserRegistered();
    }
    public void getNewUserRegistered()
    {
        final String Sname=name.getText().toString().trim().toUpperCase();
        String Spassword=password.getText().toString().trim();
        final String Semail=email.getText().toString().trim().toLowerCase();
        final String Sphone=phone.getText().toString().trim();
        Log.d(TAG, "Inside f");
        firebaseAuth.createUserWithEmailAndPassword(Semail, Spassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "Inside task successful");
                            //Updating Data
                            mRef=database.getReference();
                            uid=firebaseAuth.getCurrentUser().getUid();
                            mRef.child("users").child(uid).child("name").setValue(Sname);
                            mRef.child("users").child(uid).child("phone").setValue(Sphone);
                            mRef.child("users").child(uid).child("email").setValue(Semail);
                            DatabaseReference indexRef= database.getReference();
                            Toast.makeText(getApplicationContext(), "Registration Successfully", Toast.LENGTH_LONG).show();
                            Intent i= new Intent(getApplicationContext(), DashBoard.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                            register.revertAnimation(new OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd() {
                                    register.setInitialCornerRadius(30.0f);
                                    register.setFinalCornerRadius(30.0f);
                                }
                            });
                        }
                    }
                });

    }
}

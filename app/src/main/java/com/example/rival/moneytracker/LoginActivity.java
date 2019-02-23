package com.example.rival.moneytracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        editor = getSharedPreferences(getResources().getString(R.string.shared_pref_name), MODE_PRIVATE).edit();
        final CircularProgressButton btn = (CircularProgressButton) findViewById(R.id.btn_Login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                                    login.revertAnimation(new OnAnimationEndListener() {
                                                        @Override
                                                        public void onAnimationEnd() {
                                                            login.setBackgroundColor(Color.GREEN);
                                                            login.setText("Done");
                                                            Intent i = new Intent(getApplicationContext(), DashBoard.class);
                                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                            startActivity(i);
                                                            finish();
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

}
package com.example.rival.moneytracker;

import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;

public class ResetPassword extends AppCompatActivity {

    EditText edtResetEmail;
    String resetEmail;
    CircularProgressButton btnReset;
    private FirebaseAuth firebaseAuth;
    String TAG="ResetPassword";
    Boolean bEmailSent=false;
    private Snackbar snackbar;
    private InternetStatusReciever internetStatusReciever;
    private  AnimationDrawable animationDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        edtResetEmail=(EditText) findViewById(R.id.resetEmail);

        animationDrawable =(AnimationDrawable)findViewById(R.id.relativelayout).getBackground();
        btnReset=(CircularProgressButton) findViewById(R.id.btnReset);
        firebaseAuth= FirebaseAuth.getInstance();
        snackbar=Snackbar.make(findViewById(android.R.id.content), "You are offline", Snackbar.LENGTH_INDEFINITE);
        internetStatusReciever=new InternetStatusReciever(snackbar);
        registerReceiver(internetStatusReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        ImageButton back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
    }
    public void Reset(View view)
    {
        if(CheckInternet.isInternet==false)
        {
            Toast.makeText(this, "Internet not available", Toast.LENGTH_SHORT).show();
            return;
        }
        if(bEmailSent==true)
        {
            onBackPressed();
            return;
        }
        resetEmail=edtResetEmail.getText().toString().trim();
        if(resetEmail.length()<1)
        {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }

        btnReset.startAnimation();
        Log.d(TAG, "Email: "+resetEmail);
        firebaseAuth.fetchSignInMethodsForEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
               if(task.isSuccessful())
               {
                //   Log.d(TAG, "SignInMethods: "+task.getResult().getSignInMethods().toString());
                   if(task.getResult().getSignInMethods().size()==0)
                   {
                       Log.d(TAG, "Size 0");
                       btnReset.revertAnimation(new OnAnimationEndListener() {
                           @Override
                           public void onAnimationEnd() {
                               Toast.makeText(getApplicationContext(), "This email is not registered.", Toast.LENGTH_LONG).show();
                               btnReset.setText("Try Again");
                               btnReset.setBackground(getResources().getDrawable(R.drawable.circular_border_shape));
                           }
                       });

                   }
                   else
                   {
                       Log.d(TAG, "Size: "+task.getResult().getSignInMethods().size());
                       firebaseAuth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful())
                               {
                                   btnReset.revertAnimation(new OnAnimationEndListener() {
                                       @Override
                                       public void onAnimationEnd() {
                                           btnReset.setText("Reset email sent. Tap to go back.");
                                           btnReset.setBackground(getResources().getDrawable(R.drawable.circular_border_shape));
                                           bEmailSent=true;
                                       }
                                   });
                               }
                           }
                       });
                   }
               }
               else
               {
                   btnReset.revertAnimation(new OnAnimationEndListener() {
                       @Override
                       public void onAnimationEnd() {
                           Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_LONG).show();
                           btnReset.setText("Try Again");
                           btnReset.setBackground(getResources().getDrawable(R.drawable.circular_border_shape));
                       }
                   });
                   Log.d(TAG, "failed: "+ task.getException() );
               }
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(internetStatusReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (animationDrawable != null && !animationDrawable.isRunning())
            animationDrawable.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(internetStatusReciever);
        if (animationDrawable != null && animationDrawable.isRunning())
            animationDrawable.stop();
    }
}

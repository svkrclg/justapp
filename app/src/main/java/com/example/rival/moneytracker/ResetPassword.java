package com.example.rival.moneytracker;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        edtResetEmail=(EditText) findViewById(R.id.resetEmail);
        btnReset=(CircularProgressButton) findViewById(R.id.btnReset);
        firebaseAuth= FirebaseAuth.getInstance();

    }
    public void Reset(View view)
    {
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
                               btnReset.setText("Email has not been registered. Try Again");
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
                                           btnReset.setText("Reset email sent.");
                                       }
                                   });
                               }
                           }
                       });
                   }
               }
               else
               {

                   Log.d(TAG, "failed: "+ task.getException() );
               }
            }
        });

    }
}

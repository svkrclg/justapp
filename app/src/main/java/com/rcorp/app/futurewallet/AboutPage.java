package com.rcorp.app.futurewallet;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class AboutPage extends AppCompatActivity implements View.OnClickListener {
private ImageView gp, gmail, github;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_about_page);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        gp=findViewById(R.id.gp);
        gmail=findViewById(R.id.gmail);
        github=findViewById(R.id.github);
        gp.setOnClickListener(this);
        gmail.setOnClickListener(this);
        github.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
       int i=v.getId();
       switch (i)
       {
           case R.id.gp:
               final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
               try {
                   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=8396490515339860162")));
               } catch (android.content.ActivityNotFoundException anfe) {
                   Toast.makeText(this, "Error occured, try again", Toast.LENGTH_SHORT).show();
               }
               break;
           case R.id.gmail:
               Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                       "mailto","svkrclg@gmail.com", null));
               emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello R.Corp ,");
               startActivity(Intent.createChooser(emailIntent, "Send email..."));
               break;
           case R.id.github:
               Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/svkrdj/justapp"));
               startActivity(browserIntent);
               break;
       }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

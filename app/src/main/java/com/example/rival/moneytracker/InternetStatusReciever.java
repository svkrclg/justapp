package com.example.rival.moneytracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InternetStatusReciever extends BroadcastReceiver {
    InetAddress ipAddr=null;
    boolean internet=false;
    Context ctx;
     Snackbar snackbar;
    public InternetStatusReciever(Snackbar snackbar)
    {
        this.snackbar=snackbar;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        ctx=context;
        new CheckNetwork().execute();
    }
public class CheckNetwork extends AsyncTask<Void, Void, Void>{
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(internet==false)
        {
         if(snackbar.isShown()==false)
             snackbar.show();
        }
        else
        {
            if(snackbar.isShown()==true)
                snackbar.dismiss();
        }

    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            ipAddr = InetAddress.getByName("google.com");
            if(ipAddr.getAddress().length==0)
            {
                internet=false;
            }
            else
               internet=true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d("Internet: ", "NO");
            internet=false;
        }
        return null;
    }
}
}

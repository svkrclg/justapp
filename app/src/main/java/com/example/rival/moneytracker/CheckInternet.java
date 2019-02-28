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

public class CheckInternet  {
    public static boolean isInternet=false;
    public static void setStatus(boolean status)
    {
     isInternet=status;
    }

}

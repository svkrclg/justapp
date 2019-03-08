package com.rcorp.app.futurewallet;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;

public  class MyFirebaseApp extends android.app.Application
{
        @Override
        public void onCreate() {
            super.onCreate();
            /* Enable disk persistence  */
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MobileAds.initialize(this, "ca-app-pub-9290929472111248~4486618072");
        }
}
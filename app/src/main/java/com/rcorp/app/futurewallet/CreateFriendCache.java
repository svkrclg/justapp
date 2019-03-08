package com.rcorp.app.futurewallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class CreateFriendCache {
    private SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private static DatabaseReference databaseReference;
    static String TAG= "FriendCache";
    static String uid;
    static int i=0;
    Context context;
    public CreateFriendCache(Context context){
        this.context=context;
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        uid=firebaseAuth.getUid();
        prefs= context.getSharedPreferences(context.getResources().getString(R.string.shared_pref_name), MODE_PRIVATE);
        editor=prefs.edit();
    }
    public static void LocalSaveOfFriend(){
        i=0;
        final HashMap<String, String > friendList=new HashMap<>();
        try {
            databaseReference.child("users").child(uid).child("friend").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final long count = dataSnapshot.getChildrenCount();
                    if (count == 0) {
                        Log.d("Friend", "c: " + count);
                        CreateHashAndSaveLocal();
                    }
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final String uid = ds.getKey();
                        i++;
                        Log.d(TAG, ds.getKey() + ", " + ds.getValue(Boolean.class) + "Chiren count " + dataSnapshot.getChildrenCount());
                        databaseReference.child("userNameByUid").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            int index = i;
                            String fuid = uid;

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String name = dataSnapshot.getValue(String.class);
                                databaseReference.child("userPhoneByUid").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    String fname = name;

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final String phone = dataSnapshot.getValue(String.class);
                                        friendList.put(fname, fuid + "_" + phone);
                                        Log.d(TAG, "index: " + index);
                                        if (count == index)
                                            CreateHashAndSaveLocal(friendList);

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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (NullPointerException e)
        {}
    }
    public static void CreateHashAndSaveLocal(HashMap<String, String > hashMap)
    {
        JSONObject jsonObject = new JSONObject(hashMap);
        String jsonString = jsonObject.toString();
        editor.remove("friendMap").commit();
        editor.putString("friendMap", jsonString);
        Log.d(TAG, "PREFS:"+jsonString);
        editor.commit();
    }
    public static void CreateHashAndSaveLocal()
    {
        editor.remove("friendMap").commit();
        editor.putString("friendMap", "Not found");
        editor.commit();
    }
}

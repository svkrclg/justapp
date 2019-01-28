package com.example.rival.moneytracker;

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
    private SharedPreferences.Editor editor;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    String TAG= "FriendCache";
    String uid;
    int i=0;
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
    public void LocalSaveOfFriend(){
        final HashMap<String, String > friendList=new HashMap<>();
        databaseReference.child("users").child(uid).child("friend").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final long count =dataSnapshot.getChildrenCount();
                for (DataSnapshot ds: dataSnapshot.getChildren()
                        ) {
                    i++;
                    final String uid=ds.getKey().toString();
                    Log.d(TAG,ds.getKey().toString()+", "+ds.getValue(Boolean.class) +"Chiren count "+dataSnapshot.getChildrenCount());
                    databaseReference.child("userNameByUid").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String name=dataSnapshot.getValue(String.class);
                            databaseReference.child("userPhoneByUid").child(uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final String phone=dataSnapshot.getValue(String.class);
                                    friendList.put(name, uid+"_"+phone);
                                    if(count==i)
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
    public void CreateHashAndSaveLocal(HashMap<String, String > hashMap)
    {
        JSONObject jsonObject = new JSONObject(hashMap);
        String jsonString = jsonObject.toString();
        editor.remove("friendMap").commit();
        editor.putString("friendMap", jsonString);
        editor.commit();
        Log.d(TAG, jsonString);
    }
}

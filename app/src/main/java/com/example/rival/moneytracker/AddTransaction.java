package com.example.rival.moneytracker;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AddTransaction extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public String uid;
    ArrayList<FriendListModel>  friendListModels;
    ListView listView;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    EditText Edtname;
    HashMap<String,String> FriendHashMap;
    FriendListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        uid=firebaseAuth.getUid();
        prefs= getSharedPreferences(getResources().getString(R.string.shared_pref_name), MODE_PRIVATE);
        editor=prefs.edit();
        listView=(ListView) findViewById(R.id.FriendListView);
        Edtname=(EditText) findViewById(R.id.Opponent);
        friendListModels=new ArrayList<>();
        FriendHashMap = new HashMap<>();
        String jsonString =prefs.getString("friendMap", "Not found");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Iterator<String> keysItr = jsonObject.keys();
        while(keysItr.hasNext()) {
            String k = keysItr.next();
            String v = null;
            try {
                v = (String) jsonObject.get(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            FriendHashMap.put(k,v);
        }
        Edtname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                  String input =s.toString();
                friendListModels.clear();
                  if(s.toString().length()<=2)
                  {
                      if(adapter!=null)
                      adapter.clear();
                      return;

                  }
                  HashMap<String, String> filteredHashmap=new HashMap<>();
                  filteredHashmap=getFilteredFriendList(input);
                  for(HashMap.Entry<String, String> entry: filteredHashmap.entrySet())
                  {
                      String name=entry.getKey();
                      String uid_phone[]=entry.getValue().split("_");
                      String uid=uid_phone[0];
                      String phone=uid_phone[1];
                      friendListModels.add(new FriendListModel(name,uid,phone));

                  }
                adapter= new FriendListAdapter(friendListModels,getApplicationContext());

                listView.setAdapter(adapter);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FriendListModel friendListModel= friendListModels.get(position);

                Snackbar.make(view, friendListModel.getName()+"\n"+friendListModel.getName()+" API: "+friendListModel.getuid()+", "+friendListModel.getPhone(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });



    }
    public HashMap<String,String> getFilteredFriendList(String s)
    {
        HashMap<String, String> filtered=new HashMap<>();
        for (HashMap.Entry<String, String> entry : FriendHashMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(key.startsWith(s.toUpperCase()))
                 filtered.put(key,value);
            // ...
        }
        return filtered;
    }
}

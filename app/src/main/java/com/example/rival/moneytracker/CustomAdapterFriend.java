package com.example.rival.moneytracker;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class CustomAdapterFriend extends RecyclerView.Adapter<CustomAdapterFriend.MyViewHolder> {
    public ArrayList<Character> firstLetter =new ArrayList<>();
    public ArrayList<String> name= new ArrayList<>();
    public ArrayList<String> uid= new ArrayList<>();
    public ArrayList<String> phone= new ArrayList<>();
    Context context;
    public CustomAdapterFriend(Context context) {
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // set the data in items
        holder.friendfirstLetter.setText(firstLetter.get(position)+"");
        holder.friendname.setText(name.get(position));
        holder.friendphone.setText(phone.get(position));
    }


    public void deleteItem(final int po)
    {
        Log.d(TAG, po+" Todelete");
        String toremoveuid=uid.get(po);
        firstLetter.remove(po);
        phone.remove(po);
        name.remove(po);
        uid.remove(po);
        IncomingRequest.databaseReference.child("users").child(IncomingRequest.uid).child("incomingRequest").child(toremoveuid).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.d(TAG, "DatabaseReferecne: "+databaseReference.toString());
                notifyDataSetChanged();
                notifyItemRangeChanged(po, phone.size());
            }
        });
    }
    @Override
    public int getItemCount() {
        return phone.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView friendfirstLetter;
        TextView friendname;
        TextView friendphone;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            friendname= (TextView) itemView.findViewById(R.id.friendname);
            friendfirstLetter = (TextView) itemView.findViewById(R.id.friendfirstLetter);
            friendphone=(TextView) itemView.findViewById(R.id.friendphone);
        }
    }
}

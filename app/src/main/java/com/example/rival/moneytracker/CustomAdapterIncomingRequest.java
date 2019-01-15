package com.example.rival.moneytracker;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class CustomAdapterIncomingRequest extends RecyclerView.Adapter<CustomAdapterIncomingRequest.MyViewHolder> {
    public ArrayList<Character> firstLetter =new ArrayList<>();
    public ArrayList<String> name= new ArrayList<>();
    public ArrayList<String> uid= new ArrayList<>();
    public ArrayList<String> phone= new ArrayList<>();
    Context context;
    Boolean fuck=true;
    public CustomAdapterIncomingRequest(Context context) {
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.incoming_request_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // set the data in items
        holder.firstLetter.setText(firstLetter.get(position)+"");
        holder.name.setText(name.get(position));
        holder.incomingphone.setText(phone.get(position));
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fuck=true;
                Log.d(TAG, "UID: "+ uid.toArray().toString()+", phone: "+phone.toArray().toString());
                notifyDataSetChanged();
                final int po=position;
                Log.d(TAG, po+" Tosend");
                deleteItem(po);
            }
        });
    }

    public void deleteItem(final int po)
    {
        if(fuck!=true)
            return;
        Log.d(TAG, po+" Todelete");
        String toremoveuid=uid.get(po);
        firstLetter.remove(po);
        phone.remove(po);
        name.remove(po);
        uid.remove(po);
        fuck=false;
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
        TextView firstLetter;
        TextView name;
        TextView incomingphone;
        Button decline;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name= (TextView) itemView.findViewById(R.id.incomingname);
            firstLetter = (TextView) itemView.findViewById(R.id.firstLetter);
            incomingphone=(TextView) itemView.findViewById(R.id.incomingphone);
            decline=(Button)itemView.findViewById(R.id.declineRequest);
        }
    }
}

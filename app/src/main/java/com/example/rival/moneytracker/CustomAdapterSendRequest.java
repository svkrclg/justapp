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
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class CustomAdapterSendRequest extends RecyclerView.Adapter<CustomAdapterSendRequest.MyViewHolder> {
    ArrayList<SendRequestPOJO> sendRequestPOJOS=new ArrayList<>();
    Context context;
    public CustomAdapterSendRequest(Context context, ArrayList<SendRequestPOJO> sendRequestPOJOS) {
        this.context = context;
        this.sendRequestPOJOS=sendRequestPOJOS;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_request_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // set the data in items
        SendRequestPOJO obj=sendRequestPOJOS.get(position);
        holder.firstLetter.setText(obj.getFirstLetter()+"");
        holder.name.setText(obj.getName());
        holder.incomingphone.setText(obj.getPhone());
    }

    @Override
    public int getItemCount() {
        return sendRequestPOJOS.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView firstLetter;
        TextView name;
        TextView incomingphone;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name= (TextView) itemView.findViewById(R.id.Sendname);
            firstLetter = (TextView) itemView.findViewById(R.id.sendfirstLetter);
            incomingphone=(TextView) itemView.findViewById(R.id.Sendphone);
        }
    }
}

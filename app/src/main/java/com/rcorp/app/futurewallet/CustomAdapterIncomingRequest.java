package com.rcorp.app.futurewallet;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class CustomAdapterIncomingRequest extends RecyclerView.Adapter<CustomAdapterIncomingRequest.MyViewHolder> {

    Context context;
    ArrayList<IncomingRequestPOJO> incomingRequestPOJOS= new ArrayList<>();
    public CustomAdapterIncomingRequest(Context context, ArrayList<IncomingRequestPOJO> incomingRequestPOJOS) {
        this.context = context;
        this.incomingRequestPOJOS=incomingRequestPOJOS;
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
        final IncomingRequestPOJO obj=incomingRequestPOJOS.get(position);
        holder.firstLetter.setText(obj.getFirstLetter()+"");
        holder.accept.setBackgroundResource(R.drawable.button_bg);
        holder.decline.setBackgroundResource(R.drawable.button_bg);
        holder.name.setText(obj.getName());
        holder.incomingphone.setText(obj.getPhone());
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundResource(R.drawable.button_bg_onclick);
                if(CheckInternet.isInternet==false)
                {
                    Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                deleteItem(obj.getUid());
            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               view.setBackgroundResource(R.drawable.button_bg_onclick);
                if(CheckInternet.isInternet==false)
                {
                    Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                addItem(obj.getUid());
                new CreateFriendCache(context).LocalSaveOfFriend();
            }
        });
    }

    public void addItem(String toAddUid)
    {
        IncomingRequest.databaseReference.child("users").child(IncomingRequest.uid).child("incomingRequest").child(toAddUid).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(context, "Request accepted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteItem(String toDeleteUId)
    {
        IncomingRequest.databaseReference.child("users").child(IncomingRequest.uid).child("incomingRequest").child(toDeleteUId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(context, "Request rejected.", Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public int getItemCount() {
        return incomingRequestPOJOS.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView firstLetter;
        TextView name;
        TextView incomingphone;
        Button decline;
        Button accept;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name= (TextView) itemView.findViewById(R.id.incomingname);
            firstLetter = (TextView) itemView.findViewById(R.id.firstLetter);
            incomingphone=(TextView) itemView.findViewById(R.id.incomingphone);
            decline=(Button)itemView.findViewById(R.id.declineRequest);
            accept=(Button)itemView.findViewById(R.id.acceptRequest);
        }
    }
}

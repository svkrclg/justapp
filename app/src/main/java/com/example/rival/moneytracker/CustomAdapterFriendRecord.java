package com.example.rival.moneytracker;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class CustomAdapterFriendRecord extends RecyclerView.Adapter<CustomAdapterFriendRecord.MyViewHolder> {
    private Context context;
    private ArrayList<friendRecordPOJO> list;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    String uid;
    public CustomAdapterFriendRecord(Context context, ArrayList<friendRecordPOJO> list) {
        this.context = context;
        this.list = list;
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        uid=firebaseAuth.getUid();
        databaseReference=firebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_record_row_layout,viewGroup,false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final friendRecordPOJO record=list.get(i);
        myViewHolder.time.setText(record.getTime());
        myViewHolder.amount.setText(record.getAmount()+"");
        myViewHolder.direction.setText(record.getDirection());
        myViewHolder.reason.setText(record.getReason());
        myViewHolder.addedBy.setText(record.getAddedByMe());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
    TextView amount;
    TextView reason, direction;
    TextView time, addedBy;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            amount=itemView.findViewById(R.id.amount);
            direction=itemView.findViewById(R.id.direction);
            reason=itemView.findViewById(R.id.reason);
            addedBy=itemView.findViewById(R.id.addedBy);
            time=itemView.findViewById(R.id.time);

        }
    }
}

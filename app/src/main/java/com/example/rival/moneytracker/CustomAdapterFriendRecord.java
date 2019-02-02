package com.example.rival.moneytracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

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
        if(record.getDirection().equals("going"))
                myViewHolder.amount.setText("-"+record.getAmount());
        else
            myViewHolder.amount.setText("+"+record.getAmount());
        myViewHolder.reason.setText(record.getReason()+"");
        myViewHolder.time.setText(record.getTime());
        int paddingDpLR = 25;
        float density = context.getResources().getDisplayMetrics().density;
        int paddingPixelLR = (int)(paddingDpLR * density);
        int paddingDpTB=15;
        int paddingPixelTB = (int)(paddingDpTB * density);

        if(record.getAddedByMe()==true)
        {
            myViewHolder.details.setBackgroundResource(R.drawable.addedbyme);
            RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)myViewHolder.details.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            RelativeLayout.LayoutParams params1=(RelativeLayout.LayoutParams)myViewHolder.time.getLayoutParams();
            params1.addRule(RelativeLayout.LEFT_OF, myViewHolder.details.getId());
            myViewHolder.details.setPadding(paddingPixelLR, paddingPixelTB, paddingPixelLR,paddingPixelTB);
        }
        else
        {

            myViewHolder.details.setBackgroundResource(R.drawable.addedbyoppn);
            RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)myViewHolder.details.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            RelativeLayout.LayoutParams params1=(RelativeLayout.LayoutParams)myViewHolder.time.getLayoutParams();
            params1.addRule(RelativeLayout.RIGHT_OF, myViewHolder.details.getId());
            myViewHolder.details.setPadding(paddingPixelLR, paddingPixelTB, paddingPixelLR,paddingPixelTB);

        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
    TextView amount;
    TextView reason;
    TextView time;
    RelativeLayout details;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            amount=itemView.findViewById(R.id.amount);
            details=itemView.findViewById(R.id.details);
            reason=itemView.findViewById(R.id.reason);
            time=itemView.findViewById(R.id.time);

        }
    }
}

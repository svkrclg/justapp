package com.rcorp.app.futurewallet;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ConfTranCardAdapter extends RecyclerView.Adapter<ConfTranCardAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<ConfTranClass> list;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    String uid;
    public ConfTranCardAdapter(Context context, ArrayList<ConfTranClass> list) {
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
        View itemView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.confirm_transacrion_row_layout,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        final ConfTranClass confTranClass=list.get(i);
        myViewHolder.opponentName.setText(confTranClass.getName());
        if(confTranClass.getDirection().equals("going"))
        {
            myViewHolder.amount.setText("- "+ "₹ "+confTranClass.getAmount());
            myViewHolder.opponentName.setBackgroundResource(R.drawable.name_bg_going);
        }
        else
        {
            myViewHolder.amount.setText("+ "+ "₹ "+confTranClass.getAmount());
            myViewHolder.opponentName.setBackgroundResource(R.drawable.name_bg_coming);
        }
        myViewHolder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Confirm "+ confTranClass.getOpponentUid());
                Intent i=new Intent(context, WithFriendRecord.class);
                i.putExtra("Name", confTranClass.getName());
                i.putExtra("OppnUid", confTranClass.getOpponentUid());
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
    TextView opponentName;
    TextView amount;
    RelativeLayout rl;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            amount=itemView.findViewById(R.id.amount);
            opponentName=itemView.findViewById(R.id.oppnname);
            rl=(RelativeLayout) itemView.findViewById(R.id.relativelayout);

        }
    }
}

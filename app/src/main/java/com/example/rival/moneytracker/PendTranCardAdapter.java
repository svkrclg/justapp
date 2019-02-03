package com.example.rival.moneytracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class PendTranCardAdapter extends RecyclerView.Adapter<PendTranCardAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<PendTranClass> list;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    String uid;
    public PendTranCardAdapter(Context context, ArrayList<PendTranClass> list) {
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
        View itemView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pending_transaction_row_layout,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        final PendTranClass pendTranClass=list.get(i);
        myViewHolder.opponentName.setText(pendTranClass.getName());
        myViewHolder.reason.setText(pendTranClass.getReason()+"");
        myViewHolder.amount.setText(pendTranClass.getAmount()+"");
        if(pendTranClass.getDirection().equals("coming"))
            myViewHolder.direction.setImageResource(R.drawable.incoming_money);
        else
            myViewHolder.direction.setImageResource(R.drawable.outgoing_money);
        if(pendTranClass.getAddedByMe()==true)
        {
            myViewHolder.confirm.setVisibility(View.GONE);
            myViewHolder.reject.setImageResource(R.drawable.delete);
        }
        myViewHolder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Reject "+ pendTranClass.getOpponentUid());
                Toast.makeText(context, "Deleting: "+ pendTranClass.getDateInMillis(), Toast.LENGTH_LONG).show();
                deletePendingTransaction(pendTranClass);
            }
        });
        myViewHolder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Confirm "+ pendTranClass.getOpponentUid());
                Toast.makeText(context, "Accpeting: "+ pendTranClass.getDateInMillis(), Toast.LENGTH_LONG).show();
                confirmPendingTransaction(pendTranClass);
            }
        });

    }
    public void confirmPendingTransaction(PendTranClass pendTranClass)
    {
           databaseReference.child("users").child(uid).child("pendingTransactions").child(pendTranClass.getDateInMillis()+"").child(uid).setValue(true);
           Log.d(TAG, "confirmPendingTransaction"+ pendTranClass.getDateInMillis());

    }
    public void deletePendingTransaction(PendTranClass pendTranClass)
    {

        Boolean isAddedByMe=pendTranClass.isAddedByMe;
        if(isAddedByMe==true)
        {
            databaseReference.child("users").child(uid).child("pendingTransactions").child(pendTranClass.getDateInMillis()+"").child(uid).setValue(false);
            Log.d(TAG, "DeletePendingTransaction 1: "+ pendTranClass.getDateInMillis());

        }
        else {
            databaseReference.child("users").child(uid).child("pendingTransactions").child(pendTranClass.getDateInMillis()+"").child(pendTranClass.getOpponentUid()).setValue(false);
            Log.d(TAG, "confirmPendingTransaction 2: "+ pendTranClass.getDateInMillis());

        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
    TextView opponentName, firstLetter;
    TextView amount, reason;
    ImageButton confirm, reject;
    ImageView direction;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            firstLetter=itemView.findViewById(R.id.firstLetter);
            amount=itemView.findViewById(R.id.amountPend);
            reason=itemView.findViewById(R.id.reasonPend);
            opponentName=itemView.findViewById(R.id.opponentName);
            confirm=itemView.findViewById(R.id.ConfirmTransaction);
            reject=itemView.findViewById(R.id.RejectTransaction);
            direction=itemView.findViewById(R.id.direction);
        }
    }
}

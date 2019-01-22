package com.example.rival.moneytracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PendTranCardAdapter extends RecyclerView.Adapter<PendTranCardAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<PendTranClass> list;

    public PendTranCardAdapter(Context context, ArrayList<PendTranClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pending_request_row_layout,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        PendTranClass pendTranClass=list.get(i);
        myViewHolder.opponentName.setText(pendTranClass.getName());
        myViewHolder.reason.setText(pendTranClass.getReason());
        myViewHolder.amount.setText(pendTranClass.getAmount());
        myViewHolder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(i);
                notifyDataSetChanged();
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
    TextView amount, reason;
    Button accept, reject;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            amount=itemView.findViewById(R.id.amountPend);
            reason=itemView.findViewById(R.id.reasonPend);
            opponentName=itemView.findViewById(R.id.opponentName);
            reject=itemView.findViewById(R.id.ConfirmTransaction);
            reject=itemView.findViewById(R.id.RejectTransaction);


        }
    }
}

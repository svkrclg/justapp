package com.example.rival.moneytracker;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
public class CustomAdapterIncomingRequest extends RecyclerView.Adapter<CustomAdapterIncomingRequest.MyViewHolder> {
    private ArrayList<Character> firstLetter;
    private ArrayList<String> name;
    private ArrayList<String> uid;
    private ArrayList<String> phone;
    Context context;
    public CustomAdapterIncomingRequest(Context context, ArrayList<Character> firstLetter, ArrayList<String> name, ArrayList<String> uid, ArrayList<String> incomingPhone) {
        this.context = context;
        this.firstLetter = firstLetter;
        this.name= name;
        this.uid = uid;
        this.phone=incomingPhone;
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items
        holder.firstLetter.setText(firstLetter.get(position));
        holder.name.setText(name.get(position));
        holder.incomingphone.setText(phone.get(position));
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast.makeText(context, uid.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return uid.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView firstLetter;
        TextView name;
        TextView incomingphone;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name= (TextView) itemView.findViewById(R.id.name);
            firstLetter = (TextView) itemView.findViewById(R.id.firstLetter);
            incomingphone=(TextView) itemView.findViewById(R.id.incomingphone);
        }
    }
}

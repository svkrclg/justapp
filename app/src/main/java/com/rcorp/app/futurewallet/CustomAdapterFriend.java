package com.rcorp.app.futurewallet;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterFriend extends RecyclerView.Adapter<CustomAdapterFriend.MyViewHolder> {
    public ArrayList<FriendPOJO> friendPOJOS;
    Context context;
    public CustomAdapterFriend(Context context , ArrayList<FriendPOJO> friendPOJOS) {
        this.context = context;
        this.friendPOJOS=friendPOJOS;
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
        FriendPOJO obj=friendPOJOS.get(position);
        holder.friendfirstLetter.setText(obj.getFirstLetter()+"");
        holder.friendname.setText(obj.getName());
        holder.friendphone.setText(obj.getPhone());
    }

    @Override
    public int getItemCount() {
        return friendPOJOS.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
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

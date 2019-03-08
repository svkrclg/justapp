package com.rcorp.app.futurewallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendListAdapter extends ArrayAdapter<FriendListModel> implements View.OnClickListener {
    private ArrayList<FriendListModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtPhone;
        TextView firstLetter;
    }

    public FriendListAdapter(ArrayList<FriendListModel> data, Context context) {
        super(context, R.layout.friend_list_model, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        FriendListModel FriendListModel=(FriendListModel)object;

        switch (v.getId())
        {
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FriendListModel FriendListModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.friend_list_model, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.Listfriendname);
            viewHolder.txtPhone = (TextView) convertView.findViewById(R.id.Listfriendphone);
            viewHolder.firstLetter = (TextView) convertView.findViewById(R.id.listFriendFirstLetter);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(FriendListModel.getName());
        viewHolder.txtPhone.setText(FriendListModel.getPhone());
        if(FriendListModel.getuid().equals("404"))
            viewHolder.firstLetter.setText("X");
        else
        viewHolder.firstLetter.setText(FriendListModel.getName().charAt(0)+"");
        // Return the completed view to render on screen
        return convertView;
    }
}

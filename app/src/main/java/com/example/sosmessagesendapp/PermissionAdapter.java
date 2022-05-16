package com.example.sosmessagesendapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.song.sosmessagesendapp.R;

import java.util.ArrayList;

public class PermissionAdapter extends RecyclerView.Adapter {

    Context mContext;
    ArrayList<PermissionItem> items;

    public PermissionAdapter(Context mContext, ArrayList<PermissionItem> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.permission_item, parent, false);
        VH holder = new VH(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VH vh= (VH) holder;

        Glide.with(mContext).load(items.get(position).pIcon).into(vh.icon);
        vh.title.setText(items.get(position).pTitle);
        vh.msg.setText(items.get(position).pMessage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class VH extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title;
        TextView msg;

        public VH(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.permission_item_icon);
            title = itemView.findViewById(R.id.permission_item_title);
            msg = itemView.findViewById(R.id.permission_item_msg);
        }
    }

}

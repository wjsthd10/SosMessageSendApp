package com.example.sosmessagesendapp;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.song.sosmessagesendapp.R;

import java.util.ArrayList;

public class PhoneNumberAdapter extends RecyclerView.Adapter {

    Context mContext;
    ArrayList<String> phoneNums;
    PhoneNumberDB helper;

    public PhoneNumberAdapter(Context mContext, ArrayList<String> phoneNums) {
        this.mContext = mContext;
        this.phoneNums = phoneNums;
        Log.e("yun_log", "phone num = "+phoneNums);
        helper = new PhoneNumberDB(mContext, "send_number.db", null, 1);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.phone_number_item, parent, false);
        VH holder=new VH(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VH vh= (VH) holder;
        int pos=position;
        vh.phoneTxt.setText(phoneNums.get(position));
        vh.phoneTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(phoneNums.get(pos)+"번호를 삭제 하시겠습니까?");
                builder.setMessage("번호를 삭제하시면 해당 번호를 발송 목록에서 제외합니다.");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //삭제동작
                        SQLiteDatabase db = helper.getWritableDatabase();
                        helper.onRemoveNumber(db, phoneNums.get(pos));
                        phoneNums.remove(pos);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.create().show();

                return false;
            }
        });
        vh.remveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(phoneNums.get(pos)+"번호를 삭제 하시겠습니까?");
                builder.setMessage("번호를 삭제하시면 해당 번호를 발송 목록에서 제외합니다.");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //삭제동작
                        SQLiteDatabase db = helper.getWritableDatabase();
                        helper.onRemoveNumber(db, phoneNums.get(pos));
                        phoneNums.remove(pos);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return phoneNums.size();
    }

    private class VH extends RecyclerView.ViewHolder {

        TextView phoneTxt;
        ImageView remveBtn;

        public VH(@NonNull View itemView) {
            super(itemView);
            phoneTxt = itemView.findViewById(R.id.phone_number_text);
            remveBtn = itemView.findViewById(R.id.phone_remove_btn);
        }
    }

}

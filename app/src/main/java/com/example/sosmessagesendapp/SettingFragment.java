package com.example.sosmessagesendapp;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.song.sosmessagesendapp.R;

public class SettingFragment extends Fragment implements View.OnClickListener {

    Context mContext;
    Handler mHandler;
    RecyclerView settingList;
    TextView canBtn, savBtn;
    private static final int SETTING_SAVE = 5001;
    private static final int SETTING_CANCEL = 5002;

    public SettingFragment(Context mContext, Handler mHandler){
        this.mContext=mContext;
        this.mHandler=mHandler;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.setting_fragment_lay, container, false);
        settingList = view.findViewById(R.id.setting_option_list);
        canBtn = view.findViewById(R.id.setting_can_btn);
        savBtn = view.findViewById(R.id.setting_sav_btn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        canBtn.setOnClickListener(this);
        savBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_can_btn:// 취소버튼
                try {
                    Message msg=new Message();
                    msg.what=SETTING_CANCEL;
                    mHandler.handleMessage(msg);
                }catch (Exception e){
                    Log.e("yun_log", "popBackStack Error");
                }
                break;
            case R.id.setting_sav_btn:// 저장버튼
                try {
                    Message msg=new Message();
                    msg.what=SETTING_SAVE;
                    mHandler.handleMessage(msg);
                }catch (Exception e){
                    Log.e("yun_log", "SavePopBackStack Error");
                }
                break;
        }
    }
}

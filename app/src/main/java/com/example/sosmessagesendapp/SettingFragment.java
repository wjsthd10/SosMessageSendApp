package com.example.sosmessagesendapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.song.sosmessagesendapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//import com.song.sosmessagesendapp.R;

public class SettingFragment extends Fragment implements View.OnClickListener {

    Context mContext;
    Handler mHandler;
    RecyclerView settingList;
    PhoneNumberAdapter adapter;

    TextView canBtn, savBtn, shakeValTxt;
    EditText phoneNumEditor;
    ImageView editBtn;
    SeekBar seekBar;
    AdView sAdView;
    private static final int SETTING_SAVE = 5001;
    private static final int SETTING_CANCEL = 5002;

    PhoneNumberDB dbHelper;
    SQLiteDatabase dbInsert;
    SQLiteDatabase dbSelect;

    ArrayList<String> sendNumberArr=new ArrayList<>();
    SharedPreferences pref;
    double mSeekbarVal=3.0;

    AdRequest adRequest;

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
        phoneNumEditor = view.findViewById(R.id.phone_number_editor);
        editBtn = view.findViewById(R.id.phone_number_add_btn);
        seekBar = view.findViewById(R.id.shake_value_seekbar);
        shakeValTxt = view.findViewById(R.id.shake_value_text);
        sAdView = view.findViewById(R.id.adView);
        return view;
    }



    @SuppressLint("Range")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        canBtn.setOnClickListener(this);
        savBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);

        adRequest = new AdRequest.Builder().build();
        Log.e("yun_log", "is TestDevice = "+adRequest.isTestDevice(mContext));

        sAdView.setAdSize(AdSize.BANNER);
        sAdView.setAdUnitId("ca-app-pub-6971751491716584/5696263742");
        sAdView.loadAd(adRequest);
        Log.e("yun_log", "adRequest = "+adRequest.getContentUrl());

        sAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("yun_log", "add load onAdFailedToLoad = "+loadAdError);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e("yun_log", "add load onAdLoaded");
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.e("yun_log", "add load onAdClicked");
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e("yun_log", "add load onAdClosed");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.e("yun_log", "add load onAdImpression");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.e("yun_log", "add load onAdOpened");
            }
        });

        pref = getContext().getSharedPreferences("SHAKE_VALUE_PREF", Context.MODE_PRIVATE);
        String prefVal=pref.getString("value", "3.0");
        seekBar.setProgress((int) (Double.parseDouble(prefVal)*10));
        shakeValTxt.setText("흔들림 감지 감도는 "+prefVal+"입니다.");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekbarVal=progress/(double)10;
                shakeValTxt.setText("흔들림 감지 감도는 "+mSeekbarVal+"입니다.");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        dbHelper = new PhoneNumberDB(mContext, "send_number.db", null, 1);
        dbInsert=dbHelper.getWritableDatabase();
        dbSelect=dbHelper.getReadableDatabase();
        dbHelper.onCreate(dbInsert);

        sendNumberArr.clear();
        Cursor c = dbSelect.query(dbHelper.getTableName(), null, null, null,null,null,null);
        while (c.moveToNext()) {
            sendNumberArr.add(c.getString(c.getColumnIndex("phoneNum")));
            Log.e("yun_log", "get data = "+c.getString(c.getColumnIndex("phoneNum")));
        }
        c.close();
        adapter = new PhoneNumberAdapter(mContext, sendNumberArr);
        settingList.setAdapter(adapter);

        phoneNumEditor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (phoneNumEditor.getText().length() > 9) {

                        if (!sendNumberArr.contains(phoneNumEditor.getText().toString())){
                            Log.e("yun_log", "get phone number = "+phoneNumEditor.getText().toString());
                            dbHelper.onInsertNumber(dbInsert, phoneNumEditor.getText().toString());
                            sendNumberArr.add(phoneNumEditor.getText().toString());
                            phoneNumEditor.setText("");
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                return false;
            }
        });


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
                    SharedPreferences.Editor editor=pref.edit();
                    editor.putString("value", mSeekbarVal+"");
                    editor.commit();

                    Message msg=new Message();
                    msg.what=SETTING_SAVE;
                    mHandler.handleMessage(msg);
                }catch (Exception e){
                    Log.e("yun_log", "SavePopBackStack Error");
                }
                break;
            case R.id.phone_number_add_btn:// 폰번호 저장 버튼
                if (phoneNumEditor.getText().length() > 9) {
                    if (!sendNumberArr.contains(phoneNumEditor.getText().toString())){
                        dbHelper.onInsertNumber(dbInsert, phoneNumEditor.getText().toString());
                        sendNumberArr.add(phoneNumEditor.getText().toString());
                        phoneNumEditor.setText("");
                        adapter.notifyDataSetChanged();
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(phoneNumEditor.getWindowToken(), 0);
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        phoneNumEditor.setText("");
    }
}

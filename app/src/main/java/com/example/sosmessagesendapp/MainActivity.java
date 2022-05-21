package com.example.sosmessagesendapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.song.sosmessagesendapp.R;

import java.util.ArrayList;

//import com.song.sosmessagesendapp.R;

public class MainActivity extends AppCompatActivity {// 인트로로 사용

    private static final int GPS_REQUEST_CODE=1001;
    private static final int PERMISSION_REQUEST_CODE=1000;
    private static final int SEND_MESSAGE_CODE=1002;

    boolean locationOk=false;
    boolean mmsOk=false;

    RecyclerView permissionListView;
    PermissionAdapter adapter;
    TextView pCancelBtn, pOkBtn;
    ArrayList<PermissionItem> pItems=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionListView = findViewById(R.id.permission_list_view);
        pCancelBtn = findViewById(R.id.permission_select_cancel);
        pOkBtn = findViewById(R.id.permission_select_ok);


    }

    private void permissionSet(){
        pItems.clear();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            pItems.add(new PermissionItem(R.drawable.ic_location_permission, "위치정보 권한","긴급 메시지에 첨부될 위치정보를 획득하기 위한 필수 권한입니다."));
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            pItems.add(new PermissionItem(R.drawable.ic_sms_permission, "SMS권한","긴급 메시지 발송을 위한 필수 권한입니다."));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        permissionSet();

        if (pItems.size() == 0) {
            Intent intent=new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
            finish();
        }else {
            adapter = new PermissionAdapter(this, pItems);
            permissionListView.setAdapter(adapter);
        }

        pCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLocationEnabled()) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.SEND_SMS
                            },
                            PERMISSION_REQUEST_CODE);
                }else {
                    setGPS_start();
                }
            }
        });

//        if (locationOk && mmsOk) {
//            Intent intent=new Intent(MainActivity.this, MapActivity.class);
//            startActivity(intent);
//            finish();
//        }else {
//            if (isLocationEnabled()) {
//                ActivityCompat.requestPermissions(MainActivity.this,
//                        new String[]{
//                                Manifest.permission.ACCESS_COARSE_LOCATION,
//                                Manifest.permission.ACCESS_FINE_LOCATION,
//                                Manifest.permission.SEND_SMS
//                        },
//                        PERMISSION_REQUEST_CODE);
//            }else {
//                setGPS_start();
//            }
//
//        }

    }

    private void setGPS_start(){
        android.app.AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("위치정보 설정이 비활성화 상태입니다.");
        builder.setMessage("위치정보 설정화면으로 이동하시겠습니까?");
        builder.setPositiveButton("이동", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("취소", null);
        builder.create().show();
    }

    public boolean isLocationEnabled(){// 위치정보 활성화 여부 리턴
        int locationMode=0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                locationMode= Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
            }catch (Exception e){
                e.getMessage();
                return false;
            }
            return locationMode!=Settings.Secure.LOCATION_MODE_OFF;
        }else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED
                    && grantResults[1]== PackageManager.PERMISSION_GRANTED
                    && grantResults[2]==PackageManager.PERMISSION_GRANTED){// 앱 이용 권한이 있을 경우

                if (isLocationEnabled()) {
                    Intent intent=new Intent(this, MapActivity.class);// 지도 화면 이동.
                    startActivity(intent);
                    finish();
                }else {
                    setGPS_start();
                }

            }else {
//                if (grantResults[1]!= PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(this, "권한을 획득하지 못하여 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
//                }else if (grantResults[2]!=PackageManager.PERMISSION_GRANTED){
//
//                }
                Toast.makeText(this, "권한을 획득하지 못하여 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                finish();

            }
        }
    }

}
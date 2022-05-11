package com.example.sosmessagesendapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naver.maps.map.MapView;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_LOCATION = 2022;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10021;

    private static final int NETWORK_DISCONNECTED=3001;
    private static final int NETWORK_CONNECTED=3002;
    private static final int SEND_SOS_MESSAGE=3003;
    private static final int SEND_LOCATION_MESSAGE=3004;
    private static final int SETTING_SAVE = 5001;
    private static final int SETTING_CANCEL = 5002;

    MapView mapView;
    NaverMap nMap;
    FusedLocationSource locationSource;

    SendSOSForeground mSosService;
    FloatingActionButton settingBtn;


    BroadcastReceiver br;

    Fragment sFragment;
    FragmentTransaction tran;
    FragmentManager fm;
//    private long shakeTime;// 흔들림 감지 시간
//    private static final int SHAKE_SKIP_TIME=500;// 연속 흔들림 감지 0.5초 뒤에 흔들림이 감지되면 무시
//    private static final float SHAKE_THRESHOLD_GRAVITY=2.7f;// 중력 가속도 높을 수록 강하게 흔들어야 감지가능 2.7f
//
//    SensorManager sensorManager;
//    Sensor sensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.map_view);
        settingBtn = findViewById(R.id.settingBtn);
        Log.e("yun_log", "onCreate mapview");

//        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
//        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        mapView.getMapAsync(this);
        Log.e("yun_log", "onCreate getMapAsync");
        BroadcastReceiver br2 = new BootCompleteReceiver();
        if (!br2.isOrderedBroadcast() && !br2.isInitialStickyBroadcast()) {
            Log.e("yun_log", "receiver set");
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
            registerReceiver(br2, intentFilter);
        }
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sFragment = new SettingFragment(MapActivity.this,sHandler);
                fm = getSupportFragmentManager();
                tran = fm.beginTransaction();
//                tran.add(R.id.top_view, sFragment, "setting");
                tran.replace(R.id.top_view, sFragment, "setting");
                tran.commitNowAllowingStateLoss();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e("yun_log", "permissions = " + Arrays.toString(permissions) + ", requestCode = " + requestCode);
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) {
                nMap.setLocationTrackingMode(LocationTrackingMode.Follow);
                nMap.setLocationSource(locationSource);
                Log.e("yun_log", "return None");
                nMap.setMaxZoom(21.0);
                nMap.setMinZoom(8.0);

            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.e("yun_log", "in onMapReady");
        Intent fIntent=new Intent(this, SendSOSForeground.class);
        bindService(fIntent, mConnection, Context.BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(fIntent);
        }else {
            startService(fIntent);
        }
        nMap = naverMap;
        UiSettings uiSettings = nMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        if (!locationSource.isActivated()) {
            if (locationSource.getLastLocation() != null) {
                Double lat =locationSource.getLastLocation().getLatitude();
                Double lng = locationSource.getLastLocation().getLongitude();
                LatLng latLng = new LatLng(lat, lng);
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latLng);
                nMap.moveCamera(cameraUpdate);
            }
            nMap.setLocationSource(locationSource);
            nMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            nMap.setMaxZoom(21.0);
            nMap.setMinZoom(8.0);
        }
    }

    private Location getLocation(){
        Location location=null;
        if (locationSource.getLastLocation() != null) {
            location=locationSource.getLastLocation();
        }
        return location;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SendSOSForeground.BindServiceBinder binder = (SendSOSForeground.BindServiceBinder) service;
            mSosService = binder.getService();
            mSosService.registerCallback(mCallback);
            Log.e( "yun_log","onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mSosService=null;
        }
    };

    private SendSOSForeground.ICallback mCallback = new SendSOSForeground.ICallback() {
        @Override
        public void remoteCallIsShaked() {// SOS신호
            Message message=new Message();
            message.what=SEND_SOS_MESSAGE;
            sHandler.sendMessage(message);
        }

        @Override
        public void scanLocation() {// 실시간 위치정보
            sHandler.sendEmptyMessage(SEND_LOCATION_MESSAGE);
        }
    };

    public void setCallback(){
        mCallback = new SendSOSForeground.ICallback() {
            @Override
            public void remoteCallIsShaked() {// SOS신호
                Message message=new Message();
                message.what=SEND_SOS_MESSAGE;
                sHandler.sendMessage(message);
                Log.e("yun_log", "in callback");
            }

            @Override
            public void scanLocation() {// 실시간 위치정보
                sHandler.sendEmptyMessage(SEND_LOCATION_MESSAGE);
            }
        };
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                SendSOSForeground.BindServiceBinder binder = (SendSOSForeground.BindServiceBinder) service;
                mSosService = binder.getService();
                mSosService.registerCallback(mCallback);
                Log.e( "yun_log","onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mSosService=null;
            }
        };
    }


    private Handler sHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){

                case NETWORK_CONNECTED:
                    Log.e("yun_log", "NETWORK_CONNECTED");
                    break;
                case NETWORK_DISCONNECTED:
                    Log.e("yun_log", "NETWORK_DISCONNECTED");
                    break;
                case SEND_SOS_MESSAGE:
                    // 구조신호 발송 코드
                    double lat = getLocation().getLatitude();
                    double lon = getLocation().getLongitude();
                    Geocoder geocoder=new Geocoder(MapActivity.this, Locale.getDefault());
                    List<Address> addresses;

                    try {
                        addresses = geocoder.getFromLocation(lat, lon, 7);

                        String add = addresses.get(0).getAddressLine(0);// 이 주소가 가장 정확 하더라...
//                        String country = addresses.get(0).getCountryName();//국가
//                        String city = addresses.get(0).getLocality();// 도시 단위
//                        String state = addresses.get(0).getAdminArea();// 시단위
//                        String subLocal = addresses.get(0).getSubLocality();//구단위
//                        String thoroughfare = addresses.get(0).getThoroughfare();// 동단위
//                        String premises = addresses.get(0).getPremises();//도로명 주소
//                        String phone = addresses.get(0).getPhone();// 번호

//                        String totalAddr = totalAddres(country, city, state, subLocal, thoroughfare, premises, phone);

                        ArrayList<String> sendNum=new ArrayList<>();// 쉐어드에 저장하여 불러오기
                        SharedPreferences pref = getSharedPreferences("SMS_SEND_NUMBER", MODE_PRIVATE);
                        sendNum.addAll(pref.getStringSet("PHONE_NUM", new HashSet<>()));
                        for (int i = 0; i < sendNum.size(); i++) {
                            sendSMS(sendNum.get(i), add);
                        }

//                        Toast.makeText(MapActivity.this, ""+totalAddr, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    startVib(2000);
                    break;

                case SEND_LOCATION_MESSAGE:// 위치정보 지속적으로 확인하는 코드

                    break;

                case SETTING_SAVE:// 설정 저장
                    finSetting();
                    Toast.makeText(MapActivity.this, "저장", Toast.LENGTH_SHORT).show();
                    break;
                case SETTING_CANCEL:// 설정 취소
                    finSetting();
                    break;
            }

        }
    };

    private void sendSMS(String phone, String location){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, location, null, null);
        Log.e("yun_log", "send message");
    }

    private String totalAddres(String country, String city, String state, String subLocal, String thoroughfare, String premises, String phone ){
        String totalAddr="";
        if (country != null){
            totalAddr+=country+" ";
        }
        if (city != null){
            totalAddr+=city+" ";
        }
        if (state != null){
            totalAddr+=state+" ";
        }
        if (subLocal != null){
            totalAddr+=subLocal+" ";
        }
        if (thoroughfare != null){
            totalAddr+=thoroughfare+" ";
        }
        if (premises != null){
            totalAddr+=premises+" ";
        }
        if (phone != null) {
            totalAddr+=phone;
        }

        return totalAddr;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getFragments().size() > 0) {
            Log.e("yun_log", "stack count = ");
            finSetting();
        }else {
            Log.e("yun_log", "backPressed");
            super.onBackPressed();
        }
    }

    private void finSetting(){
        fm.beginTransaction().remove(sFragment).commitNowAllowingStateLoss();
        fm.popBackStack();
    }

    public static String getHourMinuteSecond(long timeMillis) {
        long currentTime = (timeMillis == 0 ? System.currentTimeMillis() : timeMillis);
        Date date = new Date(currentTime);
        SimpleDateFormat aaa = new SimpleDateFormat("HH:mm:ss");
        String getTime = aaa.format(date);

        return getTime;
    }

    private void startVib(int vibPos){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(vibPos, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(vibPos);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
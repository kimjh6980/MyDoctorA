/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.khseob0715.sanfirst;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lylc.widget.circularprogressbar.CircularProgressBar;

import java.util.StringTokenizer;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.example.khseob0715.sanfirst.R.id;
import static com.example.khseob0715.sanfirst.R.layout.fragment_bluetooth_chat;
import static com.example.khseob0715.sanfirst.R.string;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {
    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // heart value
    private TextView heartvalue;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    ImageView heartanimg;	// 심장 이미지

    private LinearLayout bglayout = null;
    private ImageView weather = null;

    public CircularProgressBar hrseekbar;
    public TextView hrval;
    int seekstartval = 0;
    int seekendval = 0;
    TextView temperval;
    CircularProgressBar eachval1;
    CircularProgressBar eachval2;
    CircularProgressBar eachval3;
    CircularProgressBar eachval4;
    CircularProgressBar eachval5;
    CircularProgressBar eachval6;

    Button hrvalservice;

    private SharedPreferences prefs;

    PolarBleService mPolarBleService;
    String mpolarBleDeviceAddress="00:22:D0:A4:9D:83";  // 우리꺼
    int batteryLevel=0;
    public static int polarhrvalue = 0;

    //------------------------------
    String mDefaultDeviceAddress;

    public int i;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public BluetoothChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {	// 블투확인
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }

        prefs = getActivity().getSharedPreferences(HConstants.DEVICE_CONFIG, Context.MODE_MULTI_PROCESS);
        mDefaultDeviceAddress = prefs.getString(HConstants.CONFIG_DEFAULT_DEVICE_ADDRESS, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
        activatePolar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
        // 서비스가 종료될 때 실행
        deactivatePolar();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(fragment_bluetooth_chat, container, false);	// fragment 맞춘 레이아웃 설정
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        // setContentView 이전에 find를 할 시 NullPointerException이 발생함. 이에 따라, View가 Created 된 이후에 Find
        //heartvalue = (TextView) view.findViewById(id.heartvalue);

        // 심장 뛰는 애니메이션
        /*
        heartanimg = (ImageView) view.findViewById(id.heartani);
        final Animation heartbeat = AnimationUtils.loadAnimation(getActivity(), anim.heartbeat);
        heartbeat.setRepeatMode(Animation.REVERSE);	// 반복모드
        heartbeat.setRepeatCount(100);	// 반복횟수 (무한으로 돌리자)
        heartbeat.setStartOffset(1000);
        heartanimg.startAnimation(heartbeat);
*/
        bglayout = (LinearLayout)view.findViewById(id.fragment_image_bg);
        hrseekbar = (CircularProgressBar)view.findViewById(id.hrseekbar);

        hrval = (TextView)view.findViewById(id.receiveheartvalue);

        temperval = (TextView)view.findViewById(id.temperval);

        eachval1 = (CircularProgressBar)view.findViewById(id.coseekbar);


        bglayout.setBackgroundResource(R.drawable.bg_main);

        hrvalservice = (Button)view.findViewById(id.hrvalservice);
/*
        weather = (ImageView)view.findViewById(R.id.weathericon);

        heartseek = (CircularProgressBar) view.findViewById(id.heartrateseekbar);
*/

/*
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.e("heartval change", "heartval" + heartval.getText());
            }
        };
        /////////// / Timer 생성 ////////////// 주기
        Timer timer = new Timer();
        timer.schedule(tt, 0, 3000);*/

    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

        @SuppressLint("StringFormatInvalid")
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear(); 보여주는 화면 초기화라 필요 ㄴㄴ
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    // 받는 값 띄워주는거라 필요 ㄴㄴmConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    // ------------------------------------------------------------------------------------------------------[ BT Receive Data View Func ]
                    byte[] readBuf = (byte[]) msg.obj;  // Byte단위의 Message
                    // construct a string from the valid bytes in the buffer
                    // -------------------------------------------------------------------------------------------------------[ 여기서 BT 받아서 값 분별 ]

                    String readMessage = new String(readBuf, 0, msg.arg1);  // 이게 받는 내용

                    String[] BTSplit = readMessage.split(",");
                    int aqival = Integer.valueOf(BTSplit[0]);   // receive AQI value
                    int heartval = Integer.valueOf(BTSplit[1]); // receive HeartRate value
                    int temperatureval = Integer.valueOf(BTSplit[2]);   // receive temperatureval

                    int airval1 = Integer.valueOf(BTSplit[3]);
                    int airval2 = Integer.valueOf(BTSplit[4]);
                    int airval3 = Integer.valueOf(BTSplit[5]);
                    int airval4 = Integer.valueOf(BTSplit[6]);
                    int airval5 = Integer.valueOf(BTSplit[7]);
                    int airval6 = Integer.valueOf(BTSplit[8]);

                    aqiseekani(aqival);

                    seekendval = heartval;
                    // Run HeartRate Seekbar
                    heartseekani(seekstartval, seekendval);
                    temperval.setText(temperatureval);

                    // eachval = Cirlib find, airval = bt value
                    aireachval(eachval1, airval1);
                    aireachval(eachval2, airval2);
                    aireachval(eachval3, airval3);
                    aireachval(eachval4, airval4);
                    aireachval(eachval5, airval5);
                    aireachval(eachval6, airval6);

                    // 만약 여기서 BT값 받아서 분별까지 된다면 mConversation(이거 채팅기록 띄우는 List임) 이거 지우고 깔면 된다. (fragment_bluetooth_chat.xml)
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    public void heartseekani(int startval, int endval) {

        hrseekbar.animateProgressTo(startval, endval, new CircularProgressBar.ProgressAnimationListener() {
            @Override
            public void onAnimationStart() {
            }
            @Override
            public void onAnimationProgress(int progress) {
                hrseekbar.setTitle(progress + " bpm");
            }
            @Override
            public void onAnimationFinish() {
//                heartseek.setSubTitle("done");
            }
        });

        seekstartval = endval;
    }

    public void aqiseekani(int indexlevel)  {
        // aqi val에 따라 얼굴 변화 및 색변화
        if(indexlevel >= 0 && indexlevel <= 50)    {

        }   else if(indexlevel > 50 && indexlevel <= 100)    {

        }   else if(indexlevel > 100 && indexlevel <= 150)    {

        }   else if(indexlevel > 150 && indexlevel <= 200)  {

        }   else if(indexlevel > 200 && indexlevel <= 300)  {

        }   else if(indexlevel > 300 && indexlevel <= 500)  {

        }   else    {

        }
    }

    public void aireachval(final CircularProgressBar index, int value)    {
        index.animateProgressTo(0, value, new CircularProgressBar.ProgressAnimationListener() {
            @Override
            public void onAnimationStart() {
            }
            @Override
            public void onAnimationProgress(int progress) {
                index.setTitle(Integer.toString(progress));
            }
            @Override
            public void onAnimationFinish() {
//                heartseek.setSubTitle("done");
            }
        });
    }

    public static class heartvalthread implements Runnable    {
        private Handler handler = new Handler();

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
            try{
                Thread.sleep(3000);;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /*
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // 서비스가 호출될 때마다 실행
            activatePolar();
            return super.onStartCommand(intent, flags, startId);
        }
    */
    protected void activatePolar() {
        Log.w(this.getClass().getName(), "** activatePolar()");
        Intent gattactivatePolarServiceIntent = new Intent(getActivity(), PolarBleService.class);
        getActivity().bindService(gattactivatePolarServiceIntent, mPolarBleServiceConnection, BIND_AUTO_CREATE);
        getActivity().registerReceiver(mPolarBleUpdateReceiver, makePolarGattUpdateIntentFilter());
    }

    protected void deactivatePolar() {
        Log.w(this.getClass().getName(), "deactivatePolar()");
        try{
            if(mPolarBleService!=null){
                Log.w(this.getClass().getName(), "**** unbindService()");
                getActivity().unbindService(mPolarBleServiceConnection);
                Log.w(this.getClass().getName(), "bindService()");
            }
        }catch(Exception e){
            Log.e(this.getClass().getName(), e.toString());
        }

        try{
            mPolarBleService.unregisterReceiver(mPolarBleUpdateReceiver);
        }catch(Exception e){
            Log.e(this.getClass().getName(), e.toString());
        }
    }

    private final BroadcastReceiver mPolarBleUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            //Log.w(TAG, "####BroadcastReceiver Polar BLE Service ");

            final String action = intent.getAction();
            if (PolarBleService.ACTION_GATT_CONNECTED.equals(action)) {
            } else if (PolarBleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.w(this.getClass().getName(), "mPolarBleUpdateReceiver received ACTION_GATT_DISCONNECTED");
            } else if (PolarBleService.ACTION_HR_DATA_AVAILABLE.equals(action)) {

                //heartRate+";"+pnnPercentage+";"+pnnCount+";"+rrThreshold+";"+bioHarnessSessionData.totalNN
                //String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                StringTokenizer tokens = new StringTokenizer(data, ";");
                int hr = Integer.parseInt(tokens.nextToken());
                int prrPercenteage = Integer.parseInt(tokens.nextToken());
                int prrCount = Integer.parseInt(tokens.nextToken());
                int rrThreshold = Integer.parseInt(tokens.nextToken());	//50%, 30%, etc.
                int rrTotal = Integer.parseInt(tokens.nextToken());
                int rrValue = Integer.parseInt(tokens.nextToken());
                long sid = Long.parseLong(tokens.nextToken());

                Log.e(this.getClass().getName(), "MD: Heart rate is " + hr);
                polarhrvalue = hr;
                //sethrtext(hr);

            }else if (PolarBleService.ACTION_BATTERY_DATA_AVAILABLE.equals(action)) {
                //String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                batteryLevel = Integer.parseInt(data);
            }else if (PolarBleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                //String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                StringTokenizer tokens = new StringTokenizer(data, ";");
                int totalNN = Integer.parseInt(tokens.nextToken());
                long lSessionId = Long.parseLong(tokens.nextToken());


            }
        }
    };

    private static IntentFilter makePolarGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PolarBleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(PolarBleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(PolarBleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(PolarBleService.ACTION_HR_DATA_AVAILABLE);
        intentFilter.addAction(PolarBleService.ACTION_BATTERY_DATA_AVAILABLE);
        return intentFilter;
    }

    private final ServiceConnection mPolarBleServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mPolarBleService = ((PolarBleService.LocalBinder) service).getService();
            if (!mPolarBleService.initialize()) {
                Log.e(this.getClass().getName(), "Unable to initialize Bluetooth");
                try {
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            // Automatically connects to the device upon successful start-up initialization.
            //mPolarBleService.connect(app.polarBleDeviceAddress, false);
            mPolarBleService.connect(mpolarBleDeviceAddress, false);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            mPolarBleService = null;
        }
    };
}
package com.ppsoclab.ppsoc3;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.ppsoclab.ppsoc3.Fragments.ChartFragment;
import com.ppsoclab.ppsoc3.Fragments.ConnectFragment;
import com.ppsoclab.ppsoc3.Fragments.Zun1Fragment;
import com.ppsoclab.ppsoc3.Fragments.Zun2Fragment;
import com.ppsoclab.ppsoc3.Interfaces.DataListener;
import com.ppsoclab.ppsoc3.Interfaces.ModeChooseListener;
import com.ppsoclab.ppsoc3.Interfaces.SetListener;
import com.ppsoclab.ppsoc3.Interfaces.ZunDataListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ModeActivity extends AppCompatActivity implements ModeChooseListener, BluetoothAdapter.LeScanCallback, SetListener, View.OnClickListener {
    ProgressDialog progressDialog;
    FragmentManager fragmentManager;
    Fragment fragment;
    BluetoothAdapter bluetoothAdapter;
    Handler handler;
    HandlerThread handlerThread;
    Toast toast;
    Context context;
    BluetoothGattCallback bluetoothGattCallback;
    BluetoothGatt bluetoothGatt;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    List<BluetoothGattCharacteristic> characteristics;
    BluetoothGattCharacteristic characteristic;
    BluetoothGattCharacteristic characteristicSet;
    InputStream inputStream;
    final static String TARGET_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    DataListener dataListener;
    ZunDataListener zunDataListener;
    Button switchF;
    FragmentTransaction fragmentTransaction;
    byte[] temp;
    int position = 0;


    boolean isConnected = false;

    byte[] data;


    private static final String TAG = "ModeActivity";
    private static final String MODE_NAME_1 = "GigaFu-F081";
    private static final String MODE_NAME_2 = "RNBT-8CBB";
    private static final String THREAD_NAME = "ConnectProcess";

    private static final String SET_CH_ID = "0000fff7-0000-1000-8000-00805f9b34fb";
    private static final String NOTI_CH_ID = "0000fff6-0000-1000-8000-00805f9b34fb";
    private static final String DEVICE_UUID = "1f26fc65-0099-423c-637c-c99027417e7e";


    private static final int PACKET_SIZE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        switchF = (Button) findViewById(R.id.switchF);
        switchF.setOnClickListener(this);
        switchF.setVisibility(View.INVISIBLE);
        switchF.setClickable(false);
        temp = new byte[PACKET_SIZE];
        context = getApplicationContext();
        /*BLE setup*/
        final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(this.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if(newState == BluetoothProfile.STATE_CONNECTED) {
                    fragment = new Zun1Fragment();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container,fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();
                    position = 0;
                    zunDataListener = (Zun1Fragment) fragment;
                    progressDialog.cancel();
                    bluetoothGatt.discoverServices();
                    isConnected = true;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                List<BluetoothGattService> services = bluetoothGatt.getServices();
                Log.w(TAG,"services size: " + services.size());
                for (BluetoothGattService service : services) {
                    characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics){
                        Log.w("Char ID",characteristic.getUuid().toString());
                    }
                }
                Log.w(TAG,"chara size:"+characteristics.size());
                if(characteristics.get(0).getUuid().equals(UUID.fromString(NOTI_CH_ID))) {
                    characteristic = characteristics.get(0);
                    bluetoothGatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);
//                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.nameUUIDFromBytes(TARGET_UUID.getBytes()));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bluetoothGatt.writeDescriptor(descriptor);
                }
                if(characteristics.get(1).getUuid().equals(UUID.fromString(SET_CH_ID))){
                    characteristicSet = characteristics.get(1);
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristicLocal) {
                super.onCharacteristicChanged(gatt, characteristic);
                if (characteristic.equals(characteristicLocal)) {
                    data = characteristic.getValue();
                    zunDataListener.onDataFire(data);
//                    String str = "Data array : ";
//                    for (byte b : data){
//                        str += ByteParse.sIN16FromByte(b)+", ";
//                    }
//                    Log.w(TAG,str);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
            }
        };
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new ConnectFragment();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
        toast = Toast.makeText(this,"", Toast.LENGTH_SHORT);
        handlerThread = new HandlerThread(THREAD_NAME);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        if (!bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
        }
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            toast.setText("BLE not support");
            toast.show();
            finish();
        }
    }

    @Override
    public void doAfterModeChose(int mode) {
        progressDialog = ProgressDialog.show(this, "Please wait", "Connecting......", true);
        switch (mode) {
            case 0:
                switchF.setVisibility(View.VISIBLE);
                switchF.setClickable(true);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothAdapter.startLeScan(ModeActivity.this);
                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isConnected) {
                            bluetoothAdapter.stopLeScan(ModeActivity.this);
                            progressDialog.cancel();
                        }
                    }
                },5000);
                break;
            case 1:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        findBT();
                    }
                });
                break;
        }
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        if(bluetoothDevice.getName().equals(MODE_NAME_1)){
            bluetoothGatt = bluetoothDevice.connectGatt(this, false, bluetoothGattCallback);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG,"stop");
        bluetoothGattCallback = null;
        bluetoothAdapter.stopLeScan(this);
        bluetoothGatt.disconnect();
        bluetoothGatt.close();
        bluetoothAdapter.cancelDiscovery();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        bluetoothGatt.disconnect();
//        bluetoothGatt.close();
//    }

    private void findBT() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(MODE_NAME_2)) {
                    bluetoothDevice = device;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            openBT();
                        }
                    });
                    break;
                } else {
                    progressDialog.cancel();
                    Toast.makeText(this,"Connect attempt failed",Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "no match");
                }
            }
        } else {
            progressDialog.cancel();
            Toast.makeText(this,"Connect attempt failed",Toast.LENGTH_SHORT).show();
            Log.w(TAG, "no paired device");
        }
    }

    private void openBT() {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard
        // SerialPortService
        // ID
//        UUID uuid = bluetoothDevice.getUuids()[0].getUuid();
        try {
            Log.w(TAG, "Trying to connect with standard method");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            if (!bluetoothSocket.isConnected()) {
                bluetoothSocket.connect();
                Log.w(TAG, "Device connected with standard method");
                fragment = new ChartFragment();
                fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
                dataListener = (ChartFragment) fragment;
                inputStream = bluetoothSocket.getInputStream();
                progressDialog.cancel();
                Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();
                handler.post(dataListen);
            }
        } catch (IOException e) {
            progressDialog.cancel();
            Toast.makeText(this,"Connect attempt failed",Toast.LENGTH_SHORT).show();
            Log.w(TAG, e.toString());
        }
    }

    Runnable dataListen = new Runnable() {
        @Override
        public void run() {
            while (bluetoothSocket.isConnected()) {
                try {
                    if (inputStream.available() >= PACKET_SIZE) {
                        inputStream.read(temp);
                        if(ByteParse.sIN16FromByte(temp[0]) == 170) {
                            dataListener.onDataReceived(temp);
                        } else {
                            inputStream.skip(1);
                        }
                    }
                } catch (IOException e) {
                    Log.w(TAG,e.toString());
                }
            }
        }
    };

    @Override
    public void onSet(byte b) {
//        BluetoothGattDescriptor bluetoothGattDescriptor = characteristicSet.getDescriptors().get(0);
        byte[] value = new byte[2];
        value[0] = b;
        value[1] = 8;
        characteristicSet.setValue(value);
        bluetoothGatt.writeCharacteristic(characteristicSet);
//        bluetoothGattDescriptor.setValue(value);
//        bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
        Log.w(TAG,"set:" + value[0]);
    }

    @Override
    public void onClick(View v) {
        if(position == 0) {
            position++;
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragment = new Zun2Fragment();
            zunDataListener = (Zun2Fragment) fragment;
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else {
            position = 0;
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragment = new Zun1Fragment();
            zunDataListener = (Zun1Fragment) fragment;
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        }
    }
}

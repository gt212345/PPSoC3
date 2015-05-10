package com.ppsoclab.ppsoc3;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.Toast;

import com.ppsoclab.ppsoc3.Fragments.ChartFragment;
import com.ppsoclab.ppsoc3.Fragments.ConnectFragment;
import com.ppsoclab.ppsoc3.Interfaces.DataListener;
import com.ppsoclab.ppsoc3.Interfaces.ModeChooseListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ModeActivity extends AppCompatActivity implements ModeChooseListener, BluetoothAdapter.LeScanCallback {
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
    InputStream inputStream;
    UUID TARGET_UUID;
    DataListener dataListener;


    boolean isConnected = false;

    byte[] data;


    private static final String TAG = "ModeActivity";
    private static final String MODE_NAME_1 = "GigaFu-F081";
    private static final String MODE_NAME_2 = "";
    private static final String THREAD_NAME = "ConnectProcess";

    private static final int PACKET_SIZE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        /*BLE setup*/
        final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(this.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if(newState == BluetoothProfile.STATE_CONNECTED) {
                    bluetoothGatt.discoverServices();
                    isConnected = true;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                Log.w(TAG,"onServicesDiscovered");
                List<BluetoothGattService> services = bluetoothGatt.getServices();
                for (BluetoothGattService service : services) {
                    characteristics = service.getCharacteristics();
                }
                characteristic = characteristics.get(5);
                bluetoothGatt.setCharacteristicNotification(characteristic, true);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
                Log.w(TAG, "notify");
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristicLocal) {
                super.onCharacteristicChanged(gatt, characteristic);
                if (characteristic.equals(characteristicLocal)) {
                    data = characteristic.getValue();
                    String str = "Data array : ";
                    for (byte b : data){
                        str += ByteParse.sIN16FromByte(b)+", ";
                    }
                    Log.w(TAG,str);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
            }
        };
        setContentView(R.layout.activity_mode);
        fragmentManager = getFragmentManager();
        fragment = new ConnectFragment();
        fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
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
//        if(bluetoothDevice.){
            bluetoothGatt = bluetoothDevice.connectGatt(this, false, bluetoothGattCallback);
//        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetoothGatt.disconnect();
        bluetoothGatt.close();
    }

    private void findBT() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter
                .getBondedDevices();
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
                        byte[] temp = new byte[PACKET_SIZE];
                        inputStream.read(temp);
                        dataListener.onDataReceived(temp);
                    }
                } catch (IOException e) {
                    Log.w(TAG,e.toString());
                }
            }
        }
    };
}

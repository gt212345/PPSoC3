package com.ppsoclab.ppsoc3;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.ppsoclab.ppsoc3.Fragments.ConnectFragment;
import com.ppsoclab.ppsoc3.Interfaces.ModeChooseListener;
import java.util.List;

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
    List<BluetoothGattCharacteristic> characteristics;

    byte[] data;


    private static final String TAG = "ModeActivity";
    private static final int MODE_1 = 0;
    private static final int MODE_2 = 1;
    private static final String MODE_NAME_1 = "GigaFu-F081";
    private static final String MODE_NAME_2 = "";
    private static final String THREAD_NAME = "ConnectProcess";

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
                bluetoothGatt.discoverServices();
                Log.w(TAG,"Discovering services");
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                List<BluetoothGattService> services = bluetoothGatt.getServices();
                for (BluetoothGattService service : services) {
                    characteristics = service.getCharacteristics();
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                data = characteristic.getValue();
                for(int temp : data){
                    Log.w(TAG,temp+" ");
                }

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
        handler.post(new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.startLeScan(ModeActivity.this);
            }
        });
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
//        if(bluetoothDevice.getName() == MODE_NAME_1){
            bluetoothGatt = bluetoothDevice.connectGatt(this, false, bluetoothGattCallback);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetoothGatt.disconnect();
        bluetoothGatt.close();
    }
}

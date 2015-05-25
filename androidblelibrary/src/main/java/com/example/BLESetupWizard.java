package com.example;

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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * For setting up BLE connection
 */
public class BLESetupWizard implements  BluetoothAdapter.LeScanCallback{
    Context context;
    ProgressDialog progressDialog;
    BluetoothAdapter bluetoothAdapter;
    BluetoothGatt bluetoothGatt;
    BluetoothGattCallback bluetoothGattCallback;
    List<BluetoothGattCharacteristic> characteristics;
    BluetoothDevice bluetoothDevice;
    BluetoothGattCharacteristic bluetoothGattCharacteristic;
    BluetoothGattCharacteristic bluetoothGattCharacteristicSet;
    Toast toast;
    OnDataTransferListener listener;

    byte[] data;

    Handler handler;
    HandlerThread handlerThread;

    private static String DEVICE_NAME = "GigaFu-F081";
    private static final String SET_CH_ID = "0000fff7-0000-1000-8000-00805f9b34fb";
    private static final String NOTI_CH_ID = "0000fff6-0000-1000-8000-00805f9b34fb";

    public BLESetupWizard(Context context) {
        this.context = context;
        progressDialog = ProgressDialog.show(context, "Please wait", "Connecting......", true);
        toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
        handlerThread = new HandlerThread("SetupThread");
        handler = new Handler(handlerThread.getLooper());
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if(newState == BluetoothProfile.STATE_CONNECTED) {
                    progressDialog.cancel();
                    bluetoothGatt.discoverServices();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                List<BluetoothGattService> services = bluetoothGatt.getServices();
                for (BluetoothGattService service : services) {
                    characteristics = service.getCharacteristics();
//                    for (BluetoothGattCharacteristic characteristic : characteristics){}
                }
                if(characteristics.get(0).getUuid().equals(UUID.fromString(NOTI_CH_ID))) {
                    bluetoothGattCharacteristic = characteristics.get(0);
                    bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
                    BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic.getDescriptors().get(0);
//                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.nameUUIDFromBytes(TARGET_UUID.getBytes()));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bluetoothGatt.writeDescriptor(descriptor);
                }
                if(characteristics.get(1).getUuid().equals(UUID.fromString(SET_CH_ID))){
                    bluetoothGattCharacteristicSet = characteristics.get(1);
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristicLocal) {
                super.onCharacteristicChanged(gatt, characteristicLocal);
                if (bluetoothGattCharacteristic.equals(characteristicLocal)) {
                    data = bluetoothGattCharacteristic.getValue();
                    if (listener != null) {
                        listener.onDataTransfer(ByteParse.sIN16From2Byte(data[8], data[9]) / 128,ByteParse.sIN16From2Byte(data[10], data[11]) / 128,ByteParse.sIN16From2Byte(data[12],data[13]) / 128);
                    }
                }
            }
        };
        if (!bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(intent);
        }
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            toast.setText("BLE not support");
            toast.show();
        }
    }

    public void setDeviceName(String DEVICE_NAME) {
        this.DEVICE_NAME = DEVICE_NAME;
    }

    public void setOnDataTransferListener (OnDataTransferListener listener) {
        this.listener = listener;
    }

    public void startScanning(int millisecond) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.startLeScan(BLESetupWizard.this);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.stopLeScan(BLESetupWizard.this);
                progressDialog.cancel();
                toast.setText("Device not found");
                toast.show();
            }
        },millisecond);
    }


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if(bluetoothDevice.getName().equals(DEVICE_NAME)){
            bluetoothGatt = bluetoothDevice.connectGatt(context, false, bluetoothGattCallback);
        }
    }
}

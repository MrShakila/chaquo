package com.example.chaquo;

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
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chaquo.R;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Bluetooth adapter
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Start scanning for devices
        bluetoothAdapter.startLeScan(leScanCallback);
    }

    // Define the callback for when a device is found
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            // Check if the device is a Muse 2 headband
            System.out.println(device.getName());
            if (device.getName().contains("Muse")) {
                // Connect to the device
                connectToDevice(device);
            }
        }
    };

    private void connectToDevice(BluetoothDevice device) {
        // Connect to the device
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
    }

    // Define the callback for when the connection is established
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // The connection was successful, so discover the services
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // Get the EEG service
            BluetoothGattService eegService = gatt.getService(UUID.fromString("0000fe8d-0000-1000-8000-00805f9b34fb"));
            if (eegService != null) {
                // Get the EEG characteristic
                BluetoothGattCharacteristic eegCharacteristic = eegService.getCharacteristic(UUID.fromString("273e0006-4c4d-454d-96be-f03bac821358"));
                if (eegCharacteristic != null) {
                    // Enable notifications for the EEG characteristic
                    gatt.setCharacteristicNotification(eegCharacteristic, true);

                    // Get the descriptor for the EEG characteristic
                    BluetoothGattDescriptor eegDescriptor = eegCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                    if (eegDescriptor != null) {
                        // Enable notifications
                        eegDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(eegDescriptor);
                        System.out.println("hahah");
                        System.out.println("hahah");
                        System.out.println("hahah");
                        System.out.println("hahah");
                        System.out.println("hahah");
                        System.out.println("hahah");


                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // Check if the changed characteristic is the EEG characteristic
            if (characteristic.getUuid().equals(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))) {
                // Get the updated EEG data
                byte[] eegData = characteristic.getValue();
                // Update the UI with the EEG data
                updateEegData(eegData);
            }
        }

        private void updateEegData(byte[] eegData) {
            // Convert the EEG data to a string
            String eegString = new String(eegData);

            // Update the TextView with the EEG data
            TextView eegTextView = findViewById(R.id.textView);
            eegTextView.setText(eegString);
        }
    };}
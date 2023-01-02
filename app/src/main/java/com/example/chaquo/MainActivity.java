package com.example.chaquo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    EditText num1, num2;
    Button btn;
    TextView txt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private EEGReceiver receiver;

    private BluetoothDevice museDevice;
    // TextView museview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        num1 = (EditText) findViewById(R.id.num1);
        num2 = (EditText) findViewById(R.id.num2);
        btn = (Button) findViewById(R.id.btn);
        txt = (TextView) findViewById(R.id.textView);
        num1.setVisibility(View.INVISIBLE);
        num2.setVisibility(View.INVISIBLE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        scanner = bluetoothAdapter.getBluetoothLeScanner();

        // Create the EEG receiver
        receiver = new EEGReceiver(this);

        // Start scanning for BLE devices

        try {
            scanner.startScan(scanCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
            System.out.println("python script running");
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Python py = Python.getInstance();
                    PyObject pyObject = py.getModule("script");
                    PyObject obj = pyObject.callAttr("main");
                    txt.setText(obj.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            // Check if the device is the desired BLE device
            System.out.println(device.getName());
            if (device.getName().contains("Muse")) {
                // Stop scanning
               // museview.setText("Muse Found");
                scanner.stopScan(scanCallback);
                // Connect to the device and start receiving data
                connectToDevice(device);
            }
        }


        // Method to connect to the BLE device and start receiving data
        private void connectToDevice(BluetoothDevice device) {
            // Connect to the BLE device
            BluetoothGatt gatt = device.connectGatt(getApplicationContext(), false, gattCallback);
          //  museview.setText("Muse Connected");
            connectToMuse(device);
            // Enable notifications for the EEG data characteristic
        BluetoothGattService service = gatt.getService(UUID.fromString("0000fe8d-0000-1000-8000-00805f9b34fb"));
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("273e0004-4c4d-454d-96be-f03bac821358"));
        gatt.readCharacteristic(characteristic)
        gatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
        }

        // Callback for GATT events
        private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // Discover services
                    gatt.discoverServices();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                // Services have been discovered
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                // EEG data characteristic has changed
                byte[] data = characteristic.getValue();
                // Process the data here
                receiver.processData(data);
            }
        };

        // Method to connect to the Muse 2 headband and start receiving data
//            private void connectToMuse() {
//                // Find the Muse 2 headband's EEG stream
//                LSL.StreamInfo[] results = LSL.resolve_streams();
//                LSL.StreamInfo info = results;
//
//                // Open the EEG stream
//                LSL.StreamInlet inlet = null;
//                try {
//                    inlet = new LSL.StreamInlet(info);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                // Create a buffer to hold the sample data
//                //double[] sample = new double[inlet.channel_count()];
//
//                // Start a thread to receive data from the Muse 2 headband
////                new Thread(() -> {
////                    while (true) {
////                        // Pull a sample from the stream
////                        double timestamp = inlet.pull_sample(sample);
////
////                        // Process the sample data
////                        processData(timestamp, sample);
////                    }
////                }).start();
//            }

        // Method to process the data received from the Muse 2 headband
        private void processData(double timestamp, double[] channels) {
            // Calculate the alpha activity from the AF7 and AF8 channels
            double alpha = (channels[1] + channels[2]) / 2;

            // Do something with the alpha data
            // ...
        }

        private void connectToMuse(BluetoothDevice device) {
            // Initialize the Bluetooth adapter
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            museDevice = device;

            // Check if Bluetooth is supported
//            if (bluetoothAdapter == null) {
//                Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Check if Bluetooth is enabled
//            if (!bluetoothAdapter.isEnabled()) {
//                // Request Bluetooth permission
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                return;
//            }
//
//            // Start a thread to scan for the Muse 2 headband

            // Get the list of paired devices


            // Find the Muse 2 headba



            // Connect to the Muse 2 headband
            if (museDevice != null) {
              //  String streamName = findEEGStream(museDevice);
               // System.out.println(streamName);
//                    private void getAlphaData(BluetoothDevice museDevice) {
//
//                        // Create an inlet for the EEG stream provided by the headband
//                        LSL.StreamInlet inlet = LSL.StreamInlet.open_by_bluetooth_device(LSL.StreamInfo.from_bluetooth_device(museDevice));
//
//                        // Create a buffer to hold the sample data
//                        double[] sample = new double[inlet.channel_count()];
//
//                        // Start a thread to receive data from the Muse 2 headband
//                        new Thread(() -> {
//                            while (true) {
//                                // Pull a sample from the stream
//                                double timestamp = inlet.pull_sample(sample);
//
//                                // Calculate the alpha activity from the AF7 and AF8 channels
//                                double alpha = (sample[1] + sample[2]) / 2;
//
//                                // Do something with the alpha data
//                                processAlpha(timestamp, alpha);
//                            }
//                        }).start();
//                    }

                // Method to process the alpha data received from the Muse 2 headband
//                    private void processAlpha(double timestamp, double alpha) {
//                        // Do something with the alpha data
//                        // For example, update the UI with the alpha value
//                        runOnUiThread(() -> {
//                            TextView alphaTextView = findViewById(R.id.alpha_text_view);
//                            alphaTextView.setText(String.format("Alpha: %.2f", alpha));
//                        });
//
//                        // Alternatively, you could save the alpha data to a file
//                        // ...
//                    }
                // Find the EEG stream provided by the headband
                //   LSL.StreamInfo[] results = LSL.resolve_stream("bluetooth_device_address", museDevice.getAddress(), 1, LSL.FOREVER);
                // LSL.StreamInfo info = results[0];
                //  System.out.println(results);
//                    System.out.println(info);
//                    // Open the EEG stream
//                    try {
//                        LSL.StreamInlet inlet = new LSL.StreamInlet(info);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    // Create a buffer to hold the sample data
//                //    double[] sample = new double[inlet.pull_sample(2,100)];
//
//                    // Start a thread to receive data from the Muse 2 headband
//                    new Thread(() -> {
//                        while (true) {
//                            // Pull a sample from the stream
//                            double timestamp = 0;
//                            try {
//                             //   timestamp = inlet.pull_sample(sample);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                            // Calculate the alpha activity from the AF7 and AF8 channels
//                          //  double alpha = (sample[1] + sample[2]) / 2;
//
//                            // Do something with the alpha data
//                        //    processAlpha(timestamp, alpha);
//                        }
//                    }).start();
            } else {
                // Muse 2 headband not found
                //  Toast.makeText(this, "Muse 2 headband not found", Toast.LENGTH_SHORT).show();
            }


        };
    };
}


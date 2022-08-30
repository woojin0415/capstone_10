package com.dinkar.blescanner;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.widget.Button;

public class Bluetooth {

    private Button bt;
    private BluetoothLeScanner scanner;
    private BluetoothAdapter adapter;
    private int[][] rssi_value;
    private String mac1;
    private String mac2;
    private String mac3;
    private String mac4;
    private String mac5;
    private int count;
    private int[] test_data;

    Bluetooth(BluetoothAdapter adapter, int[] test_data, Button bt){
        this.adapter = adapter;
        scanner = adapter.getBluetoothLeScanner();
        this.test_data = test_data;
        this.bt = bt;
    }

    void start(){
        bt.setEnabled(false);
        bt.setText("데이터 수집중");
        count = 0;
        scan();
    }
    void stop(){
        stopscan();
        bt.setEnabled(true);
        bt.setText("수령 시작");
    }

    @SuppressLint("MissingPermission")
    private void scan(){
        adapter.startLeScan(scancallback);
    }


    @SuppressLint("MissingPermission")
    private void stopscan(){
        adapter.stopLeScan(scancallback);
    }

    private BluetoothAdapter.LeScanCallback scancallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] bytes) {
            String macAdd = device.getAddress();
            int rssivalue = rssi;
            System.out.println(rssi);
            if(count < 25) {
                test_data[count] = rssi;
                count++;
            }
            else if(count == 25)
                stop();

        }
    };
}

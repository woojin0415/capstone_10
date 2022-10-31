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
    private String mac1 = "AC:23:3F:E8:04:3A";
    private String mac2 = "AC:23:3F:E8:04:41";
    private String mac3 = "AC:23:3F:E8:04:33";
    private String mac4 = "AC:23:3F:E8:04:3D";
    private int n1, n2, n3, n4;
    private boolean b1, b2, b3, b4;

    Bluetooth(BluetoothAdapter adapter, int[][] test_data, Button bt){
        this.adapter = adapter;
        scanner = adapter.getBluetoothLeScanner();
        this.rssi_value = test_data;
        this.bt = bt;
    }

    void start(){
        n1 = 0;n2= 0;n3= 0;n4 = 0;
        b1 = false; b2 = false; b3 = false; b4 = false;
        bt.setEnabled(false);
        bt.setText("데이터 수집중");
        scan();
    }
    void stop(){
        stopscan();
        bt.setEnabled(true);
        bt.setText("수령 시작");
        b1 = false; b2 = false; b3 = false; b4 = false;
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

            if(b1 && b2 && b3 & b4){
                stop();
            }


            if(macAdd.equals(mac1) && (!b1)){
                if(n1 < 10)
                    rssi_value[0][n1] = -rssi;
                    System.out.println(rssi);
                if(n1 == 9)
                    b1 = true;

                n1++;
            }
            if(macAdd.equals(mac2) && (!b2)){
                if(n2 < 10)
                    rssi_value[1][n2] = -rssi;
                    System.out.println(rssi);
                if(n2 == 9)
                    b2 = true;
                n2++;
            }
            if(macAdd.equals(mac3) && (!b3)){
                if(n3 < 10)
                    rssi_value[2][n3] = -rssi;
                    System.out.println(rssi);
                if(n3 == 9)
                    b3 = true;
                n3++;
            }
            if(macAdd.equals(mac4) && (!b4)){
                if(n4 < 10)
                    rssi_value[3][n4] = -rssi;
                    System.out.println(rssi);
                if(n4 == 9)
                    b4 = true;
                n4++;
            }
        }
    };
}

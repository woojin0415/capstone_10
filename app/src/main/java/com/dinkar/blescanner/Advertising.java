package com.dinkar.blescanner;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class Advertising {
    private static final String LOG_TAG = "BLEApp";
    private BluetoothAdapter mBluetoothAdapter;
    private AdvertisingSetParameters parameters;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private Context context;
    private BluetoothLeAdvertiser advertiser;
    private AdvertiseData advertiseData;



    public Advertising(Context context) {
        String tdata = "012345678901234567890123456";
        byte[] tdata1 = tdata.getBytes();

        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
    }

    @SuppressLint("MissingPermission")
    public void startADV(String sector, boolean lock) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            parameters = (new AdvertisingSetParameters.Builder())
                    .setLegacyMode(false) // True by default, but set here as a reminder.
                    .setConnectable(true)
                    .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
                    .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_HIGH)
                    .build();

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(LOG_TAG, "startAdver");

            // Advertising 설정
            AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
            settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
            settingsBuilder.setConnectable(true);
            settingsBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
            settingsBuilder.setTimeout(0);
            AdvertiseSettings advertiseSettings = settingsBuilder.build();

            String tdata;
            // Advertising 데이터
            if(lock == false) {
                tdata = "1locksectionis" + sector;
            }
            else{
                tdata = "2locksectionis"+sector;
            }
            byte[] tdata1 = tdata.getBytes();

            AdvertiseData advertiseData = (new AdvertiseData.Builder())
                    .addManufacturerData(0x43FD, tdata1)
                    .setIncludeDeviceName(false)
                    .build();


            mBluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
        }
    }

    @SuppressLint("MissingPermission")
    public void stopADV() {
        mBluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
    }

    public AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.i(LOG_TAG, "onStartSuccess()");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            //Toast myToast = Toast.makeText(context,"", Toast.LENGTH_SHORT);

            switch (errorCode) {
                case AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE:
                    Log.i(LOG_TAG, "onStartFailure(): ADVERTISE_FAILED_DATA_TOO_LARGE");
                    //Toast.makeText(this.getApplicationContext(),"onStartFailure(): ADVERTISE_FAILED_DATA_TOO_LARGE", Toast.LENGTH_LONG);
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    Log.i(LOG_TAG, "onStartFailure(): ADVERTISE_FAILED_TOO_MANY_ADVERTISERS");
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED:
                    Log.i(LOG_TAG, "onStartFailure(): ADVERTISE_FAILED_ALREADY_STARTED");
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR:
                    Log.i(LOG_TAG, "onStartFailure(): ADVERTISE_FAILED_INTERNAL_ERROR");
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    Log.i(LOG_TAG, "onStartFailure(): ADVERTISE_FAILED_FEATURE_UNSUPPORTED");
                    break;
                default:
                    Log.i(LOG_TAG, "onStartFailure()");
            }
        }
    };

}

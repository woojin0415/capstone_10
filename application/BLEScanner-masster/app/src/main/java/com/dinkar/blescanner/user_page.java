package com.dinkar.blescanner;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dinkar.blescanner.login.test_rssi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class user_page extends AppCompatActivity {
    private Retrofit retrofit;
    int[] test_data;
    boolean type_bt;
    boolean type_bt_adv;
    TextView tv;
    String sector;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page_view);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        Intent intent = getIntent();

        String id =intent.getStringExtra("id");

        test_data = new int[25];
        Button bt_receive = findViewById(R.id.bt_receive);
        tv = findViewById(R.id.tv_user);

        type_bt = false;
        type_bt_adv = false;

        setRetrofitInit();

        Bluetooth ble = new Bluetooth(adapter, test_data, bt_receive);

        Advertising advertiser = new Advertising(this);


        bt_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type_bt == false && type_bt_adv == false) {
                    ble.start();
                    type_bt = true;
                }
                else if (type_bt == true && type_bt_adv == false){
                    for(int i =0; i<5; i++) {
                        System.out.println(id);
                        String r1 = Integer.toString(test_data[0]);
                        send(r1, r1, r1, r1, r1, id);
                        type_bt = false;
                        type_bt_adv = true;
                        bt_receive.setText("수령하기");
                    }
                }
                else if (type_bt == false && type_bt_adv == true){
                    type_bt_adv = false;
                    advertiser.startADV(sector, false); // lock: false - 잠금 해제
                }
            }
        });

    }


    private void setRetrofitInit () {
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://13.125.248.219:8080/locker/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    private void send (String r1, String r2, String r3, String r4, String r5, String user_id) {

        test_rssi service = retrofit.create(test_rssi.class);
        Call<String> call = service.getMember(r1, r2, r3, r4, r5, user_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("TAG", "onResponse11: " + response.body());
                String locker_number = response.body();
                sector = locker_number;
                tv.setText(locker_number);
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "rssi 값 전송 실패", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "onFailure: " + t);
            }
        });

    }
}

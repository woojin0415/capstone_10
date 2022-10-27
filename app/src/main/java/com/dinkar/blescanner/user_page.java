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

import com.dinkar.blescanner.login.user_rssi;
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
    int[][] test_data;
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

        test_data = new int[4][10];
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

                    System.out.println(id);
                    String r1 = intarray_to_string(test_data[0]);
                    String r2 = intarray_to_string(test_data[1]);
                    String r3 = intarray_to_string(test_data[2]);
                    String r4 = intarray_to_string(test_data[3]);

                    send(r1,
                            r2,
                            r3,
                            r4, r1, id);

                    type_bt_adv = true;
                    bt_receive.setText("수령하기");

                }
                else if (type_bt == true && type_bt_adv == true){
                    type_bt = false;
                    advertiser.startADV(sector, false); // lock: false - 잠금 해제
                    bt_receive.setText("수령 완료");
                }
                else if (type_bt == false && type_bt_adv == true){
                    type_bt_adv = false;
                    advertiser.stopADV(); // lock: true - 잠금
                    bt_receive.setText("시작하기");
                }
            }
        });

    }


    private void setRetrofitInit () {
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://203.255.81.72:10021/locker/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    private void send (String r1, String r2, String r3, String r4, String r5, String user_id) {

        user_rssi service = retrofit.create(user_rssi.class);
        Call<String> call = service.getMember(r1, r2, r3, r4, r5, user_id);
        System.out.println(r1 +  r2 + r3 + r4 +  r5);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("TAG", "onResponse11: " + response.body());
                String locker_number = response.body();
                sector = locker_number;
                tv.setText("User" + locker_number);
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "rssi 값 전송 실패", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "onFailure: " + t);
            }
        });

    }
    private String intarray_to_string(int arr[]){
        String result ="";

        for (int i =0; i<10; i++){
            result+= (char)arr[i];
        }
        System.out.println(result);
        return result;
    }
}

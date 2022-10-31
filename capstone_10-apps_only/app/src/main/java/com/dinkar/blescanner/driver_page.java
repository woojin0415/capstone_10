package com.dinkar.blescanner;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dinkar.blescanner.login.driver_rssi;
import com.dinkar.blescanner.login.user_rssi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class driver_page extends AppCompatActivity {
    private Retrofit retrofit;
    int[][] test_data;
    boolean type_bt;
    boolean type_bt_adv;
    TextView tv;
    EditText ed;
    String sector;
    Button bt_receive;
    ImageView imageView1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_page_view);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        Intent intent = getIntent();

        final String[] id = {intent.getStringExtra("id")};

        test_data = new int[4][10];
        bt_receive = findViewById(R.id.bt_receive_dv);
        tv = findViewById(R.id.tv_driver);
        ed = findViewById(R.id.et_dv);

        imageView1 = findViewById(R.id.imageView1);

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
                    id[0] = ed.getText().toString();

                    String r1 = intarray_to_string(test_data[0]);
                    String r2 = intarray_to_string(test_data[1]);
                    String r3 = intarray_to_string(test_data[2]);
                    String r4 = intarray_to_string(test_data[3]);

                    System.out.println(id[0]);
                    send(r1,
                            r2,
                            r3,
                            r4, r1, id[0]);

                    bt_receive.setEnabled(false);
                    type_bt_adv = true;
                    bt_receive.setText("맡기기");

                }
                else if (type_bt == true && type_bt_adv == true){
                    type_bt = false;
                    advertiser.startADV(sector, true); // lock: true - 잠금
                    bt_receive.setText("수취 완료");

                }
                else if (type_bt == false && type_bt_adv == true){
                    type_bt_adv = false;
                    advertiser.stopADV(); // lock: true - 잠금
                    bt_receive.setText("시작하기");
                    tv.setText(" ");

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

        driver_rssi service = retrofit.create(driver_rssi.class);
        Call<String> call = service.getMember(r1, r2, r3, r4, r5, user_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("TAG", "onResponse11: " + response.body());
                String locker_number = response.body();
                sector = locker_number;

                if (locker_number.equals("1")){
                    imageView1.setImageResource(R.drawable.locker1);
                    tv.setText(locker_number);
                }
                else if (locker_number.equals("2")){
                    imageView1.setImageResource(R.drawable.locker2);
                    tv.setText(locker_number);
                }
                else if (locker_number.equals("3")){
                    imageView1.setImageResource(R.drawable.locker3);
                    tv.setText(locker_number);
                }
                else if (locker_number.equals("4")){
                    imageView1.setImageResource(R.drawable.locker4);
                    tv.setText(locker_number);
                }
                else{
                    imageView1.setImageResource(R.drawable.lockerf);
                    tv.setText("이미 사용 중인 락커");
                    type_bt = false;
                    type_bt_adv = false;
                    bt_receive.setText("시작하기");
                }
                bt_receive.setEnabled(true);
                }



            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "rssi 값 전송 실패", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "onFailure: " + t);

                type_bt = false;
                type_bt_adv = false;
                bt_receive.setText("시작하기");
                bt_receive.setEnabled(true);
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

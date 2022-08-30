package com.dinkar.blescanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dinkar.blescanner.login.TestLogin1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Log_in_page extends AppCompatActivity {
    private EditText logID1,LogPs1;    // 아이디, 비밀번호 입력창
    private Button Logins1,logput1,button2;                    // 로그인 버튼
    private Retrofit retrofit;                    // 웹서버와 통신할 Retrofit
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_view);
        setTitle("Retrofit Login");
        setRetrofitInit();  // Retrofit 초기화
        logID1 = findViewById(R.id.logID);
        LogPs1 = findViewById(R.id.LogPs);
        Logins1 = findViewById(R.id.Logins);
        logput1 = findViewById(R.id.logput);
        button2 = findViewById(R.id.button2);
        Logins1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login1();// 버튼 클릭시, 로그인을 진행합니다.
            }
        });
        logput1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Log_in_page.this, Sign_in_page.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Log_in_page.this, find_password_page.class);
                startActivity(intent);
            }
        });
    }
        // todo        http://13.125.248.219:8080/sign/signup/?UserID=1111&password=1111&type=1
        private void setRetrofitInit () {
            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://13.125.248.219:8080/sign/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        private void login1 () {
            String id1 = logID1.getText().toString();
            String pw1 = LogPs1.getText().toString();
//        ILgoinService service = retrofit.create(ILgoinService.class);
            TestLogin1 service = retrofit.create(TestLogin1.class);
            Call<String> call = service.getMember(id1, pw1);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("TAG", "onResponse11: " + response.body());
                    String Ty = response.body();
                    if (response.body().equals("1")){
                        Intent intent = new Intent(Log_in_page.this, user_page.class);
                        intent.putExtra("id", id1);
                        startActivity(intent);
                    }else if(response.body().equals("2")){
                        Intent intent = new Intent(Log_in_page.this, user_page.class);
                        startActivity(intent);
                        intent.putExtra("id", id1);
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다!", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "onFailure: " + t);
                }
            });

        }
    }
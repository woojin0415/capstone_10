package com.dinkar.blescanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dinkar.blescanner.login.pscoll;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class find_password_page extends AppCompatActivity {
    private EditText logID1;    // 아이디, 비밀번호 입력창
    private Button btnLogin1;
    private TextView passcoll1;
    private Retrofit retrofit;
    @Override public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(find_password_page.this, Log_in_page.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_password_view);
        setTitle("Retrofit Login");

        setRetrofitInit();  // Retrofit 초기화

        logID1 = findViewById(R.id.pons1);
        btnLogin1 = findViewById(R.id.button12);
        passcoll1 = findViewById(R.id.passcoll);


        btnLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login2();// 버튼 클릭시, 로그인을 진행합니다.

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
    private void login2 () {
        String id2 = logID1.getText().toString();

//        ILgoinService service = retrofit.create(ILgoinService.class);
        pscoll service = retrofit.create(pscoll.class);

        Call<String> call = service.getMember(id2);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("TAG", "onResponse11: " + response.body());
                String Ty = response.body();
                passcoll1.setText(Ty);
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다!", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "onFailure: " + t);
            }
        });
    }
}
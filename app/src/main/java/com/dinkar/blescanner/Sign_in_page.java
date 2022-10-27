package com.dinkar.blescanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dinkar.blescanner.login.TestLogin;
import com.dinkar.blescanner.login.logck;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Sign_in_page extends AppCompatActivity {

    private EditText editTextID ,editTextPW;    // 아이디, 비밀번호 입력창
    private Button btnLogin,btnLogin1;                    // 로그인 버튼
    private RadioButton user_bt, driver_bt;
    private Retrofit retrofit;
    private Retrofit retrofit1;   // 웹서버와 통신할 Retrofit

    private RadioGroup radio_group;
    String checked = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_in_view);
        setTitle("Retrofit Login");

        setRetrofitInit();  // Retrofit 초기화

        editTextID = findViewById(R.id.editText_id);
        editTextPW = findViewById(R.id.editText_pw);
        btnLogin = findViewById(R.id.btn_login);
        radio_group = findViewById(R.id.radio_group);
        btnLogin1 = findViewById(R.id.logck1);

        user_bt = findViewById(R.id.radioButton1);
        driver_bt = findViewById(R.id.radioButton2);

        user_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(driver_bt.isChecked()){
                    driver_bt.setChecked(false);
                }
                checked = "1";
            }
        });

        driver_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_bt.isChecked()){
                    user_bt.setChecked(false);
                }
                checked = "2";
            }
        });

        btnLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginCk();

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();    // 버튼 클릭시, 로그인을 진행합니다.
            }
        });
    }


    // todo        http://13.125.248.219:8080/sign/login/

    private void setRetrofitInit(){
        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://203.255.81.72:10021/sign/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private void login(){
        String id = editTextID.getText().toString();
        String pw = editTextPW.getText().toString();
        String type = checked;

//        ILgoinService service = retrofit.create(ILgoinService.class);
        TestLogin service = retrofit.create(TestLogin.class);

        Call<String> call = service.getMember(id, pw, type);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("TAG", "onResponse: "+response.body().toString());
                Toast.makeText(getApplicationContext(), "회원가입에 성공하셨습니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Sign_in_page.this, Log_in_page.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다!", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "onFailure: " + t);
            }
        });
    }

    private void loginCk(){
        String id2 = editTextID.getText().toString();

//        ILgoinService service = retrofit.create(ILgoinService.class);
        logck service = retrofit.create(logck.class);

        Call<String> call = service.getMember(id2);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response1) {
                Log.e("TAG", "onResponse: "+response1.body().toString());
                String Ty1 = response1.body();
                if (response1.body().equals("T")){
                    Toast.makeText(getApplicationContext(), "중복된 회원입니다 아이디를 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                }else if(response1.body().equals("F")){
                    Toast.makeText(getApplicationContext(), "아이디 생성이 가능합니다", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다!", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "onFailure: " + t);
            }
        });
    }

}

        // JSon 형태로 받을때 ====================================================================

//        Call<MemberVO> call = service.getMember(id, pw, type);

//        call.enqueue(new Callback<MemberVO>() {
//            @Override
//            public void onResponse(Call<MemberVO> call, Response<MemberVO> response) {
//
//                MemberVO memberVO = response.body();    // 웹서버로부터 응답받은 데이터가 들어있다.
//
//                Log.e("TAG", "onResponse: " +response.toString());
//
//                if(memberVO != null){       // 회원입니다.
//                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
//                    intent.putExtra("memberVO", memberVO);
//                    startActivity(intent);
//                }else{                      // 회원이 아닙니다.
//                    Toast.makeText(getApplicationContext(), "회원이 아닙니다!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MemberVO> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다!", Toast.LENGTH_SHORT).show();
//                Log.e("TAG", "onFailure: " + t);
//            }
//        });

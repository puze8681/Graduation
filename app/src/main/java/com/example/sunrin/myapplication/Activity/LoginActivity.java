package com.example.sunrin.myapplication.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sunrin.myapplication.Data.UserModel;
import com.example.sunrin.myapplication.R;
import com.example.sunrin.myapplication.Server.RetrofitItner;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private final int LOGIN = 100;
    private final int REGISTER = 200;
    private final int ADD_PHOTO = 300;
    private final int ADD_ALBUM = 400;
    private final int PHOTO_LIST = 500;
    private final int ADD_ALBUM_PHOTO = 600;
    private final int ALBUM_LIST = 700;

    Button btn_login;
    Button btn_register;
    EditText edit_school;
    EditText edit_identity;
    EditText edit_birth;
    EditText edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = (Button)findViewById(R.id.btn_login);
        btn_register = (Button)findViewById(R.id.btn_register);
        edit_school = (EditText) findViewById(R.id.edit_school);
        edit_identity = (EditText)findViewById(R.id.edit_identity);
        edit_birth = (EditText)findViewById(R.id.edit_birth);
        edit_password = (EditText)findViewById(R.id.edit_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String school = edit_school.getText().toString();
                final String identity = edit_identity.getText().toString();
                String birth = edit_birth.getText().toString();
                String password = edit_password.getText().toString();

                if(school.equals("") || identity.equals("") || (birth.length() != 8) || ((password.length() < 8) || (password.length() > 16))){
                    Toast.makeText(LoginActivity.this, "로그인 양식을 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                }else{
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(RetrofitItner.BaseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitItner apiRequest = retrofit.create(RetrofitItner.class);

                    apiRequest.login(school, identity, birth, password).enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            if(response.code() == 200){
                                Intent photoListActivity = new Intent(LoginActivity.this, PhotoListActivity.class);
                                photoListActivity.putExtra("userToken", response.body().getUsertoken());
                                startActivityForResult(photoListActivity, LOGIN);
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "코드 not 200", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "서버에러", Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        }
                    });
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }


}

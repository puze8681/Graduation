package com.example.sunrin.myapplication.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sunrin.myapplication.Data.SuccessModel;
import com.example.sunrin.myapplication.R;
import com.example.sunrin.myapplication.Server.RetrofitItner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private final int LOGIN = 100;
    private final int REGISTER = 200;
    private final int ADD_PHOTO = 300;
    private final int ADD_ALBUM = 400;
    private final int PHOTO_LIST = 500;
    private final int ADD_ALBUM_PHOTO = 600;
    private final int ALBUM_LIST = 700;

    Button btn_check;
    EditText edit_name;
    EditText edit_school;
    EditText edit_identity;
    EditText edit_birth;
    EditText edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_check = (Button)findViewById(R.id.btn_check);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_school = (EditText) findViewById(R.id.edit_school);
        edit_identity = (EditText)findViewById(R.id.edit_identity);
        edit_birth = (EditText)findViewById(R.id.edit_birth);
        edit_password = (EditText)findViewById(R.id.edit_password);

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edit_name.getText().toString();
                String school = edit_school.getText().toString();
                String identity = edit_identity.getText().toString();
                String birth = edit_birth.getText().toString();
                String password = edit_password.getText().toString();

                if(name.equals("") || school.equals("") || identity.equals("") || birth.length() != 8 || ((password.length() < 8) || (password.length() > 16))) {
                    Toast.makeText(RegisterActivity.this, "계성 생성 양식을 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                }else{
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(RetrofitItner.BaseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitItner apiRequest = retrofit.create(RetrofitItner.class);

                    apiRequest.register(name, school, identity, birth, password).enqueue(new Callback<SuccessModel>() {
                        @Override
                        public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                            if(response.code() == 200){
                                Toast.makeText(RegisterActivity.this, "회원가입 성공. 로그인을 해주세요.", Toast.LENGTH_LONG).show();
                                Intent photoListActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(photoListActivity);
                                finish();
                            }else{
                                Toast.makeText(RegisterActivity.this, "코드 not 200", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SuccessModel> call, Throwable t) {
                            Toast.makeText(RegisterActivity.this, "서버에러", Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        }
                    });
                }
            }
        });
    }
}

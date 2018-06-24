package com.example.sunrin.myapplication.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunrin.myapplication.Data.SuccessModel;
import com.example.sunrin.myapplication.R;
import com.example.sunrin.myapplication.Server.RetrofitItner;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddPhotoAlbumActivity extends AppCompatActivity {

    private final int LOGIN = 100;
    private final int REGISTER = 200;
    private final int ADD_PHOTO = 300;
    private final int ADD_ALBUM = 400;
    private final int PHOTO_LIST = 500;
    private final int ADD_ALBUM_PHOTO = 600;
    private final int ALBUM_LIST = 700;

    private ImageView image_back;
    private TextView txt_create;
    private EditText edit_name;
    private EditText edit_info;
    private TextView txt_info;

    String usertoken;
    String name;
    String info;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_album);

        image_back = findViewById(R.id.img_back);
        txt_create = findViewById(R.id.txt_create);
        edit_name = findViewById(R.id.edit_name);
        edit_info = findViewById(R.id.edit_info);
        txt_info = findViewById(R.id.txt_info);
        txt_info.setVisibility(View.INVISIBLE);

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        intent = getIntent();
        usertoken = getIntent().getStringExtra("userToken");
        Log.d("UserToken", usertoken);

        //TODO 만들기 버튼
        txt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("UserToken", usertoken);
                name = edit_name.getText().toString();
                info = edit_info.getText().toString();
                Intent intent = new Intent(AddPhotoAlbumActivity.this, PhotoListActivity.class);

                if(name.equals("")|| info.equals("")){
                    Toast.makeText(AddPhotoAlbumActivity.this, "정보를 제대로 입력해주세요.", Toast.LENGTH_LONG).show();
                }else{
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(RetrofitItner.BaseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitItner apiRequest = retrofit.create(RetrofitItner.class);
                    apiRequest.makePhotobook(name, info, usertoken).enqueue(new Callback<SuccessModel>() {
                        @Override
                        public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                            if(response.code() == 200){
                                Toast.makeText(AddPhotoAlbumActivity.this, "사진첩 추가 성공", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddPhotoAlbumActivity.this, "code is " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<SuccessModel> call, Throwable t) {
                            Toast.makeText(AddPhotoAlbumActivity.this, "서버에러", Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        }
                    });
                    //date, photoAlbum, image, info
                    intent.putExtra("userToken", usertoken);
                    startActivityForResult(intent, ADD_ALBUM);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case ALBUM_LIST:
                    usertoken = data.getStringExtra("userToken");
                    break;
            }

        }
    }
}

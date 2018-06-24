package com.example.sunrin.myapplication.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunrin.myapplication.Data.PhotoModel;
import com.example.sunrin.myapplication.R;
import com.example.sunrin.myapplication.Server.RetrofitItner;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoInfoActivity extends AppCompatActivity {

    private final int LOGIN = 100;
    private final int REGISTER = 200;
    private final int ADD_PHOTO = 300;
    private final int ADD_ALBUM = 400;
    private final int PHOTO_LIST = 500;
    private final int ADD_ALBUM_PHOTO = 600;
    private final int ALBUM_LIST = 700;

    TextView txt_info;
    TextView txt_create;
    TextView summary;
    ImageView img;

    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_info);

        img = findViewById(R.id.img_image);
        summary = findViewById(R.id.txt_summary);
        txt_info = findViewById(R.id.txt_info);
        txt_create = findViewById(R.id.txt_create);
        txt_create.setVisibility(View.INVISIBLE);
        txt_info.setText("사진 상세 보기");
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String booktoken = intent.getStringExtra("bookToken");
        Log.d("URL", url);
        Log.d("BOOKTOKEN", booktoken);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitItner.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitItner apiRequest = retrofit.create(RetrofitItner.class);

        apiRequest.getPhotoData(url).enqueue(new Callback<PhotoModel>() {
            @Override
            public void onResponse(Call<PhotoModel> call, Response<PhotoModel> response) {
                if(response.code() == 200){
                    Picasso.with(PhotoInfoActivity.this).load(response.body().getPhoto()).into(img);
                    summary.setText(response.body().getSummary());
                }else{
                    Toast.makeText(PhotoInfoActivity.this, "code is "+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PhotoModel> call, Throwable t) {
                Toast.makeText(PhotoInfoActivity.this, "서버에러", Toast.LENGTH_SHORT).show();
                Log.e("Error", t.getMessage());
            }
        });
    }
}

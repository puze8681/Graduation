package com.example.sunrin.myapplication.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunrin.myapplication.Adapter.AlbumListAdapter;
import com.example.sunrin.myapplication.Data.PhotobookModel;
import com.example.sunrin.myapplication.R;
import com.example.sunrin.myapplication.Server.RetrofitItner;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoAlbumListActivity extends AppCompatActivity {

    private final int LOGIN = 100;
    private final int REGISTER = 200;
    private final int ADD_PHOTO = 300;
    private final int ADD_ALBUM = 400;
    private final int PHOTO_LIST = 500;
    private final int ADD_ALBUM_PHOTO = 600;
    private final int ALBUM_LIST = 700;
    private final int ALBUM_SELECT = 800;

    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();

    GridView gridView;
    TextView edit_search;
    ImageView img_add;
    ImageView img_menu;

    ArrayList<PhotobookModel> photobooks = new ArrayList<>();
    AlbumListAdapter adapter;

    String usertoken;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album_list);

        gridView = findViewById(R.id.grid_view_photo_album_list);
        edit_search = findViewById(R.id.edit_search);
        img_add = findViewById(R.id.btn_add);
        img_menu = findViewById(R.id.btn_option);
        edit_search.setText("사진첩 이름을 입력해주세요.");

        intent = getIntent();
        usertoken = getIntent().getStringExtra("userToken");
        Log.d("UserToken", usertoken);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitItner.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitItner apiRequest = retrofit.create(RetrofitItner.class);
        Log.d("UserToken", usertoken);
        apiRequest.getmyphotobook(usertoken).enqueue(new Callback<ArrayList<PhotobookModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PhotobookModel>> call, final Response<ArrayList<PhotobookModel>> response) {
                if(response.code() == 200){
                    Log.e("count", String.valueOf(response.body().size()));
                    photobooks = response.body();
                    urls = new ArrayList<>();
                    names = new ArrayList<>();
                    for(int i = 0; i < photobooks.size(); i++){
                        urls.add(photobooks.get(i).getPhoto());
                        names.add(photobooks.get(i).getName());

                    }

                    //TODO URL to Bitmap 전환이 제대로 안됨 (돌려도 Null로 들어감)
                    Log.e("DATATA", urls.toString());

                    adapter = new AlbumListAdapter(urls, names);
                    gridView.setAdapter(adapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(PhotoAlbumListActivity.this, PhotoListActivity.class);
                            intent.putExtra("url", urls.get(position));
                            intent.putExtra("userToken", usertoken);
                            intent.putExtra("bookToken", photobooks.get(position).getBooktoken());
                            Log.d("URL", urls.get(position));
                            Log.d("BOOKTOKEN", photobooks.get(position).getBooktoken());
                            startActivityForResult(intent, ALBUM_SELECT);
                        }
                    });
                }else{
                    Toast.makeText(PhotoAlbumListActivity.this, "code is " + response.code(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<PhotobookModel>> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
//        //TODO 검색기능 구현해야함
//        img_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //getSearchPhotobook
//                if(edit_search.getText().toString().equals("")){
//                    Toast.makeText(PhotoAlbumListActivity.this, "제대로 입력 해주세요.", Toast.LENGTH_LONG).show();
//                }else {
//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl(RetrofitItner.BaseUrl)
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .build();
//                    RetrofitItner apiRequest = retrofit.create(RetrofitItner.class);
//                    apiRequest.getSearchPhotobook(edit_search.getText().toString()).enqueue(new Callback<ArrayList<PhotobookModel>>() {
//                        @Override
//                        public void onResponse(Call<ArrayList<PhotobookModel>> call, final Response<ArrayList<PhotobookModel>> response) {
//                            if(response.code() == 200){
//                                Log.e("count", String.valueOf(response.body().size()));
//                                photobooks = response.body();
//
//                                //TODO URL to Bitmap 전환이 제대로 안됨 (돌려도 Null로 들어감)
//                                Log.e("DATATA", urls.toString());
//
//                                adapter = new AlbumListAdapter(urls, names);
//                                gridView.setAdapter(adapter);
//                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        Intent intent = new Intent(PhotoAlbumListActivity.this, PhotoListActivity.class);
//                                        intent.putExtra("url", urls.get(position));
//                                        intent.putExtra("userToken", usertoken);
//                                        intent.putExtra("bookToken", photobooks.get(position).getBooktoken());
//                                        startActivityForResult(intent, ALBUM_SELECT);
//                                    }
//                                });
//                            }{
//                                Toast.makeText(PhotoAlbumListActivity.this, "code is " + response.code(), Toast.LENGTH_LONG).show();
//                            }
//                        }
//                        @Override
//                        public void onFailure(Call<ArrayList<PhotobookModel>> call, Throwable t) {
//                            Log.e("Error", t.getMessage());
//                        }
//                    });
//                }
//            }
//        });

        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoAlbumListActivity.this, AddPhotoAlbumActivity.class);
                intent.putExtra("userToken", usertoken);
                startActivityForResult(intent, ALBUM_LIST);
            }
        });

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoAlbumListActivity.this, PhotoListActivity.class);
                intent.putExtra("userToken", usertoken);
                Log.d("UserToken", usertoken);
                startActivityForResult(intent, ALBUM_LIST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RetrofitItner.BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitItner apiRequest = retrofit.create(RetrofitItner.class);

            switch (requestCode) {
                case ADD_ALBUM:
                    apiRequest.getmyphotobook(usertoken).enqueue(new Callback<ArrayList<PhotobookModel>>() {
                        @Override
                        public void onResponse(Call<ArrayList<PhotobookModel>> call, final Response<ArrayList<PhotobookModel>> response) {
                            if(response.code() == 200){
                                Log.e("count", String.valueOf(response.body().size()));
                                photobooks = response.body();
                                urls = new ArrayList<>();
                                names = new ArrayList<>();
                                for(int i = 0; i < photobooks.size(); i++){
                                    urls.add(photobooks.get(i).getPhoto());
                                    names.add(photobooks.get(i).getName());

                                }

                                //TODO URL to Bitmap 전환이 제대로 안됨 (돌려도 Null로 들어감)
                                Log.e("DATATA", urls.toString());

                                adapter = new AlbumListAdapter(urls, names);
                                gridView.setAdapter(adapter);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(PhotoAlbumListActivity.this, PhotoListActivity.class);
                                        intent.putExtra("url", urls.get(position));
                                        intent.putExtra("userToken", usertoken);
                                        intent.putExtra("bookToken", photobooks.get(position).getBooktoken());
                                        Log.d("URL", urls.get(position));
                                        startActivityForResult(intent, ALBUM_SELECT);
                                    }
                                });
                            }else{
                                Toast.makeText(PhotoAlbumListActivity.this, "code is " + response.code(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<PhotobookModel>> call, Throwable t) {
                            Log.e("Error", t.getMessage());
                        }
                    });
                    break;
                case PHOTO_LIST:
                    usertoken = data.getStringExtra("userToken");
                    Log.d("UserToken", usertoken);
                    apiRequest.getmyphotobook(usertoken).enqueue(new Callback<ArrayList<PhotobookModel>>() {
                        @Override
                        public void onResponse(Call<ArrayList<PhotobookModel>> call, final Response<ArrayList<PhotobookModel>> response) {
                            if(response.code() == 200){
                                Log.e("count", String.valueOf(response.body().size()));
                                photobooks = response.body();
                                urls = new ArrayList<>();
                                names = new ArrayList<>();
                                for(int i = 0; i < photobooks.size(); i++){
                                    urls.add(photobooks.get(i).getPhoto());
                                    names.add(photobooks.get(i).getName());

                                }

                                //TODO URL to Bitmap 전환이 제대로 안됨 (돌려도 Null로 들어감)
                                Log.e("DATATA", urls.toString());

                                adapter = new AlbumListAdapter(urls, names);
                                gridView.setAdapter(adapter);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(PhotoAlbumListActivity.this, PhotoListActivity.class);
                                        intent.putExtra("url", urls.get(position));
                                        intent.putExtra("userToken", usertoken);
                                        intent.putExtra("bookToken", photobooks.get(position).getBooktoken());
                                        startActivityForResult(intent, ALBUM_SELECT);
                                    }
                                });
                            }else{
                                Toast.makeText(PhotoAlbumListActivity.this, "code is " + response.code(), Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<ArrayList<PhotobookModel>> call, Throwable t) {
                            Log.e("Error", t.getMessage());
                        }
                    });
                    break;
            }
        }
    }
}

package com.example.sunrin.myapplication.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunrin.myapplication.Adapter.PhotoListAdapter;
import com.example.sunrin.myapplication.R;
import com.example.sunrin.myapplication.Server.RetrofitItner;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoListActivity extends AppCompatActivity {

    private final int LOGIN = 100;
    private final int REGISTER = 200;
    private final int ADD_PHOTO = 300;
    private final int ADD_ALBUM = 400;
    private final int PHOTO_LIST = 500;
    private final int ADD_ALBUM_PHOTO = 600;
    private final int ALBUM_LIST = 700;
    private final int ALBUM_SELECT = 800;

    GridView gridView;
    ImageView img_add;
    ImageView img_menu;
    TextView txt_search;

    ArrayList<String> urls = new ArrayList<>();
    PhotoListAdapter adapter;

    String usertoken;
    String booktoken;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        gridView = findViewById(R.id.grid_view_photo_list);
        img_add = findViewById(R.id.btn_add);
        img_menu = findViewById(R.id.btn_option);

        txt_search = findViewById(R.id.edit_search);
        txt_search.setText("사진 이름을 입력하세요.");
        booktoken = "";

        intent = getIntent();
        usertoken = intent.getStringExtra("userToken");
        Log.d("UserToken", usertoken);
        //TODO 검색기능 구현해야함


        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoListActivity.this, AddPhotoActivity.class);
                intent.putExtra("userToken", usertoken);
                startActivityForResult(intent, PHOTO_LIST);
            }
        });

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoListActivity.this, PhotoAlbumListActivity.class);
                intent.putExtra("userToken", usertoken);
                startActivityForResult(intent, PHOTO_LIST);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitItner.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitItner apiRequest = retrofit.create(RetrofitItner.class);

        apiRequest.getAllPhotos(usertoken).enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, final Response<ArrayList<String>> response) {
                if (response.code() == 200) {
                    Log.e("count", String.valueOf(response.body().size()));
                    urls = response.body();

                    //TODO URL to Bitmap 전환이 제대로 안됨 (돌려도 Null로 들어감)
                    Log.e("DATATA", urls.toString());

                    adapter = new PhotoListAdapter(urls);
                    gridView.setAdapter(adapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(PhotoListActivity.this, PhotoInfoActivity.class);
                            intent.putExtra("url", urls.get(position));
                            intent.putExtra("userToken", usertoken);
                            intent.putExtra("bookToken", booktoken);
                            startActivityForResult(intent, PHOTO_LIST);
                        }
                    });
                } else {
                    Toast.makeText(PhotoListActivity.this, "code is " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Log.e("Error", t.getMessage());
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
                case LOGIN:
                    usertoken = data.getStringExtra("userToken");
                    break;
                case ADD_PHOTO:
                    usertoken = data.getStringExtra("userToken");
                    booktoken = data.getStringExtra("bookToken");
                    if (booktoken.equals("")) {
                        apiRequest.getAllPhotos(usertoken).enqueue(new Callback<ArrayList<String>>() {
                            @Override
                            public void onResponse(Call<ArrayList<String>> call, final Response<ArrayList<String>> response) {
                                if (response.code() == 200) {
                                    Log.e("count", String.valueOf(response.body().size()));
                                    urls = response.body();

                                    //TODO URL to Bitmap 전환이 제대로 안됨 (돌려도 Null로 들어감)
                                    Log.e("DATATA", urls.toString());

                                    adapter = new PhotoListAdapter(urls);
                                    gridView.setAdapter(adapter);
                                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(PhotoListActivity.this, PhotoInfoActivity.class);
                                            intent.putExtra("url", urls.get(position));
                                            intent.putExtra("userToken", usertoken);
                                            intent.putExtra("bookToken", "NULL");
                                            startActivityForResult(intent, PHOTO_LIST);
                                        }
                                    });
                                } else {
                                    Toast.makeText(PhotoListActivity.this, "code is " + response.code(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                                Log.e("Error", t.getMessage());
                            }
                        });
                    } else {
                        apiRequest.getMyPhotos(booktoken).enqueue(new Callback<ArrayList<String>>() {
                            @Override
                            public void onResponse(Call<ArrayList<String>> call, final Response<ArrayList<String>> response) {
                                if (response.code() == 200) {
                                    Log.e("count", String.valueOf(response.body().size()));
                                    urls = response.body();

                                    //TODO URL to Bitmap 전환이 제대로 안됨 (돌려도 Null로 들어감)
                                    Log.e("DATATA", urls.toString());

                                    adapter = new PhotoListAdapter(urls);
                                    gridView.setAdapter(adapter);
                                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(PhotoListActivity.this, PhotoInfoActivity.class);
                                            intent.putExtra("url", urls.get(position));
                                            intent.putExtra("userToken", usertoken);
                                            intent.putExtra("bookToken", booktoken);
                                            startActivityForResult(intent, PHOTO_LIST);
                                        }
                                    });
                                } else {
                                    Toast.makeText(PhotoListActivity.this, "code is " + response.code(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                                Log.e("Error", t.getMessage());
                            }
                        });
                    }
                    break;
                case ALBUM_SELECT:
                    usertoken = data.getStringExtra("userToken");
                    booktoken = data.getStringExtra("bookToken");
                    apiRequest.getMyPhotos(booktoken).enqueue(new Callback<ArrayList<String>>() {
                        @Override
                        public void onResponse(Call<ArrayList<String>> call, final Response<ArrayList<String>> response) {
                            if (response.code() == 200) {
                                Log.e("count", String.valueOf(response.body().size()));
                                urls = response.body();

                                //TODO URL to Bitmap 전환이 제대로 안됨 (돌려도 Null로 들어감)
                                Log.e("DATATA", urls.toString());

                                adapter = new PhotoListAdapter(urls);
                                gridView.setAdapter(adapter);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(PhotoListActivity.this, PhotoInfoActivity.class);
                                        intent.putExtra("url", urls.get(position));
                                        intent.putExtra("userToken", usertoken);
                                        intent.putExtra("bookToken", booktoken);
                                        startActivityForResult(intent, PHOTO_LIST);
                                    }
                                });
                            } else {
                                Toast.makeText(PhotoListActivity.this, "code is " + response.code(), Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                            Log.e("Error", t.getMessage());
                        }
                    });
                    break;
                case ALBUM_LIST:
                    usertoken = data.getStringExtra("userToken");
                    booktoken = data.getStringExtra("bookToken");
                    apiRequest.getMyPhotos(booktoken).enqueue(new Callback<ArrayList<String>>() {
                        @Override
                        public void onResponse(Call<ArrayList<String>> call, final Response<ArrayList<String>> response) {
                            if (response.code() == 200) {
                                Log.e("count", String.valueOf(response.body().size()));
                                urls = response.body();

                                //TODO URL to Bitmap 전환이 제대로 안됨 (돌려도 Null로 들어감)
                                Log.e("DATATA", urls.toString());

                                adapter = new PhotoListAdapter(urls);
                                gridView.setAdapter(adapter);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(PhotoListActivity.this, PhotoInfoActivity.class);
                                        intent.putExtra("url", urls.get(position));
                                        intent.putExtra("userToken", usertoken);
                                        intent.putExtra("bookToken", booktoken);
                                        startActivityForResult(intent, PHOTO_LIST);
                                    }
                                });
                            } else {
                                Toast.makeText(PhotoListActivity.this, "code is " + response.code(), Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                            Log.e("Error", t.getMessage());
                        }
                    });
                    break;
            }
        }
    }
}

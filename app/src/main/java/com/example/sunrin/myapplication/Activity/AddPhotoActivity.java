package com.example.sunrin.myapplication.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunrin.myapplication.Data.PhotobookModel;
import com.example.sunrin.myapplication.Data.SuccessModel;
import com.example.sunrin.myapplication.R;
import com.example.sunrin.myapplication.Server.RetrofitItner;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddPhotoActivity extends AppCompatActivity {

    private final int LOGIN = 100;
    private final int REGISTER = 200;
    private final int ADD_PHOTO = 300;
    private final int ADD_ALBUM = 400;
    private final int PHOTO_LIST = 500;
    private final int ADD_ALBUM_PHOTO = 600;
    private final int ALBUM_LIST = 700;

    private final int CAMERA_CODE = 111;
    private final int WRITE_CODE = 222;
    private final int READ_CODE = 333;
    private final int REQUEST_PERMISSION_CODE = 444;
    private final int GALLERY_CODE = 555;

    private String usertoken;
    private ArrayList<PhotobookModel> photobooks = new ArrayList<>();
    private ArrayList<String> photobooktokens = new ArrayList<>();

    private TextView txt_info;
    private ImageView img_back;
    private TextView txt_create;
    private TextView txt_date;
    private Spinner spinner_album;
    private ImageView img_image;
    private EditText edit_info;

    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<String> spinnerList;
    private String photoAlbum;
    private String booktoken;

    private AlertDialog.Builder builder;
    private int isImage;
    private String date;
    private String info;

    private File imgFile;
    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        img_back = (ImageView) findViewById(R.id.img_back);
        txt_create = (TextView) findViewById(R.id.txt_create);
        txt_date = (TextView) findViewById(R.id.txt_date);
        spinner_album = (Spinner) findViewById(R.id.spinner_album);
        img_image = (ImageView) findViewById(R.id.img_image);
        edit_info = (EditText) findViewById(R.id.edit_info);
        txt_info = (TextView) findViewById(R.id.txt_info);
//        txt_info.setVisibility(View.INVISIBLE);

        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        txt_date.setText(date);

        isImage = 0;
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        usertoken = intent.getStringExtra("userToken");

        //TODO 만들기 버튼
        txt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info = edit_info.getText().toString();
                Intent intent = new Intent(AddPhotoActivity.this, PhotoListActivity.class);

                if ((isImage == 0) || info.equals("")) {
                    Toast.makeText(AddPhotoActivity.this, "정보를 제대로 입력해주세요.", Toast.LENGTH_LONG).show();
                } else {

                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/png"), imgFile);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", imgFile.getName(), requestFile);

                    RequestBody netBooktoken = RequestBody.create(okhttp3.MultipartBody.FORM, booktoken);
                    RequestBody netInfo = RequestBody.create(okhttp3.MultipartBody.FORM, date+"\n\n"+info);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(RetrofitItner.BaseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitItner apiRequest = retrofit.create(RetrofitItner.class);

                    apiRequest.addPhoto(netBooktoken, netInfo, body).enqueue(new Callback<SuccessModel>() {
                        @Override
                        public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                            if(response.code() == 200){
                                Toast.makeText(AddPhotoActivity.this, "사진 추가 성공", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddPhotoActivity.this, "code is ", response.code()).show();
                                Log.d("CODE:", String.valueOf(response.code()));
                            }
                        }

                        @Override
                        public void onFailure(Call<SuccessModel> call, Throwable t) {
                            Toast.makeText(AddPhotoActivity.this, "서버에러", Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        }
                    });
                    //date, photoAlbum, image, info
                    intent.putExtra("userToken", usertoken);
                    intent.putExtra("bookToken", booktoken);
                    startActivityForResult(intent, ADD_PHOTO);
                    finish();
                }
            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitItner.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitItner apiRequest = retrofit.create(RetrofitItner.class);

        apiRequest.getmyphotobook(usertoken).enqueue(new Callback<ArrayList<PhotobookModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PhotobookModel>> call, Response<ArrayList<PhotobookModel>> response) {
                if(response.code() == 200){
                    photobooks = response.body();
                    if (photobooks.size() == 0) {
                        Toast.makeText(AddPhotoActivity.this, "사진첩이 없습니다. 사진첩을 추가해주세요.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddPhotoActivity.this, AddPhotoAlbumActivity.class);
                        intent.putExtra("userToken", usertoken);
                        startActivityForResult(intent, ADD_PHOTO);
                        finish();
                    }
                    for (int i = 0; i < photobooks.size(); i++) {
                        spinnerList.add(photobooks.get(i).getName());
                        photobooktokens.add(photobooks.get(i).getBooktoken());
                    }
                }else{
                    Toast.makeText(AddPhotoActivity.this, "코드 not 200", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PhotobookModel>> call, Throwable t) {
                Toast.makeText(AddPhotoActivity.this, "서버에러", Toast.LENGTH_SHORT).show();
                Log.e("Error", t.getMessage());
            }
        });

        spinnerList = new ArrayList<>();
        spinnerList.add("기본");

        spinnerAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerList);
        spinner_album.setAdapter(spinnerAdapter);

        spinner_album.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(AddPhotoActivity.this, "선택된 사진첩 : " + spinner_album.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                photoAlbum = spinner_album.getItemAtPosition(position).toString();
                if (photobooktokens.size() == 0) {
                    booktoken = "";
                } else {
                    booktoken = photobooktokens.get(position-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        img_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isImage == 0) {
                    checkPermission();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // 권한을 활성화 해주기 위한 설명
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "카메라로 찍어 업로드한 사진을 갤러리에 저장하기 위해 권한을 허용해주세요..", Toast.LENGTH_SHORT).show();
            }
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "업로드할 사진을 갤러리에서 불러오기 위해 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
            }
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "업로드할 사진을 카메라에서 촬영하기 위해 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, REQUEST_PERMISSION_CODE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            // 모든 권한 항상 허용
            selectUpload();
        }
    }

    //사진을 업로드할 방법 선택
    private void selectUpload() {
        builder = new AlertDialog.Builder(this);

        builder.setTitle("사진 업로드");
        builder.setMessage("사진을 업로드할 방법을 선택해주세요.");
        builder.setNeutralButton("갤러리", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectGallery();
            }
        });
        builder.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectCamera();
            }
        });
        builder.setNegativeButton("취소", null);
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    //업로드할 사진을 카메라로 촬영
    private void selectCamera() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, CAMERA_CODE);
                }
            }

        }
    }

    //파일 생성
    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/"

                + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();

        return storageDir;
    }

    //사진 갤러리에 저장
    private void savePhotoIntoGallery(String currentPhotoPath) {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        File f = new File(currentPhotoPath);

        Uri contentUri = Uri.fromFile(f);

        mediaScanIntent.setData(contentUri);

        this.sendBroadcast(mediaScanIntent);

    }

    //카메라로 찍은 사진 적용
    private void getPictureForPhoto() {
        imgFile = new File(currentPhotoPath);
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        savePhotoIntoGallery(currentPhotoPath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        img_image.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        isImage = 1;
    }

    //업로드할 사진을 갤러리에서 선택
    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    //선택한 사진에 대한 데이터 처리
    private void sendPicture(Uri imgUri) {
        String imagePath = getRealPathFromURI(imgUri); // path 경로
        imgFile = new File(imagePath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        img_image.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        isImage = 1;
    }

    //사진이 저장된 절대 경로 구하기
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    //사진의 회전값 구하기
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    //사진을 정방향대로 회전
    private Bitmap rotate(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                } else {
                    Toast toast = Toast.makeText(this,
                            "기능 사용을 위한 권한 동의가 필요합니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //TODO Spinner 에서 사진첩 고정시켜줘야함
                case PHOTO_LIST:
                    usertoken = data.getStringExtra("userToken");
                    booktoken = data.getStringExtra("booktoken");
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(RetrofitItner.BaseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitItner apiRequest = retrofit.create(RetrofitItner.class);

                    apiRequest.getmyphotobook(usertoken).enqueue(new Callback<ArrayList<PhotobookModel>>() {
                        @Override
                        public void onResponse(Call<ArrayList<PhotobookModel>> call, Response<ArrayList<PhotobookModel>> response) {
                            if(response.code() == 200){
                                photobooks = response.body();
                                if (photobooks.size() == 0) {
                                    Toast.makeText(AddPhotoActivity.this, "사진첩이 없습니다. 사진첩을 추가해주세요.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddPhotoActivity.this, AddPhotoAlbumActivity.class);
                                    intent.putExtra("userToken", usertoken);
                                    startActivityForResult(intent, ADD_PHOTO);
                                    finish();
                                }
                                for (int i = 0; i < photobooks.size(); i++) {
                                    spinnerList.add(photobooks.get(i).getName());
                                    photobooktokens.add(photobooks.get(i).getBooktoken());
                                }
                            }else{
                                Toast.makeText(AddPhotoActivity.this, "코드 not 200", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<PhotobookModel>> call, Throwable t) {
                            Toast.makeText(AddPhotoActivity.this, "서버에러", Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        }
                    });
                    break;
                case GALLERY_CODE:
                    sendPicture(data.getData()); //갤러리에서 가져오기
                    break;
                case CAMERA_CODE:
                    getPictureForPhoto(); //카메라에서 가져오기
                    break;
                default:
                    break;
            }

        }
    }
}
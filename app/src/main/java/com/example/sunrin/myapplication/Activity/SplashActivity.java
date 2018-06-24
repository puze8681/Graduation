package com.example.sunrin.myapplication.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.sunrin.myapplication.R;

public class SplashActivity extends AppCompatActivity {

    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        final Handler hd2 = new Handler();
        final Handler hd3 = new Handler();
        startAnimations();
        hd2.postDelayed(new Runnable() {
            @Override
            public void run() {
                endAnimation();
                hd3.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout animlayout = findViewById(R.id.layout_splash);
                        animlayout.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            }
        }, 2000);
    }

    private void startAnimations() {
        Log.d("Anim", "start");
        LinearLayout animlayout = findViewById(R.id.layout_splash);
        animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.alpha);
        animation.reset();
        animlayout.clearAnimation();
        animlayout.startAnimation(animation);
    }

    private void endAnimation() {
        Log.d("Anim", "end");
        LinearLayout animlayout = findViewById(R.id.layout_splash);
        animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.disalpha);
        animation.reset();
        animlayout.clearAnimation();
        animlayout.startAnimation(animation);
    }
}

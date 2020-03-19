package com.example.mvvmapplication.view;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mvvmapplication.R;

import static android.view.View.GONE;

public class LoginActivity extends AppCompatActivity {
    private ImageView imageViewLogo;
    private TextView textViewSocial;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootLayout,animationEndLayout;
    //todo: not working as expected.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initializeViews();
        new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewSocial.setVisibility(GONE);
                loadingProgressBar.setVisibility(GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    rootLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white_transparent_80));
                    imageViewLogo.setImageResource(R.drawable.temp_logo);
                }
                startAnimation();
            }
            @Override
            public void onFinish() {

            }
        }.start();
    }
    private void initializeViews(){
        imageViewLogo = findViewById(R.id.socialLogoImageView);
        textViewSocial = findViewById(R.id.txtSocializeLogin);
        loadingProgressBar = findViewById(R.id.afterAnimationProgress);
        rootLayout = findViewById(R.id.loginRootView);
        animationEndLayout = findViewById(R.id.animationEndView);
    }
    private void startAnimation(){
        ViewPropertyAnimator viewPropertyAnimator = imageViewLogo.animate();
        viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animationEndLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}

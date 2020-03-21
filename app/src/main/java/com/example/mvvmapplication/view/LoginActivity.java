package com.example.mvvmapplication.view;

import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mvvmapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import static android.view.View.GONE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView imageViewLogo;
    private TextView textViewLogin;
    private ProgressBar loadingProgressBar;
    private LinearLayout animationEndLayout;
    private RelativeLayout rootLayout;
    private TextInputEditText edtUsername,edtPassword;

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
                //empty method
            }
            @Override
            public void onFinish() {
                loadingProgressBar.setVisibility(GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    rootLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white_transparent_80));
                    imageViewLogo.setImageResource(R.drawable.application_logo_black);
                }
                startAnimation();
            }
        }.start();
    }
    private void initializeViews(){
        imageViewLogo = findViewById(R.id.socialLogoImageView);
        loadingProgressBar = findViewById(R.id.afterAnimationProgress);
        rootLayout = findViewById(R.id.loginRootView);
        animationEndLayout = findViewById(R.id.animationEndView);
        textViewLogin = findViewById(R.id.textViewSocialLogin);
        edtUsername = findViewById(R.id.usernameEditText);
        edtPassword = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView textSignUp = findViewById(R.id.txt_sign_up);
        textSignUp.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }
    private void startAnimation(){
        ViewPropertyAnimator viewPropertyAnimator = imageViewLogo.animate();
        viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                textViewLogin.setVisibility(GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationEndLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        //todo: login will be called from google firebase
        switch (v.getId()){
            case R.id.loginButton:
                if (Objects.requireNonNull(edtUsername.getText()).toString().equals("admin")&&
                    Objects.requireNonNull(edtPassword.getText()).toString().equals("pass")){
                    startActivity(new Intent(this,MainActivity.class));
                }
                else {
                    Toast.makeText(this, "Wrong credentials !", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.txt_sign_up:
                setContentView(R.layout.activity_signup);
                break;
        }
    }
}

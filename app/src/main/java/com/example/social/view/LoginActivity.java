package com.example.social.view;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.social.R;
import com.example.social.constants.Constants;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.view.View.GONE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,FirebaseAuth.AuthStateListener,Constants{
    private ImageView imageViewLogo;
    private TextView textViewLogin;
    private ProgressBar loadingProgressBar;
    private LinearLayout animationEndLayout;
    private RelativeLayout rootLayout;
    private TextInputEditText edtUsername,edtPassword;
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private CheckBox chkRememberMe;
    private boolean isVisible = false;
    private static final int REQUEST_CODE = 4978;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
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
        chkRememberMe = findViewById(R.id.chk_remember_me);
        Button loginButton = findViewById(R.id.loginButton);
        TextView textSignUp = findViewById(R.id.txt_sign_up);
        TextView textViewForgotPassword = findViewById(R.id.txt_forgot_pass);
        ImageView imageViewShowPass = findViewById(R.id.imgShowPassword);
        imageViewShowPass.setOnClickListener(this);
        textViewForgotPassword.setOnClickListener(this);
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
        switch (v.getId()){
            case R.id.loginButton:
                firebaseLogin();
                break;
            case R.id.txt_sign_up:
                providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());
                showSignInOptions();
                break;
            case R.id.txt_forgot_pass:
                startActivity(new Intent(getApplicationContext(),ForgotPasswordActivity.class));
                break;
            case R.id.imgShowPassword:
                if (!isVisible){
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isVisible = !isVisible;
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    System.out.println(user.getEmail() + " "+ user.getDisplayName());
                    startActivity(new Intent(this,MainActivity.class));
                }
            }else {
                if (response != null) {
                    System.out.println(response.getError() + " "+ requestCode);
                }
            }
        }
    }
    private void showSignInOptions(){
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.FirebaseAuthTheme)
                .build(),REQUEST_CODE);
    }
    private void firebaseLogin() {
        initializeSharedPreferencesForFirebaseAuthenticationFunctionality();
        String email = Objects.requireNonNull(edtUsername.getText()).toString();
        String password = Objects.requireNonNull(edtPassword.getText()).toString();
        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(this,MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong Credentials !", Toast.LENGTH_SHORT).show();
                }
            });
        } else Toast.makeText(this,"Empty value",Toast.LENGTH_LONG).show();
    }

    private void initializeSharedPreferencesForFirebaseAuthenticationFunctionality() {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(AUTH_PERSISTENCE, Context.MODE_PRIVATE)
                .edit();
        if (chkRememberMe.isChecked()) {
            editor.putBoolean(AUTO_SIGN, true);
        } else {
            editor.putBoolean(AUTO_SIGN,false);
        } editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.onAuthStateChanged(firebaseAuth);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (getApplicationContext().getSharedPreferences(AUTH_PERSISTENCE,Context.MODE_PRIVATE).getBoolean(AUTO_SIGN,false)){
            if (firebaseAuth.getCurrentUser() != null) {
                startActivity(new Intent(this, MainActivity.class));
            }
        } else {
            System.out.println(firebaseAuth.getCurrentUser());
        }
    }
}

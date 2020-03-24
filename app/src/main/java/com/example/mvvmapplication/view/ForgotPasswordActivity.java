package com.example.mvvmapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mvvmapplication.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private EditText edtForgotPasswordInput;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initializeView();
        initializeAuthenticator();
    }
    private void initializeView(){
        edtForgotPasswordInput = findViewById(R.id.edt_email_forgot_pass);
        Button btnForgotPassword = findViewById(R.id.btnResetPassword);
        TextView txtGoLoginPage = findViewById(R.id.txtGoBackToLoginPage);
        btnForgotPassword.setOnClickListener(this);
        txtGoLoginPage.setOnClickListener(this);
    }
    private void initializeAuthenticator(){
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.txtGoBackToLoginPage:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.btnResetPassword:
                firebaseAuth.sendPasswordResetEmail(edtForgotPasswordInput.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Password has sent to your email.",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
                break;
                default:
                    break;
        }
    }
}

package com.example.social.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.social.R;
import com.example.social.databinding.SignUpActivityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private SignUpActivityBinding signUpBinding;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = DataBindingUtil.setContentView(this,R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        signUpBinding.btnSignUp.setOnClickListener(this);
        signUpBinding.txtAlreadyAMember.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.btn_sign_up:
                String txtUsername = signUpBinding.edtSignUpUsername.getText().toString();
                String txtPass = signUpBinding.edtSignUpPassword.getText().toString();
                String txtMail = signUpBinding.edtSignUpEmail.getText().toString();
                if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtMail) || TextUtils.isEmpty(txtPass)) {
                    Toast.makeText(SignUpActivity.this, "Required field", Toast.LENGTH_SHORT).show();
                } else if (txtPass.length() < 6) {
                    Toast.makeText(this, "Please define more secure pass !", Toast.LENGTH_SHORT).show();
                } else {
                    registerFirebaseUser(txtUsername, txtMail, txtPass);
                } break;
             case R.id.txtAlreadyAMember:
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;
            default:break;
        }
    }
    private void registerFirebaseUser(final String userName,String mail,String pass){
        firebaseAuth.createUserWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                   FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                   if (firebaseUser != null) {
                       String userID = firebaseUser.getUid();
                       databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                       HashMap<String,String> hashMap = new HashMap<>();
                       hashMap.put("id",userID);
                       hashMap.put("username",userName);
                       hashMap.put("imageURL","default");
                       hashMap.put("status","offline");
                       hashMap.put("search",userName.toLowerCase());
                       hashMap.put("email",mail);
                       hashMap.put("description",getString(R.string.description));

                       databaseReference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                       });
                   }
                } else {
                    Toast.makeText(this, "An error occurred. Please check your network connection.", Toast.LENGTH_SHORT).show();
                }
        });
    }
}

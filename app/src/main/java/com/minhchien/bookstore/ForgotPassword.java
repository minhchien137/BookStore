package com.minhchien.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    EditText emailForgot;
    Button btnResetPass;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        emailForgot = (EditText) findViewById(R.id.phoneNumberReset);
        btnResetPass = (Button) findViewById(R.id.resetPasswordButton);
        tvLogin = (TextView) findViewById(R.id.LoginForgotText);


        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });


        // Login
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToLoginActivity();
            }
        });


    }

    private void resetPassword(){
        String email = emailForgot.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Vui lòng nhập địa chỉ email!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Email đặt lại mật khẩu đã được gửi!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(this, "Gửi thất bại. Vui lòng thử lại!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void changeToLoginActivity(){
        Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(loginIntent);
    }

}
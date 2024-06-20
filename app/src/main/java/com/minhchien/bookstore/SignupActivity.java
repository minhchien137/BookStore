package com.minhchien.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minhchien.bookstore.model.User;

import org.w3c.dom.Text;

public class SignupActivity extends AppCompatActivity {

    EditText txtFullName,txtPhone,txtPass;

    Button btnRegister;

    TextView tvSignupLogin;

    ImageView togglePWSignUp;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        txtFullName = (EditText) findViewById(R.id.HovatenSignup);
        txtPhone = (EditText) findViewById(R.id.sodienthoaiSignup);
        txtPass = (EditText) findViewById(R.id.matkhauSignup);
        btnRegister = (Button)findViewById(R.id.signupButton);
        tvSignupLogin = (TextView) findViewById(R.id.signupText);
        togglePWSignUp = (ImageView) findViewById(R.id.togglePasswordVisibilitySU);


        togglePWSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtPass.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    txtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePWSignUp.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    txtPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    togglePWSignUp.setImageResource(R.drawable.ic_visibility);
                }
                txtPass.setSelection(txtPass.getText().length());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        tvSignupLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToLoginActivity();
            }
        });
    }

    private void register(){
        String phone = txtPhone.getText().toString();
        String fullName = txtFullName.getText().toString();
        String pass = txtPass.getText().toString();
        if(isValid(phone,fullName,pass)){
            User user = new User(fullName,phone,pass,"None");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Check phone number is exist
                    if(snapshot.hasChild(phone)){
                        Toast.makeText(SignupActivity.this, "Số điện thoại này đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        myRef.child(user.getPhoneNumber()).setValue(user);
                        Toast.makeText(SignupActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        //Login
                        changeToLoginActivity();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }


    // check validate
    private Boolean isValid(String phone, String fullName, String password){
        String validNumber = "^[+]?[0-9]{8,15}$";
        if(!phone.matches(validNumber)){
            Toast.makeText(SignupActivity.this,"Số điện thoại này không hợp lệ!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(fullName.trim().isEmpty() || password.isEmpty()){
            Toast.makeText(SignupActivity.this,"Họ và tên không được để trống!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length() < 6){
            Toast.makeText(SignupActivity.this,"Mật khẩu phải dài hơn 6 ký tự!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void changeToLoginActivity(){
        Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(loginIntent);
    }
}
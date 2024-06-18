package com.minhchien.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minhchien.bookstore.admin.AdminMenuActivity;
import com.minhchien.bookstore.model.User;
import com.minhchien.bookstore.sharepreference.Constants;
import com.minhchien.bookstore.sharepreference.PreferenceManager;


public class LoginActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    EditText txtPhone,txtPass;
    TextView tvSignupLogin;
    Button btnLogin;
    private User user;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        txtPhone = (EditText) findViewById(R.id.sodienthoaiLogin);
        txtPass = (EditText) findViewById(R.id.matkhauLogin);
        btnLogin = (Button) findViewById(R.id.loginButton);
        tvSignupLogin = (TextView) findViewById(R.id.signupText);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = txtPhone.getText().toString().trim();
                String pass = txtPass.getText().toString().trim();
                if(phone.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Số điện thoại không được để trống!", Toast.LENGTH_SHORT).show();
                } else if (pass.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Mật khẩu không được để trống!", Toast.LENGTH_SHORT).show();
                } else{
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(phone)){
                                final User user = snapshot.child(phone).getValue(User.class);
                                if(user.getPassword().equals(pass)){
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    writeToSharedPreferences(user);
                                    //is admin
                                    if(user.getIsAdmin() == 1){
                                        Intent adminMenu = new Intent(getApplicationContext(), AdminMenuActivity.class);
                                        //adminMenu.putExtra(USER_KEY,user);
                                        startActivity(adminMenu);
                                    }
                                    else{
                                        //Main menu Activity
                                        //Gui user
                                        Intent menu = new Intent(getApplicationContext(),MenuActivity.class);
                                        //menu.putExtra(USER_KEY,user);
                                        startActivity(menu);
                                    }

                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!\nVui lòng kiểm tra lại", Toast.LENGTH_LONG).show();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

        tvSignupLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToRegisterActivity();
            }
        });




}
    private void writeToSharedPreferences(User user){
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
//        sharedPreferences = getSharedPreferences("book_store", Context.MODE_PRIVATE);
//        SharedPreferences.Editor  editor = sharedPreferences.edit();
        String phone = user.getPhoneNumber();
        int isAdmin = user.getIsAdmin();
        preferenceManager.putString(Constants.LOGIN_PHONE,phone);
        preferenceManager.putInt(Constants.LOGIN_IS_ADMIN,isAdmin);
//        editor.putString("phone",phone);
//        editor.putInt("isAdmin",isAdmin);
//        editor.commit();
    }

    private void changeToRegisterActivity(){
        Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
        startActivity(intent);
    }

}
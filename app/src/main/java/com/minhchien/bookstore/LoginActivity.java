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
import android.widget.Toast;

import com.minhchien.bookstore.model.User;


public class LoginActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    EditText txtPhone,txtPass;
    Button btnLogin, btnRegister;
    private User user;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

}}
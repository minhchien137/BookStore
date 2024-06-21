package com.minhchien.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.minhchien.bookstore.admin.AdminMenuActivity;
import com.minhchien.bookstore.sharepreference.Constants;
import com.minhchien.bookstore.sharepreference.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private static int TIME_OUT = 2000; //Time to launch the another activity

   PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferenceManager = new PreferenceManager(getApplicationContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //check login state
                String phone = preferenceManager.getString(Constants.LOGIN_PHONE);
                int isAdmin = preferenceManager.getInt(Constants.LOGIN_IS_ADMIN);
//               //Intent i = new Intent(MainActivity.this, LoginActivity.class);
                Intent i = new Intent(MainActivity.this,MenuActivity.class);
                if(phone != null){
                    if(isAdmin == 1){
                        i = new Intent(MainActivity.this, AdminMenuActivity.class);

                    }
                }
                else {
                    preferenceManager.clear();
                    i = new Intent(MainActivity.this,MenuActivity.class);
                }
                startActivity(i);
                finish();
            }
        },TIME_OUT);
    }

    }

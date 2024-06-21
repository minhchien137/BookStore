package com.minhchien.bookstore.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.minhchien.bookstore.AccountFragment;
import com.minhchien.bookstore.R;


import org.w3c.dom.Text;

public class AdminMenuActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    CRUDFragment crudFragment = new CRUDFragment();
    CategoryFragment categoryFragment = new CategoryFragment();
    OrderListFragment orderListFragment = new OrderListFragment();
    AdminListbook adminListbook = new AdminListbook();

    AccountFragment accountFragment = new AccountFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.admin_menu_nav);
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_menu_container,crudFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.crud)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_menu_container,crudFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.category) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_menu_container,categoryFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.order) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_menu_container,orderListFragment).commit();
                    return true;
                }
                else if (item.getItemId() == R.id.list_book){
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_menu_container,adminListbook).commit();
                    return true;
                } else if (item.getItemId() == R.id.account) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_menu_container,accountFragment).commit();
                    return true;
                } else {
                    return false;
                }
//
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
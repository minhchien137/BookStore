package com.minhchien.bookstore;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minhchien.bookstore.model.Book;
import com.minhchien.bookstore.sharepreference.Constants;
import com.minhchien.bookstore.sharepreference.PreferenceManager;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.N;
import org.w3c.dom.Text;

import java.util.Locale;

public class MenuActivity extends AppCompatActivity {

    public static BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();

    SearchFragment searchFragment = new SearchFragment();

    CartFragment cartFragment = new CartFragment();

    AccountFragment accountFragment = new AccountFragment();



    FirebaseDatabase firebaseDatabase;

    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("Books");
        initUI();
        if (getIntent().getExtras() != null){

            Book newBook = getIntent().getExtras().getParcelable("book-target");
            Log.d("Book", newBook.getTitleBook());
            ShowDetailFragment detailFragment = new ShowDetailFragment();
            Bundle bundle = new Bundle();  // Bundle de luu tru du lieu se duoc truyen cho Fragment
            bundle.putParcelable("book-target", newBook);
            detailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, detailFragment).commit();
            return;
        }
        else {
                handleEvenBookAdded();
                getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
        }


    }


    private void initUI(){
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.menu_nav);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                   if (item.getItemId() == R.id.home){
                       getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                       return true;
                   } else if (item.getItemId() == R.id.search) {
                       getSupportFragmentManager().beginTransaction().replace(R.id.container,searchFragment).commit();
                       return true;
                   } else if (item.getItemId() == R.id.cart) {
                       getSupportFragmentManager().beginTransaction().replace(R.id.container,cartFragment).commit();
                       return true;
                   } else if (item.getItemId() == R.id.account) {
                       getSupportFragmentManager().beginTransaction().replace(R.id.container, accountFragment).commit();
                       return true;
                   } else {
                       return false;
                   }
            }
        });

    }
        private void handleEvenBookAdded(){
            myRef.limitToLast(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (snapshot.exists()){
                        Book newBook = snapshot.getValue(Book.class);
                        sendNotification(newBook);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        private void sendNotification(Book newBook){

            PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext(), Constants.NOTIFICATION_PREFERENCE_NAME);
            String newBookId = preferenceManager.getString(Constants.NEW_BOOK_ID);
            if(newBookId != null && newBook.getIdBook().equals(newBookId)){
                return;
            }
            // Luu ID cuon sach moi vao Share
            preferenceManager.putString(Constants.NEW_BOOK_ID,newBook.getIdBook());
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel = null;
            // Tao kenh thong bao cho android (8.0 tro len)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            //Tao thong bao
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),Constants.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_book_foreground)
                    .setContentTitle(Constants.TITLE)
                    .setContentText(newBook.getTitleBook().toUpperCase(Locale.ROOT)+" đã có mặt trên BookStore, XEM NGAY!!!!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // dat muc do uu tien cho thong bao
                    .setAutoCancel(true);
            // Tao intent
            Intent intent = new Intent(getApplicationContext(),MenuActivity.class);
            intent.putExtra("book-target",newBook);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            Notification notification = builder.build();
            notificationManager.notify(0,notification);
        }
}
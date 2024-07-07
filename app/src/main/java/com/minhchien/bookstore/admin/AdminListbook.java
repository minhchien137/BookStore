package com.minhchien.bookstore.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minhchien.bookstore.R;
import com.minhchien.bookstore.customadapter.BookListViewAdapter;
import com.minhchien.bookstore.model.Book;
import com.minhchien.bookstore.model.Category;

import java.util.ArrayList;
import java.util.List;


public class AdminListbook extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    //biding
    ListView listView;

    Spinner spinnerCategory;

    ArrayList<Book> listBook;

    ArrayAdapter categoryAdapter;

    ArrayList<String> categorys;
    BookListViewAdapter adapter;
    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_listbook, container, false);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Books");
        listView = (ListView) view.findViewById(R.id.admin_lv);
        fragmentManager = getParentFragmentManager();
        listBook = new ArrayList<>();
        adapter = new BookListViewAdapter(getContext(), listBook, fragmentManager);
        // fill data Spiner Category
        spinnerCategory = (Spinner) view.findViewById(R.id.sp_listbook_categorys);
        categorys = new ArrayList<>();
        categoryAdapter = new ArrayAdapter(getContext(), R.layout.style_spinner, categorys);
        spinnerCategory.setAdapter(categoryAdapter);
        getCategory();

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categorys.get(position);
                UpdateTheoTheLoai(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView.setAdapter(adapter);
        getData();



        return view;
    }

    private void getCategory() {
        myRef = database.getReference("Categorys");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String cate = data.getValue(String.class);
                    categorys.add(cate);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getData() {
        myRef = database.getReference("Books");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listBook != null) {
                    listBook.clear();
                }
                for (DataSnapshot data : snapshot.getChildren()) {
                    Book book = data.getValue(Book.class);
                    listBook.add(book);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void UpdateTheoTheLoai(String category){
        listBook.clear();

        if (category.equals("Tất Cả")) {
            getData(); // Call getData to retrieve all books again
            return;
        }

        myRef = database.getReference("Books");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Book book = dataSnapshot.getValue(Book.class);
                    if (book.getCategoryBook().equals(category)){
                        listBook.add(book);
                    }
                }
                adapter.notifyDataSetChanged();
//
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
package com.minhchien.bookstore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.minhchien.bookstore.customadapter.CategoryAdapter;
import com.minhchien.bookstore.model.Book;
import com.minhchien.bookstore.model.Category;
import com.minhchien.bookstore.sharepreference.PreferenceManager;
import com.minhchien.bookstore.ui.FilterCustomDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SearchFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private PreferenceManager preferenceManager;

    TextView txtLichSuTimKiem;

    List<Category> mListCategorys;
    List<Book> listBooks;
    CategoryAdapter categoryAdapter;
    LinearLayout filter;
    SearchView searchView;
    RecyclerView mainRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        //Firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Books");
        mListCategorys = new ArrayList<Category>();
        listBooks = new ArrayList<>();

        preferenceManager = new PreferenceManager(requireContext(),"search_history");
        //data biding
        searchView = rootView.findViewById(R.id.search);
        mainRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_recycleView);
        txtLichSuTimKiem = rootView.findViewById(R.id.lichsutimkiem);
        filter = (LinearLayout) rootView.findViewById(R.id.filter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        mainRecyclerView.setLayoutManager(linearLayoutManager);
        FragmentManager fragmentManager = getParentFragmentManager();
        categoryAdapter = new CategoryAdapter(getContext(),fragmentManager,false);
        mainRecyclerView.setAdapter(categoryAdapter);
        displaySearchHistory();
        handleFilter();
        handleSearch();
        return rootView;
    }
    private void queryDB(String search){
        Query query = database.getReference("Books")
                .orderByChild("titleBook").startAt(search);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listBooks != null){
                    listBooks.clear();
                }
                if(mListCategorys != null){
                    mListCategorys.clear();
                }
                if(snapshot.exists()){
                    for (DataSnapshot data:snapshot.getChildren()){
                        Book b = data.getValue(Book.class);
                        if(b.getIsActive() == 1 && b.getInStockBook() > 0)
                            listBooks.add(b);
                    }
                    mListCategorys.add(new Category(null,listBooks));
                    categoryAdapter.setData(mListCategorys);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void handleFilter(){
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FilterCustomDialog filterCustomDialog = FilterCustomDialog.newInstance();
                filterCustomDialog.setCategoryAdapter(categoryAdapter);
                filterCustomDialog.show(fragmentManager,"test dialog");
            }
        });
    }
    private void handleSearch(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryDB(query);
                saveSearchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                queryDB(s);
                return false;
            }
        });


    }

    private void saveSearchQuery(String query) {
        Set<String> history = preferenceManager.getStringSet("history");
        if (history == null) {
            history = new HashSet<>();
        }
        if (!history.contains(query)) {
            history.add(query);
            preferenceManager.clear();
            preferenceManager.putStringSet("history", history);
        }
    }

    private void displaySearchHistory() {
        Set<String> history = preferenceManager.getStringSet("history");
        if (history != null) {
            StringBuilder historyText = new StringBuilder();
            for (String query : history) {
                historyText.append(query).append("   ");
            }
            txtLichSuTimKiem.setText(historyText.toString());
        }
    }





}
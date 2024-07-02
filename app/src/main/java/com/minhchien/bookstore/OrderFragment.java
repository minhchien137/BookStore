package com.minhchien.bookstore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minhchien.bookstore.customadapter.OrderAdapter;
import com.minhchien.bookstore.model.Order;
import com.minhchien.bookstore.sharepreference.Constants;
import com.minhchien.bookstore.sharepreference.PreferenceManager;

import java.util.ArrayList;

public class OrderFragment extends Fragment {

    private RecyclerView orderRv;

    private ArrayList<Order> orderArrayList;

    private OrderAdapter orderAdapter;

    private String phone;

    PreferenceManager preferenceManager;

    FragmentManager fragmentManager;


    public OrderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        orderRv = view.findViewById(R.id.order_recyclerview);
        preferenceManager = new PreferenceManager(getContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
        fragmentManager = getParentFragmentManager();
        loadOrder();
        return view;
    }

    private void loadOrder(){
        orderArrayList = new ArrayList<>();
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("Users");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderArrayList.clear();
                phone = preferenceManager.getString(Constants.LOGIN_PHONE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders");
                ref.orderByChild("orderBy").equalTo(phone).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    Order order = dataSnapshot.getValue(Order.class);
                                    orderArrayList.add(order);
                                }
                                orderAdapter = new OrderAdapter(getContext(),orderArrayList,fragmentManager);
                                orderRv.setAdapter(orderAdapter);
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
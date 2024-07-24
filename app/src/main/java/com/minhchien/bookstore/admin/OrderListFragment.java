package com.minhchien.bookstore.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minhchien.bookstore.R;
import com.minhchien.bookstore.customadapter.OrderAdapter;
import com.minhchien.bookstore.model.Order;
import com.minhchien.bookstore.sharepreference.Constants;
import com.minhchien.bookstore.sharepreference.PreferenceManager;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class OrderListFragment extends Fragment {

    private RecyclerView ordersRv;
    private ArrayList<Order> orderList;

    private OrderAdapter orderAdapter;

    private Spinner spinnerOrderListAdmin;
    private String phone;
    PreferenceManager preferenceManager;
    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_order_list, container, false);
        ordersRv = view.findViewById(R.id.ordersRv);
        spinnerOrderListAdmin = view.findViewById(R.id.spnnier_orderlist);
        preferenceManager = new PreferenceManager(getContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
        fragmentManager = getParentFragmentManager();

        orderList = new ArrayList<>();
        orderAdapter =  new OrderAdapter(getContext(), orderList, fragmentManager);
        ordersRv.setAdapter(orderAdapter);


        loadOrders();
        setupSpinner();
        return view;
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.trang_thai, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderListAdmin.setAdapter(adapter);

        spinnerOrderListAdmin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectStatus = parent.getItemAtPosition(position).toString();
                filterOrders(selectStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void filterOrders(String status){
        ArrayList<Order> orderArrayList = new ArrayList<>();
        if (status.equals("All")) {
            orderArrayList.addAll(orderList);
        } else {
            for (Order order : orderList) {
                if (order.getOrderStatus().equals(status)) {
                    orderArrayList.add(order);
                }
            }
        }
        orderAdapter.updateOrderList(orderArrayList);
    }
    private void loadOrders() {
        orderList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                phone = preferenceManager.getString(Constants.LOGIN_PHONE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for( DataSnapshot ds : snapshot.getChildren()){
                                Order order = ds.getValue(Order.class);
                                orderList.add(order);
                            }
                            orderAdapter = new OrderAdapter(getContext(),orderList,fragmentManager);
                            ordersRv.setAdapter(orderAdapter);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Do nothing
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
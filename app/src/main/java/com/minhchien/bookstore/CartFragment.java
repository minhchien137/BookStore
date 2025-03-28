package com.minhchien.bookstore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.minhchien.bookstore.customadapter.CartItemAdapter;
import com.minhchien.bookstore.database.CardDao;
import com.minhchien.bookstore.model.CartItem;
import com.minhchien.bookstore.sharepreference.Constants;
import com.minhchien.bookstore.sharepreference.PreferenceManager;
import com.minhchien.bookstore.ui.FormatCurrency;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment {

    public static RecyclerView cartRecyclerView;
    public static TextView txtCartValue;
    Button btnBack;
    Button btnCheckOut;
    List<CartItem> cart;
    CartItemAdapter cartItemAdapter;
    CardDao cartDao;
    List<Integer>cartItemValues;
    Fragment checkOutFragment = new CheckOutFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        cartDao = new CardDao(getContext());
        cart = new ArrayList<>();
        //biding
        cartRecyclerView = (RecyclerView) view.findViewById(R.id.cart_recyclerview);
        txtCartValue = (TextView) view.findViewById(R.id.txtTongGioHang);
        btnBack = (Button) view.findViewById(R.id.cart_btnBack);
        btnCheckOut = (Button) view.findViewById(R.id.cart_btnCheckOut);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        cartRecyclerView.setLayoutManager(linearLayoutManager);
        FragmentManager fragmentManager = getParentFragmentManager();
        cartItemValues = new ArrayList<>();
        cartItemAdapter = new CartItemAdapter(getContext(),fragmentManager);
        cartRecyclerView.setAdapter(cartItemAdapter);
        getListCarts();
        //handle event
        handleBtnCheckOut();
        handleBtnBack();
        return view;
    }
    private void getListCarts(){
        cart = cartDao.getAll();
        cartItemAdapter.setData(cart);
        updateCartValue();

    }
    public static void updateCartValue(){
        View child;
        int sum = 0;
        for(int i=0;i<cartRecyclerView.getChildCount();i++){
            child = cartRecyclerView.getChildAt(i);
            RecyclerView.ViewHolder holder = cartRecyclerView.getChildViewHolder(child);
            TextView txtPrice = holder.itemView.findViewById(R.id.lv_txt_price);
            int price = Integer.parseInt(txtPrice.getText().toString());
            sum += price;
        }
        txtCartValue.setText(FormatCurrency.formatVND(sum));
    }
    private void handleBtnBack(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }
    private void handleBtnCheckOut(){
        PreferenceManager preferenceManager = new PreferenceManager(getContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
        String phone = preferenceManager.getString(Constants.LOGIN_PHONE);
        if(phone == null){
            Toast.makeText(getContext(), "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
            return;
        }
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView editText = v.findViewById(R.id.txtTongGioHang);
                View child;
                int sum = 0;
                for(int i=0;i<cartRecyclerView.getChildCount();i++){
                    child = cartRecyclerView.getChildAt(i);
                    RecyclerView.ViewHolder holder = cartRecyclerView.getChildViewHolder(child);
                    TextView txtPrice = holder.itemView.findViewById(R.id.lv_txt_price);
                    int price = Integer.parseInt(txtPrice.getText().toString());
                    sum += price;
                }
                Bundle result = new Bundle();
                result.putInt("value",sum);
                getParentFragmentManager().setFragmentResult("data",result);
                getParentFragmentManager().beginTransaction().replace(R.id.container,checkOutFragment).commit();
//                PreferenceManager preferenceManager = new PreferenceManager(getContext(),Constants.LOGIN_KEY_PREFERENCE_NAME);
//                int isAdmin = preferenceManager.getInt(Constants.LOGIN_IS_ADMIN);
//                if(isAdmin == 1){
//                    getParentFragmentManager().beginTransaction().replace(R.id.admin_menu_container,checkOutFragment).commit();
//                }
//                else getParentFragmentManager().beginTransaction().replace(R.id.container,checkOutFragment).commit();
            }
        });
    }
}
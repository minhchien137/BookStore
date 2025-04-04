package com.minhchien.bookstore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minhchien.bookstore.customadapter.CartItemAdapter;
import com.minhchien.bookstore.database.BookDao;
import com.minhchien.bookstore.database.CardDao;
import com.minhchien.bookstore.model.CartItem;
import com.minhchien.bookstore.model.User;
import com.minhchien.bookstore.sharepreference.Constants;
import com.minhchien.bookstore.sharepreference.PreferenceManager;
import com.minhchien.bookstore.ui.FormatCurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckOutFragment extends Fragment {

    EditText txtPhone;
    EditText txtAddress;
    EditText txtName;
    TextView txtValue;
    private DatabaseReference reference;
    Button btnBack;
    Button btnConfirm;
    private String phone;
    List<CartItem> cart;
    CartItemAdapter cartItemAdapter;
    CardDao cartDao;
    BookDao bookDao;
    List<Integer>cartItemValues;
    public CheckOutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_out, container, false);
        cartDao = new CardDao(getContext());
        bookDao = new BookDao(getContext());
        cart = new ArrayList<>();
        txtName = (EditText) view.findViewById(R.id.checkout_txtname);
        txtPhone = (EditText) view.findViewById(R.id.checkout_txtphone);
        txtAddress = (EditText) view.findViewById(R.id.checkout_txtaddress);
        btnBack = (Button) view.findViewById(R.id.checkout_btnBack);
        btnConfirm = (Button) view.findViewById(R.id.checkout_btnConfirm);
        PreferenceManager preferenceManager = new PreferenceManager(getContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
        phone = preferenceManager.getString(Constants.LOGIN_PHONE);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User profile = snapshot.getValue(User.class);

                if (profile != null){
                    String name = profile.getFullName();
                    String address = profile.getAddress();

                    txtName.setText(name);
                    txtAddress.setText(address);
                    txtPhone.setText(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Thiết lập FragmentResultListener để nhận giá trị tổng đơn hàng từ CartFragment.
        getParentFragmentManager().setFragmentResultListener("data", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int data = result.getInt("value");
                txtValue = (TextView) view.findViewById(R.id.txtTongHoaDon);
                int value = data + 20000;
                txtValue.setText(FormatCurrency.formatVND(value));
            }
        });
        FragmentManager fragmentManager = getParentFragmentManager();
        cartItemValues = new ArrayList<>();
        cartItemAdapter = new CartItemAdapter(getContext(),fragmentManager);
        cart = cartDao.getAll();
        cartItemAdapter.setData(cart);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() == 0)
                {
                    Toast.makeText(getActivity(), "Không có sản phẩm trong giỏ hàng", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitOrder();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartFragment cartFragment = new CartFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.container, cartFragment) // Thay thế CheckOutFragment bằng CartFragment
                        .addToBackStack(null)  // Thêm CartFragment vào back stack
                        .commit();
            }
        });
        return view;
    }

    private void submitOrder() {
        String timestamp = ""+System.currentTimeMillis();
        String cost = txtValue.getText().toString().trim().replace("đ","");
        String phoneNum = txtPhone.getText().toString().trim();
        // Tạo một HashMap để lưu trữ thông tin đơn hàng.
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId",""+timestamp);
        hashMap.put("orderTime",""+timestamp);
        hashMap.put("orderCost",""+cost);
        hashMap.put("orderStatus","In Progess");
        hashMap.put("orderBy",""+phoneNum);


        // Lưu thông tin đơn hàng vào Firebase Realtime Database.
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
        databaseReference.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                for(int i=0; i<cart.size();i++){
                    String Id = String.valueOf(cart.get(i).getIdCart());
                    String num = String.valueOf(cart.get(i).getNumCart());
                    String bookId = cart.get(i).getBookId();


                    HashMap<String, String> hashMap1 = new HashMap<>();
                    hashMap1.put("Id",Id);
                    hashMap1.put("BookId",bookId);
                    hashMap1.put("Quantity",num);
                    databaseReference.child(timestamp).child("Items").child(Id).setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Cập nhật số lượng sản phẩm trong kho.
                            boolean result = bookDao.updateBookInStock(bookId,Integer.parseInt(num));
                            if(result)
                                return;
                        }
                    });
                }
                Toast.makeText(getActivity(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                // Xóa tất cả sản phẩm trong giỏ hàng.
                cartDao.deleteAll();
                // Tạo OrderDetailFragment và chuyển hướng đến nó, truyền thông tin đơn hàng qua Bundle.
                OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("orderId",timestamp);
                bundle.putString("orderBy",phoneNum);
                orderDetailFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.container,orderDetailFragment)
                        .addToBackStack(null).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
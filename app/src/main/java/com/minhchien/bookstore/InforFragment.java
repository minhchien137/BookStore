package com.minhchien.bookstore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minhchien.bookstore.model.User;
import com.minhchien.bookstore.sharepreference.Constants;
import com.minhchien.bookstore.sharepreference.PreferenceManager;


public class InforFragment extends Fragment {
    EditText txtPhone;
    EditText txtAddress;
    EditText txtName;
    EditText txtGmail;
    Button btnBack;
    Button btnUpdateInfor;
    Fragment updateInforFragment = new UpdateInforFragment();
    private DatabaseReference reference;

    private String phone;

    public InforFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_infor, container, false);
        btnBack = (Button) view.findViewById(R.id.infor_btnBack);
        btnUpdateInfor = (Button) view.findViewById(R.id.infor_btnUpdate);
        txtName = (EditText) view.findViewById(R.id.infor_txtname);
        txtPhone = (EditText) view.findViewById(R.id.infor_txtphone);
        txtGmail = (EditText) view.findViewById(R.id.infor_txtgmail);
        txtAddress = (EditText) view.findViewById(R.id.infor_txtaddress);
        handleUpdateInfor();
        PreferenceManager preferenceManager = new PreferenceManager(getContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
        phone = preferenceManager.getString(Constants.LOGIN_PHONE);
        if(phone == null) {
            Toast.makeText(getContext(), "Đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
            return view;
        }
        reference = FirebaseDatabase.getInstance().getReference("Users");

        // Sử dụng addListenerForSingleValueEvent để lấy thông tin người dùng từ Firebase dựa trên số điện thoại.
        reference.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User profile = snapshot.getValue(User.class);

                if (profile != null) {
                    String name = profile.getFullName();
                    String address = profile.getAddress();
                    String gmail = profile.getGmail();

                    txtName.setText(name);
                    txtAddress.setText(address);
                    txtPhone.setText(phone);
                    txtGmail.setText(gmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new AccountFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;


        }

        private void handleUpdateInfor(){
            btnUpdateInfor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PreferenceManager preferenceManager = new PreferenceManager(getContext(),Constants.LOGIN_KEY_PREFERENCE_NAME);
                    // Lấy giá trị isAdmin từ SharedPreferences.
                    int isAdmin = preferenceManager.getInt(Constants.LOGIN_IS_ADMIN);
                    if(isAdmin == 1){
                        getParentFragmentManager().beginTransaction().replace(R.id.admin_menu_container,updateInforFragment).commit();
                    }
                    else getParentFragmentManager().beginTransaction().replace(R.id.container,updateInforFragment).commit();
                }
            });
    }
}
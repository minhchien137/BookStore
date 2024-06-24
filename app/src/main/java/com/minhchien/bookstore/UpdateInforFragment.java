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


public class UpdateInforFragment extends Fragment {

    EditText txtName, txtDiachi;

    Button btnSave, btnBack;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference reference;

    private String phone,name, address;

    public UpdateInforFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update_infor, container, false);
        txtName = view.findViewById(R.id.updateinfor_txtname);
        txtDiachi = view.findViewById(R.id.updateinfor_txtaddress);
        btnSave = view.findViewById(R.id.updateinfor_btnUpdate);
        btnBack = view.findViewById(R.id.updateinfor_btnBack);

        reference = FirebaseDatabase.getInstance().getReference("Users");
        showProfile();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateInfor();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackInfor();
            }
        });

        return view;

    }

    private void showProfile() {
        PreferenceManager preferenceManager = new PreferenceManager(getContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
        phone = preferenceManager.getString(Constants.LOGIN_PHONE);
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User profile = snapshot.getValue(User.class);

                if (profile != null){
                    name = profile.getFullName();
                    address = profile.getAddress();

                    txtName.setText(name);
                    txtDiachi.setText(address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void UpdateInfor(){
        if (isNameChanged() || isAddressChanged()){
            Toast.makeText(getActivity(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "Cập nhật thông tin thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    public void BackInfor(){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new InforFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private boolean isNameChanged(){
        PreferenceManager preferenceManager = new PreferenceManager(getContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
        phone = preferenceManager.getString(Constants.LOGIN_PHONE);
        if (!name.equals(txtName.getText().toString())){
            reference.child(phone).child("fullName").setValue(txtName.getText().toString());
            name = txtName.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isAddressChanged(){
        PreferenceManager preferenceManager = new PreferenceManager(getContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
        address = preferenceManager.getString(Constants.LOGIN_PHONE);
        if (!address.equals(txtDiachi.getText().toString())){
            reference.child(address).child("address").setValue(txtDiachi.getText().toString());
            address = txtDiachi.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }
}
package com.minhchien.bookstore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minhchien.bookstore.sharepreference.Constants;
import com.minhchien.bookstore.sharepreference.PreferenceManager;


public class ChangePasswordFragment extends Fragment {

    EditText txtMKrecent, txtNewMK, txtNewMKXacNhan;

    Button btnSave, btnBack;

    ImageView tgPW, tgNew , tgNewRQ;

    private String phone, pass;

    DatabaseReference referencer;

    public ChangePasswordFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        txtMKrecent = view.findViewById(R.id.changePass_pass);
        txtNewMK = view.findViewById(R.id.changePass_newPass);
        txtNewMKXacNhan = view.findViewById(R.id.changePass_newPassXacNhan);
        btnSave = view.findViewById(R.id.changePass_btnSave);
        btnBack = view.findViewById(R.id.changePass_btnBack);
        tgPW = view.findViewById(R.id.tgPW);
        tgNew = view.findViewById(R.id.tgNew);
        tgNewRQ = view.findViewById(R.id.tgNewRQ);

        tgPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtMKrecent.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    txtMKrecent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    tgPW.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    txtMKrecent.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    tgPW.setImageResource(R.drawable.ic_visibility);
                }
                txtMKrecent.setSelection(txtMKrecent.getText().length());
            }
        });

        tgNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNewMK.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    txtNewMK.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    tgNew.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    txtNewMK.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    tgNew.setImageResource(R.drawable.ic_visibility);
                }
                txtNewMK.setSelection(txtNewMK.getText().length());
            }
        });

        tgNewRQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNewMKXacNhan.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    txtNewMKXacNhan.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    tgNewRQ.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    txtNewMKXacNhan.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    tgNewRQ.setImageResource(R.drawable.ic_visibility);
                }
                txtNewMKXacNhan.setSelection(txtNewMKXacNhan.getText().length());
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
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

    private void updatePassword() {

        // readData de doc mat khau hien tai tu User tu database
        readData(new MyCallBack() {
            @Override
            public void onCallback(String value) {
                PreferenceManager preferenceManager = new PreferenceManager(getContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
                phone = preferenceManager.getString(Constants.LOGIN_PHONE);
                referencer = FirebaseDatabase.getInstance().getReference("Users");
                pass = value;

                // So sanh mat khau hien tai voi mat khau User nhap
                if (pass.equals(txtMKrecent.getText().toString())) {
                    String newPass = txtNewMK.getText().toString();
                    String confirmNewPass = txtNewMKXacNhan.getText().toString();

                    // Kiểm tra mật khẩu mới không được trùng với mật khẩu hiện tại
                    if (!newPass.equals(pass) && newPass.length() >= 6 && newPass.equals(confirmNewPass)) {
                        referencer.child(phone).child("password").setValue(newPass);
                        Toast.makeText(getActivity(), "Thay đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    } else if (newPass.equals(pass)) {
                        Toast.makeText(getActivity(), "Mật khẩu mới không được trùng với mật khẩu hiện tại!", Toast.LENGTH_SHORT).show();
                    } else if (newPass.length() < 6) {
                        Toast.makeText(getActivity(), "Vui lòng nhập mật khẩu dài hơn 6 ký tự!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Mật khẩu mới và mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Thay đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // readData doc so dien thoai cua nguoi dung tu PreferenManager

    public void readData(MyCallBack myCallback) {
        PreferenceManager preferenceManager = new PreferenceManager(getContext(), Constants.LOGIN_KEY_PREFERENCE_NAME);
        phone = preferenceManager.getString(Constants.LOGIN_PHONE);
        referencer = FirebaseDatabase.getInstance().getReference("Users");
        referencer.child(phone).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                myCallback.onCallback(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}
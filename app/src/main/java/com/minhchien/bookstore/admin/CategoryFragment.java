package com.minhchien.bookstore.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minhchien.bookstore.R;
import com.minhchien.bookstore.SignupActivity;

import java.util.ArrayList;


public class CategoryFragment extends Fragment {
    private FirebaseDatabase database;

    private DatabaseReference myRef;

    EditText txtCategory;

    Button btnAdd;

    ListView cateListview;

    ArrayList<String> listCate;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_category, container, false);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Categorys");
        txtCategory = (EditText) view.findViewById(R.id.category_txt);
        btnAdd = (Button) view.findViewById(R.id.category_btn_add);
        String cateName = txtCategory.getText().toString();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cateName = txtCategory.getText().toString();

                if (cateName.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Không được để trống tên thể lọai!", Toast.LENGTH_SHORT).show();
                } else {
                    // Kiểm tra xem thể loại đã tồn tại chưa
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(cateName)) {
                                Toast.makeText(getContext(), "Đã tồn tại thể loại này", Toast.LENGTH_SHORT).show();
                            } else {
                                // Thêm thể loại vào database
                                myRef.push().setValue(cateName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Thêm thể loại thành công", Toast.LENGTH_SHORT).show();
                                            txtCategory.setText("");
                                        } else {
                                            Toast.makeText(getContext(), "Thêm thể loại thất bại\nVui lòng thử lại!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


        listCate = new ArrayList<>();
        cateListview = (ListView) view.findViewById(R.id.category_lv);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, listCate);
        cateListview.setAdapter(adapter);


        cateListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String cateName = listCate.get(position);

                // Hiển thị hộp thoại xác nhận xóa
                new AlertDialog.Builder(getContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa " + cateName + "?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Xóa thể loại khỏi database
                                deleteCategory(cateName);
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();

                return true; // Trả về true để ngăn chặn sự kiện click bình thường
            }
        });



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listCate != null) {
                    listCate.clear();
                }
                for (DataSnapshot data : snapshot.getChildren()) {
                    String category = data.getValue(String.class);
                    listCate.add(category);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;

    }

    private void deleteCategory(String cateName) {
    }

    }


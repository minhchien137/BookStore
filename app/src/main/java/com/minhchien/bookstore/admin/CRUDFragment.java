package com.minhchien.bookstore.admin;

import android.app.AlertDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minhchien.bookstore.R;
import com.minhchien.bookstore.database.BookDao;
import com.minhchien.bookstore.model.Book;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CRUDFragment extends Fragment {

    //Realtime Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    //Firebase Storage
    FirebaseStorage storage;

    StorageReference storageReference;
    //Data biding
    EditText txtTitle, txtAuthor,txtYear,txtPrice,txtNum,txtDes, txtVersion, txtCompany;
    Switch swActive;
    Spinner snCategory;
    Button btnAdd,btnAddImg;
    ImageView img;
    Uri imgUri;
    Book book;
    ArrayList<String> categorys;
    ArrayAdapter categoryAdapter;
    //
    ActivityResultLauncher<String> getImage;
    //
    AlertDialog dialog;
    //BookDao
    BookDao dao;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_c_r_u_d, container, false);
        //get Realtime
        database = FirebaseDatabase.getInstance();
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        //biding
        book = new Book();

        txtTitle = (EditText) view.findViewById(R.id.crud_title);
        txtAuthor = (EditText) view.findViewById(R.id.crud_author);
        txtYear = (EditText) view.findViewById(R.id.crud_year);
        txtPrice = (EditText) view.findViewById(R.id.crud_price);
        txtNum = (EditText) view.findViewById(R.id.crud_in_stock);
        txtDes = (EditText) view.findViewById(R.id.crud_des);
        txtVersion = (EditText) view.findViewById(R.id.crud_version);
        txtCompany = (EditText) view.findViewById(R.id.crud_congtyphathanh);
        swActive = (Switch) view.findViewById(R.id.crud_sw_active);
        snCategory = (Spinner) view.findViewById(R.id.crud_categorys);
        img = (ImageView) view.findViewById(R.id.crud_img);
        //Button add image
        btnAddImg = (Button) view.findViewById(R.id.crud_btn_img) ;
        //Button add book to DB
        btnAdd = (Button) view.findViewById(R.id.crud_btn_add);
        //take image from gallery
        getImage = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        img.setImageURI(result);
                        imgUri = result;
                    }
                }
        );
        //
        dao = new BookDao(getContext());
        //Fill data category
        categorys = new ArrayList<>();
        categoryAdapter = new ArrayAdapter(getContext(),R.layout.style_spinner,categorys);
        snCategory.setAdapter(categoryAdapter);
        getCategory();
        //Dialog
        setProgressDialog();
        //Handle event
        onGetImageClick();
        onAddClick();
        return view;
    }
    private void onGetImageClick(){
        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage.launch("image/*");

            }
        });
    }
    private void onAddClick(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check data valid
                String title = txtTitle.getText().toString();
                String author = txtAuthor.getText().toString();
                String category = String.valueOf(snCategory.getSelectedItem());
                String year = txtYear.getText().toString();
                String price = txtPrice.getText().toString();
                String version = txtVersion.getText().toString();
                String company = txtCompany.getText().toString();
                String inStock = txtNum.getText().toString();
                String desc = txtDes.getText().toString();
                int isActive = 1;
                if(!swActive.isChecked()){
                    isActive = 0;
                }
                if(isValid(title,author,category,year,version,company,price,desc,inStock)){
                    //Upload image and get link
                    if(imgUri != null){
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                        Date now = new Date();
                        String fileName = formatter.format(now);
                        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);
                        int finalIsActive = isActive;
                        storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imgURL = uri.toString();
                                        book.setImgURLBook(imgURL);
                                        //Add to DB
                                        if(book.getImgURLBook() != null){
                                            //Create book object
                                            book.setTitleBook(title);
                                            book.setAuthor(author);
                                            book.setCategoryBook(category);
                                            book.setCompanyBook(company);
                                            book.setVersionBook(version);
                                            book.setYearBook(Integer.parseInt(year));
                                            book.setPriceBook(Integer.parseInt(price));
                                            book.setInStockBook(Integer.parseInt(inStock));
                                            book.setDescriptionBook(desc);
                                            book.setIsActive(finalIsActive);
                                            if(dao.addBook(book)){
                                                clearEditText();
                                            }
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Tải hình ảnh thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                dialog.show();
                            }
                        });

                    }
                    else {
                        Toast.makeText(getContext(), "Ảnh bìa không được để trống", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
    private boolean isValid(String title,String author,String category,String year,String version, String company, String price,String desc,String inStock){
        if(title.trim().isEmpty() || author.trim().isEmpty() || year.trim().isEmpty() || version.trim().isEmpty() || company.trim().isEmpty() ||
                category.trim().isEmpty() || price.trim().isEmpty() || inStock.trim().isEmpty() || desc.trim().isEmpty()){
            Toast.makeText(getContext(), "Các trường không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        int y = Integer.parseInt(year);
        int num = Integer.parseInt(inStock);
        int p = Integer.parseInt(price);
        if(y < 0 && y > 2024){
            Toast.makeText(getContext(), "Năm xuất bản không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(num < 0){
            Toast.makeText(getContext(), "Số lượng phải lớn hơn hoặc bằng 0", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(p<=0){
            Toast.makeText(getContext(), "Giá bán phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void getCategory(){
        myRef = database.getReference("Categorys");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
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
    private  void clearEditText(){
        txtTitle.setText("");
        txtAuthor.setText("");
        txtDes.setText("");
        txtNum.setText("");
        txtPrice.setText("");
        txtYear.setText("");
        txtVersion.setText("");
        txtCompany.setText("");
        img.setImageResource(R.drawable.ic_menu_gallery);
    }
    //progress dialog
    private void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(getContext());
        tvText.setText("Đang tải ảnh lên...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }
}
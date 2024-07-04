package com.minhchien.bookstore.admin;

import android.annotation.SuppressLint;
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

import com.bumptech.glide.Glide;
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

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class UpdateFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    FirebaseStorage storage;
    StorageReference storageReference;
    EditText txtTitle, txtAuthor, txtYear, txtVersion, txtCompany, txtNum, txtPrice, txtDes;
   // Tat cảnh báo lint có tên "UseSwitchCompatOrMaterialCode" .
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch swActive;
    Spinner snCategory;
    Button btnUpdate, btnAddImg;
    ImageView img;

    Uri imgUri;
    Book book;
    ArrayList<String> category;

    ArrayAdapter categoryAdapter;

    ActivityResultLauncher<String> getImage;

    AlertDialog alertDialog;

    BookDao dao;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_update, container, false);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        book = new Book();
        txtTitle = (EditText) view.findViewById(R.id.crud_title);
        txtAuthor = (EditText) view.findViewById(R.id.crud_author);
        txtYear  = (EditText) view.findViewById(R.id.crud_year);
        txtVersion = (EditText) view.findViewById(R.id.crud_version);
        txtCompany = (EditText) view.findViewById(R.id.crud_congtyphathanh);
        txtNum = (EditText) view.findViewById(R.id.crud_in_stock);
        txtDes = (EditText) view.findViewById(R.id.crud_des);
        txtPrice = (EditText) view.findViewById(R.id.crud_price);
        swActive = (Switch) view.findViewById(R.id.crud_sw_active);
        img = (ImageView) view.findViewById(R.id.crud_img);
        snCategory = (Spinner) view.findViewById(R.id.crud_categorys);

        // button add image
        btnAddImg = (Button) view.findViewById(R.id.crud_btn_img);

        // Take image from gallery
        getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                img.setImageURI(result);
                imgUri = result;
            }
        });

        // Book Dao

        dao = new BookDao(getContext());
        btnUpdate = (Button) view.findViewById(R.id.crud_btn_update);

        // Fill data category
        category = new ArrayList<>();
        categoryAdapter = new ArrayAdapter(getContext(), R.layout.style_spinner, category);
        snCategory.setAdapter(categoryAdapter);
        getCategory();

        // get arguments
        Bundle bundle = getArguments();
        if(bundle != null){
            String bookId = bundle.getString("BOOK_ID");
        // them
            String bookTitle = bundle.getString("BOOK_TITLE");
            String bookAuthor = bundle.getString("BOOK_AUTHOR");
            String bookYear = bundle.getString("BOOK_YEAR");
            String bookVersion = bundle.getString("BOOK_VERSION");
            String bookCompany = bundle.getString("BOOK_COMPANY");
            String bookinStock = bundle.getString("BOOK_SOLUONG");
            String bookDes = bundle.getString("BOOK_DES");
            String bookPrice = bundle.getString("BOOK_PRICE");
            txtTitle.setText(bookTitle);
            txtAuthor.setText(bookAuthor);
            txtYear.setText(bookYear);
            txtVersion.setText(bookVersion);
            txtCompany.setText(bookCompany);
            txtNum.setText(bookinStock);
            txtDes.setText(bookDes);
            txtPrice.setText(bookPrice);
            book.setIdBook(bookId);
            getBookInfo();
        }

        // Dialog
        setProgressDialog();
        onGetImageClick();
        onUpdataClick();
        return view;

    }

    private void onUpdataClick(){
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = txtTitle.getText().toString();
                String author = txtAuthor.getText().toString();
                String category = String.valueOf(snCategory.getSelectedItem());
                String year =  txtYear.getText().toString();
                String version = txtVersion.getText().toString();
                String company = txtCompany.getText().toString();
                String inStock = txtNum.getText().toString();
                String price = txtPrice.getText().toString();
                String des = txtDes.getText().toString();
                int isActive = 1;
                if (!swActive.isChecked()){
                        isActive = 0;
                }
                if (isValid(title,author,category,year,version,company,inStock,price,des)){
                    int yearVal = Integer.parseInt(year);
                    int priceVal = Integer.parseInt(price);
                    int inStockVal = Integer.parseInt(inStock);
                    //imgUri != null -> upload image
                    //reset book -> upload img -> update
                    if (imgUri != null){
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
                                        // Add to DB
                                        if (book.getImgURLBook() != null){
                                            if (dao.updateBook(book.getIdBook(),title,author,category,imgURL,company,version,yearVal,priceVal,inStockVal,des,finalIsActive)){
                                                getBookInfo();
                                            }
                                            alertDialog.dismiss();

                                        }
                                    }
                                });
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    alertDialog.show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alertDialog.dismiss();
                            }
                        });
                    }else {
                        if (dao.updateBook(book.getIdBook(),title,author,category,book.getImgURLBook(),company,version,yearVal,priceVal,inStockVal,des, isActive)){
                            getBookInfo();
                        }
                    }
                }
            }
        });
    }
    private void onGetImageClick(){
        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage.launch("image/*");
            }
        });
    }

    private void getBookInfo(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(book.getIdBook())){
                    book = snapshot.child(book.getIdBook()).getValue(Book.class);
                    fillData();
                }
                else {
                    Toast.makeText(getContext(), "Không tồn tại!", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

private void fillData(){
    txtTitle.setText(book.getTitleBook());
    txtAuthor.setText(book.getAuthor());
    ArrayAdapter adapter = (ArrayAdapter)snCategory.getAdapter();
    if (adapter != null){
        int selectIndex = adapter.getPosition(book.getCategoryBook());
        snCategory.setSelection(selectIndex);
    }
    txtCompany.setText(book.getCompanyBook());
    txtVersion.setText(book.getVersionBook());
    txtPrice.setText(Integer.toString(book.getPriceBook()));
    txtYear.setText(Integer.toString(book.getYearBook()));
    txtNum.setText(Integer.toString(book.getInStockBook()));
    txtDes.setText(book.getDescriptionBook());
    swActive.setChecked(book.getIsActive() == 1);
    // Get image from URL
    Glide.with(getContext()).load(book.getImgURLBook()).into(img);
}

    private void getCategory(){
        myRef = database.getReference("Categorys");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    String cate = data.getValue(String.class);
                    category.add(cate);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

private boolean isValid(String title,String author,String category ,String year,String version,String company,String inStock,String price,String desc){
    if (title.trim().isEmpty() || author.trim().isEmpty() || category.trim().isEmpty() || year.trim().isEmpty()
    || version.trim().isEmpty() || company.trim().isEmpty() || inStock.trim().isEmpty() || price.trim().isEmpty() || desc.trim().isEmpty()){
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

    private void clearEditText(){
        txtTitle.setText("");
        txtAuthor.setText("");
        txtVersion.setText("");
        txtCompany.setText("");
        txtDes.setText("");
        txtPrice.setText("");
        txtYear.setText("");
        txtNum.setText("");
        img.setImageResource(R.drawable.ic_menu_gallery);
    }

    // progress dialog
    private void setProgressDialog() {
        int llPadding = 30;
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        ll.setLayoutParams(layoutParams);

        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(layoutParams);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        TextView textView = new TextView(getContext());
        textView.setText("Đang tải ảnh lên.......");
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setTextSize(20);
        textView.setLayoutParams(layoutParams);

        ll.addView(progressBar);
        ll.addView(textView);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setView(ll);

        alertDialog = builder.create();
        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams1 = new WindowManager.LayoutParams();
            layoutParams1.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams1.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams1.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams1);
        }

    }
}
package com.minhchien.bookstore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.minhchien.bookstore.customadapter.BookAdapter;
import com.minhchien.bookstore.database.CardDao;
import com.minhchien.bookstore.model.Book;
import com.minhchien.bookstore.model.CartItem;
import com.minhchien.bookstore.ui.FormatCurrency;

import java.util.ArrayList;
import java.util.List;

public class ShowDetailFragment extends Fragment {

    TextView txtTitle, txtPrice, txtDes, txtAuthor, txtYear, txtCate, txtNum, txtVersion, txtCompany;

    ImageView img;

    Button btnGiam, btnTang, btnAddToCart, btnBack;

    RecyclerView sanphamtuongtu;

    BookAdapter similarProductsAdaper;

    List<Book> similarProductsList;

    Book book;

    int numofBook;

    CardDao cardDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.fragment_show_detail, container, false);
    txtTitle = (TextView) view.findViewById(R.id.titleTxt);
    txtPrice = (TextView) view.findViewById(R.id.priceTxt);
    txtAuthor = (TextView) view.findViewById(R.id.detail_author);
    txtYear = (TextView) view.findViewById(R.id.detail_year);
    txtCate = (TextView) view.findViewById(R.id.detail_category);
    txtVersion = (TextView) view.findViewById(R.id.detail_phienban);
    txtCompany = (TextView) view.findViewById(R.id.detail_congtyphathanh);
    txtDes = (TextView) view.findViewById(R.id.descriptionTxt);
    btnGiam = (Button) view.findViewById(R.id.btngiamsoluong);
    btnTang = (Button) view.findViewById(R.id.btntangsoloung);
    img = (ImageView) view.findViewById(R.id.detail_image);
    btnAddToCart = (Button) view.findViewById(R.id.detail_btnAddToCart);
    btnBack = (Button) view.findViewById(R.id.detail_btnBack);
    txtNum = (TextView) view.findViewById(R.id.txtsoluong);
    sanphamtuongtu = (RecyclerView) view.findViewById(R.id.similar_products_recycler_view);
    similarProductsList = new ArrayList<>();
    similarProductsAdaper = new BookAdapter(getContext(),getParentFragmentManager());
    sanphamtuongtu.setLayoutManager(new GridLayoutManager(getContext(), 3));
    sanphamtuongtu.setAdapter(similarProductsAdaper);
    numofBook = 1;
    Bundle bundle = getArguments();
    if (bundle != null){

        Log.d("Bundle Data", bundle.toString()); // Kiem tra log xem co get du lieu dc hay khong ?
        book = bundle.getParcelable("book-target");
        fetchSimilarProducts(book.getCategoryBook()); // Lấy sản phẩm tương tự

        fillData();
    }
    cardDao = new CardDao(getContext());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        txtDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDes();
            }
        });


    //handle event;
    //Tang giam so luong
        handleEventNumOfBook();
        handleAddToCart();
        //Them vao gio hang
        return view;

    }

    //  Phương thức này truy vấn cơ sở dữ liệu để lấy danh sách các sản phẩm có cùng thể loại với sản phẩm hiện tại.

    private void fetchSimilarProducts(String category){
        DatabaseReference bref = FirebaseDatabase.getInstance().getReference("Books");
        Query query = bref.orderByChild("categoryBook").equalTo(category);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                similarProductsList.clear();
                for (DataSnapshot data: snapshot.getChildren()){
                    Book book1 = data.getValue(Book.class);
                    if (book1 != null && !book1.getIdBook().equals(ShowDetailFragment.this.book.getIdBook())){
                        similarProductsList.add(book1);
                    }
                }
                similarProductsAdaper.setData(similarProductsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void handleAddToCart(){
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookId = book.getIdBook();
                if (bookId != null){
                    CartItem cartItem = new CartItem();
                    cartItem.setBookId(bookId);
                    cartItem.setNumCart(numofBook);
                    cardDao.addToCart(cartItem);
                    changeToCartFragment();
                }
            }
        });
    }
    private void ShowDes(){
       AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
       builder.setTitle(book.getTitleBook());
       builder.setMessage(book.getDescriptionBook());
       builder.setPositiveButton("Đóng",
               ((dialog, which) -> dialog.dismiss()));
       AlertDialog dialog = builder.create();
       dialog.show();

    }

    private void changeToCartFragment(){
        CartFragment cartFragment = new CartFragment();
        this.getParentFragmentManager().beginTransaction().replace(R.id.container,cartFragment)
                .addToBackStack(null)
                .commit();

    }

    private void handleEventNumOfBook(){
        btnTang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numofBook += 1;
                setNum();
            }
        });

        btnGiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numofBook > 1) {
                    numofBook -= 1;
                    setNum();
                }
            }
        });
    }

    private void fillData(){
        txtTitle.setText(book.getTitleBook());
        txtAuthor.setText(book.getAuthor());
        txtPrice.setText(FormatCurrency.formatVND(book.getPriceBook()));
        Glide.with(getContext()).load(book.getImgURLBook()).into(img);
        txtVersion.setText(book.getVersionBook());
        txtCompany.setText(book.getCompanyBook());
        txtYear.setText(Integer.toString(book.getYearBook()));
        txtCate.setText(book.getCategoryBook());
//        txtDes.setText(book.getDescriptionBook());
        setNum();
    }

    private void setNum(){
        txtNum.setText(Integer.toString(numofBook));
    }

}
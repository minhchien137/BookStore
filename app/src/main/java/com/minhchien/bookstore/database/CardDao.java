package com.minhchien.bookstore.database;

import static android.provider.SyncStateContract.Helpers.insert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.minhchien.bookstore.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CardDao extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "book_store_db";

    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "cart_table";

    private static final String KEY_ID = "idCart";

    private static final String KEY_BOOK_ID = "book_ID";

    private static final String KEY_NUM = "numCart";

    public CardDao(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCartTable = " CREATE TABLE " + TABLE_NAME
                + " (idCart INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " book_ID TEXT NOT NULL, numCart INTEGER NOT NULL);";
        db.execSQL(createCartTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // add Gio Hang

    public void addToCart(CartItem cartItem) {
        CartItem existCartItem = getCartItem(cartItem.getBookId());
        if (existCartItem == null) {
            insert(cartItem);
        } else {
            int newNum = existCartItem.getNumCart() + cartItem.getNumCart();
            existCartItem.setNumCart(newNum);
            updateCartItem(existCartItem);
        }
    }

    public void updateCartItem(CartItem cartItem) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID,cartItem.getIdCart());
        values.put(KEY_BOOK_ID,cartItem.getBookId());
        values.put(KEY_NUM,cartItem.getNumCart());
        db.update(TABLE_NAME,values,KEY_ID+" =?",new String[] {String.valueOf(cartItem.getIdCart())});
        db.close();;
    }



    public void insert(CartItem cartItem) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_ID, cartItem.getBookId());
        values.put(KEY_NUM, cartItem.getNumCart());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public CartItem getCartItem(String bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,null,KEY_BOOK_ID+" =?",new String[]{String.valueOf(bookId)},null,null,null);
        while(cursor.moveToFirst()){
            CartItem cartItem = new CartItem(cursor.getInt(0),cursor.getString(1),cursor.getInt(2));
            return cartItem;
        }
        db.close();
        return  null;
    }

    public void deleteCartItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ID+" =?",new String[]{String.valueOf(id)});
        db.close();
    }

    public List<CartItem> getAll (){

        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            CartItem cartItem = new CartItem(cursor.getInt(0),cursor.getString(1),cursor.getInt(2));
            list.add(cartItem);
            cursor.moveToNext();
        }
        return list;

    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.close();
    }

}

package com.minhchien.bookstore.model;

public class CartItem {
    private int idCart;
    private String bookId;
    private int numCart;

    public CartItem(int idCart, String bookId, int numCart) {
        this.idCart = idCart;
        this.bookId = bookId;
        this.numCart = numCart;
    }

    public int getIdCart() {
        return idCart;
    }

    public void setIdCart(int idCart) {
        this.idCart = idCart;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getNumCart() {
        return numCart;
    }

    public void setNumCart(int numCart) {
        this.numCart = numCart;
    }
}

package com.minhchien.bookstore.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private String idBook;
    private String titleBook;
    private String author;
    private String categoryBook;
    private String imgURLBook;
    private String companyBook;
    private String versionBook;
    private int yearBook;
    private int priceBook;
    private int inStockBook;
    private String descriptionBook;
    private int isActive;

    public Book(String idBook, String titleBook, String author, String categoryBook, String imgURLBook, String companyBook, String versionBook, int yearBook, int priceBook, int inStockBook, String descriptionBook, int isActive) {
        this.idBook = idBook;
        this.titleBook = titleBook;
        this.author = author;
        this.categoryBook = categoryBook;
        this.imgURLBook = imgURLBook;
        this.companyBook = companyBook;
        this.versionBook = versionBook;
        this.yearBook = yearBook;
        this.priceBook = priceBook;
        this.inStockBook = inStockBook;
        this.descriptionBook = descriptionBook;
        this.isActive = isActive;
    }

    protected Book(Parcel in) {
        idBook = in.readString();
        titleBook = in.readString();
        author = in.readString();
        categoryBook = in.readString();
        imgURLBook = in.readString();
        companyBook = in.readString();
        versionBook = in.readString();
        yearBook = in.readInt();
        priceBook = in.readInt();
        inStockBook = in.readInt();
        descriptionBook = in.readString();
        isActive = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return "Book{" +
                "id='" + idBook + '\'' +
                ", title='" + titleBook + '\'' +
                ", author='" + author + '\'' +
                ", category='" + categoryBook + '\'' +
                ", imgURL='" + imgURLBook + '\'' +
                ", company='" + companyBook + '\'' +
                ", version='" + versionBook + '\'' +
                ", year=" + yearBook +
                ", price=" + priceBook +
                ", inStock=" + inStockBook +
                ", description='" + descriptionBook + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    public String getIdBook() {
        return idBook;
    }

    public void setIdBook(String idBook) {
        this.idBook = idBook;
    }

    public String getTitleBook() {
        return titleBook;
    }

    public void setTitleBook(String titleBook) {
        this.titleBook = titleBook;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategoryBook() {
        return categoryBook;
    }

    public void setCategoryBook(String categoryBook) {
        this.categoryBook = categoryBook;
    }

    public String getImgURLBook() {
        return imgURLBook;
    }

    public void setImgURLBook(String imgURLBook) {
        this.imgURLBook = imgURLBook;
    }

    public String getCompanyBook() {
        return companyBook;
    }

    public void setCompanyBook(String companyBook) {
        this.companyBook = companyBook;
    }

    public String getVersionBook() {
        return versionBook;
    }

    public void setVersionBook(String versionBook) {
        this.versionBook = versionBook;
    }

    public int getYearBook() {
        return yearBook;
    }

    public void setYearBook(int yearBook) {
        this.yearBook = yearBook;
    }

    public int getPriceBook() {
        return priceBook;
    }

    public void setPriceBook(int priceBook) {
        this.priceBook = priceBook;
    }

    public int getInStockBook() {
        return inStockBook;
    }

    public void setInStockBook(int inStockBook) {
        this.inStockBook = inStockBook;
    }

    public String getDescriptionBook() {
        return descriptionBook;
    }

    public void setDescriptionBook(String descriptionBook) {
        this.descriptionBook = descriptionBook;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(idBook);
        parcel.writeString(titleBook);
        parcel.writeString(author);
        parcel.writeString(categoryBook);
        parcel.writeString(imgURLBook);
        parcel.writeString(companyBook);
        parcel.writeString(versionBook);
        parcel.writeInt(yearBook);
        parcel.writeInt(priceBook);
        parcel.writeInt(inStockBook);
        parcel.writeString(descriptionBook);
        parcel.writeInt(isActive);
    }
}

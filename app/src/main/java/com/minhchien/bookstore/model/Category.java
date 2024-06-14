package com.minhchien.bookstore.model;

import java.util.List;

public class Category {
    private String idCategory;
    private String Namecategory;
    private List<Book> list;

    public Category(String idCategory, String namecategory) {
        this.idCategory = idCategory;
        Namecategory = namecategory;
    }

    public Category(String idCategory, String namecategory, List<Book> list) {
        this.idCategory = idCategory;
        Namecategory = namecategory;
        this.list = list;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public String getNamecategory() {
        return Namecategory;
    }

    public void setNamecategory(String namecategory) {
        Namecategory = namecategory;
    }

    public List<Book> getList() {
        return list;
    }

    public void setList(List<Book> list) {
        this.list = list;
    }

}

package com.example.merokisanbazar.model;

import java.util.List;

public class Product {
    private String _id;
    private String ProductName;
    private String ProductLocation;
    private String ProductCategory;
    private String ProductAddedBy;
    private String ProductImage;
    private String ProductDescription;
    private int ProductPrice;
    private int ProductQuantity;
    private int ProductSold;
    private String[] likes;
    private List<Comment> comments;

    public Product( int ProductSold) {
        this.ProductSold = ProductSold;
    }

    public Product(String productImage) {
        ProductImage = productImage;
    }


    public Product(String productName, String productLocation, String productCategory, String productAddedBy, String productImage, String productDescription, int productPrice, int productQuantity) {
        ProductName = productName;
        ProductLocation = productLocation;
        ProductCategory = productCategory;
        ProductAddedBy = productAddedBy;
        ProductImage = productImage;
        ProductDescription = productDescription;
        ProductPrice = productPrice;
        ProductQuantity = productQuantity;
    }

    public Product(String productName, String productLocation, String productCategory, String productDescription, int productPrice, int productQuantity) {
        this.ProductName = productName;
        this.ProductLocation = productLocation;
        this.ProductCategory = productCategory;
        this.ProductDescription = productDescription;
        this.ProductPrice = productPrice;
        this.ProductQuantity = productQuantity;
    }


    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String[] getLikes() {
        return likes;
    }

    public void setLikes(String[] likes) {
        this.likes = likes;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductLocation() {
        return ProductLocation;
    }

    public void setProductLocation(String productLocation) {
        ProductLocation = productLocation;
    }

    public String getProductCategory() {
        return ProductCategory;
    }

    public void setProductCategory(String productCategory) {
        ProductCategory = productCategory;
    }

    public String getProductAddedBy() {
        return ProductAddedBy;
    }

    public void setProductAddedBy(String productAddedBy) {
        ProductAddedBy = productAddedBy;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public int getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(int productPrice) {
        ProductPrice = productPrice;
    }

    public int getProductQuantity() {
        return ProductQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        ProductQuantity = productQuantity;
    }

    public int getProductSold() {
        return ProductSold;
    }

    public void setProductSold(int productSold) {
        ProductSold = productSold;
    }
}

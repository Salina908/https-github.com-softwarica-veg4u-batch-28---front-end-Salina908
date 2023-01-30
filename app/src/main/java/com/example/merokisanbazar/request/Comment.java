package com.example.merokisanbazar.request;

public class Comment {
    private String text;
    private String userId;
    private String commentId;
    private String productId;

    public Comment(String text, String userId, String productId) {
        this.text = text;
        this.userId = userId;
        this.productId = productId;
    }


    public Comment(String commentId, String productId) {
        this.commentId = commentId;
        this.productId = productId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

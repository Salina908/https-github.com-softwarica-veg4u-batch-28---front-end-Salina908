package com.example.merokisanbazar.request;

public class Like {
    private String productId;
    private String userId;

    public Like(String productId, String userId) {
        this.productId = productId;
        this.userId = userId;
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

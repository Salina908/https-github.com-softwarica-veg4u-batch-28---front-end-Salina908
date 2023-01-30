package com.example.merokisanbazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merokisanbazar.adpaters.CommentAdapter;
import com.example.merokisanbazar.apis.ProductApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.model.Comment;
import com.example.merokisanbazar.model.Product;
import com.example.merokisanbazar.request.Like;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetails extends AppCompatActivity {

    private ImageView backBtn, imageView3, like, comment, closeComment;
    private TextView pName, pPrice, pLocation, pQuantity, pLikes, pComments, pDesc;
    private EditText etComment;
    private Button btnAddComment, btn_buy;

    private String pid;
    private String uid;
    private Product product;

    private Dialog popDialog;

    private RecyclerView recyclerView;

    private List<Comment> comments = new ArrayList<>();
    List<String> likes;
    private ProgressDialog mDialog;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to remove the title feature from window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //to hide the action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        //to make window fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_product_details);

        mDialog = new ProgressDialog(ProductDetails.this);

        backBtn = findViewById(R.id.backBtn);
        imageView3 = findViewById(R.id.imageViewProduct);
        pName = findViewById(R.id.pName);
        pPrice = findViewById(R.id.pPrice);
        pLocation = findViewById(R.id.pLocation);
        pQuantity = findViewById(R.id.pQuantity);
        pLikes = findViewById(R.id.pLikes);
        pComments = findViewById(R.id.pComments);
        pDesc = findViewById(R.id.pDesc);
        like = findViewById(R.id.like);
        comment = findViewById(R.id.comment);
        btn_buy = findViewById(R.id.btn_buy);

        popDialog = new Dialog(ProductDetails.this);

        comment.setOnClickListener(v -> {
            if (BackendConnection.token.length() < 7) {
                Toast.makeText(this, "Please login to comment on the product", Toast.LENGTH_LONG).show();
                return;
            }
            viewComments();
        });

        like.setOnClickListener(v -> {

            if (BackendConnection.token.length() < 7) {
                Toast.makeText(this, "Please login to like the product", Toast.LENGTH_LONG).show();
                return;
            }


            Like myLike = new Like(pid, uid);
            ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);
            Call<Product> productCall;
            if (likes.contains(uid)) {
                productCall = productApis.unLikeProduct(BackendConnection.token, myLike);
            } else {
                productCall = productApis.likeProduct(BackendConnection.token, myLike);
            }

            productCall.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    getProductDetails();
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Toast.makeText(ProductDetails.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            pid = bundle.getString("_id");
            uid = bundle.getString("uid");

            getProductDetails();
        }

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetails.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btn_buy.setOnClickListener(v -> {
            if (product.getProductSold() == 1) {
                Toast.makeText(ProductDetails.this, "Product already sold", Toast.LENGTH_SHORT).show();
                return;
            } else {
                mDialog.setMessage("Processing the request...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Product product1 = new Product(1);
                        ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);

                        Call<String> productSellCall = productApis.sellProduct(pid, product1);
                        productSellCall.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {

                                if (response.code() == 200) {
                                    mDialog.dismiss();
                                    getProductDetails();
                                    finish();
                                }


                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(ProductDetails.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, 3000);


            }

        });
    }

    private void viewComments() {
        popDialog.setContentView(R.layout.comment_layout);
        closeComment = popDialog.findViewById(R.id.closeComment);
        recyclerView = popDialog.findViewById(R.id.recyclerComment);
        etComment = popDialog.findViewById(R.id.etComment);
        btnAddComment = popDialog.findViewById(R.id.btnAddComment);

        getCommentView();

        btnAddComment.setOnClickListener(v -> {
            String commentText = etComment.getText().toString().trim();

            if (commentText.isEmpty()) {
                etComment.setError("Please enter the comment text");
                etComment.requestFocus();
                return;
            }

            ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);

            com.example.merokisanbazar.request.Comment comment = new com.example.merokisanbazar.request.Comment(commentText, uid, pid);
            Call<Product> commentCall = productApis.comment(BackendConnection.token, comment);

            commentCall.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.code() == 201) {
                        getProductDetails();
                        popDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Toast.makeText(ProductDetails.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        closeComment.setOnClickListener(v -> {
            getProductDetails();
            popDialog.dismiss();

        });
        popDialog.show();
    }

    private void getProductDetails() {
        ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);
        Call<Product> productCall = productApis.getSingleProduct(pid);
        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {

                if (response.code() == 200) {
                    product = response.body();
                    comments = response.body().getComments();
                    Picasso.get().load(BackendConnection.imagePath + product.getProductImage()).into(imageView3);
                    pName.setText(product.getProductName());
                    pPrice.setText("Rs: " + product.getProductPrice());
                    pLocation.setText(product.getProductLocation());
                    pQuantity.setText("Stock available: " + product.getProductQuantity());
                    pLikes.setText("   " + product.getLikes().length);
                    pComments.setText("   " + product.getComments().size());
                    pDesc.setText(product.getProductDescription());

                    if (product.getProductSold() == 1) {
                        btn_buy.setText("Sold");
                    }

                    likes = Arrays.asList(product.getLikes());

                    if (likes.contains(uid)) {
                        like.setImageDrawable(getDrawable(R.drawable.ic_baseline_thumb_down_alt_24));

                    } else {
                        like.setImageDrawable(getDrawable(R.drawable.ic_baseline_thumb_up_24));

                    }

                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(ProductDetails.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCommentView() {
        CommentAdapter commentAdapter = new CommentAdapter(comments, ProductDetails.this, pid, uid);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProductDetails.this);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}
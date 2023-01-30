package com.example.merokisanbazar.adpaters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merokisanbazar.ProductDetails;
import com.example.merokisanbazar.R;
import com.example.merokisanbazar.apis.UserApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.fragments.ProductDetailsFragment;
import com.example.merokisanbazar.model.Product;
import com.example.merokisanbazar.model.User;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context context;
    private List<Product> productList;
    private FragmentManager fragmentManager;

    public ProductAdapter(Context context, List<Product> productList, FragmentManager fragmentManager) {
        this.context = context;
        this.productList = productList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.product_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.cardView.setOnClickListener(v -> {


            UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
            Call<User> userCall = userApis.getBuyerUserDetails(BackendConnection.token);

            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {

                            Intent intent = new Intent(context, ProductDetails.class);
                            intent.putExtra("_id", productList.get(position).get_id());
                            intent.putExtra("uid", response.body().get_id());
                            context.startActivity(intent);

                        } else {
                            UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
                            Call<User> userCall = userApis.getUserDetails(BackendConnection.token);
                            userCall.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if (response.code() == 200) {
                                        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("_id", productList.get(position).get_id());
                                        productDetailsFragment.setArguments(bundle);
                                        FragmentTransaction ft = fragmentManager.beginTransaction();
                                        if (response.body().getAdmin()) {
                                            ft.replace(R.id.nav_host_fragment_content_admin, productDetailsFragment);

                                        } else {
                                            ft.replace(R.id.nav_host_fragment_activity_seller_dashboard, productDetailsFragment);

                                        }
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });

        holder.cardView.setOnLongClickListener(v -> {
            Toast.makeText(context, "" + productList.get(position).getProductName() + " details", Toast.LENGTH_SHORT).show();
            return false;
        });

        holder.textView.setText(productList.get(position).getProductName());
        Picasso.get().load(BackendConnection.imagePath + productList.get(position).getProductImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        CardView cardView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImage);
            textView = itemView.findViewById(R.id.productName);
            cardView = itemView.findViewById(R.id.productCardView);

        }
    }
}

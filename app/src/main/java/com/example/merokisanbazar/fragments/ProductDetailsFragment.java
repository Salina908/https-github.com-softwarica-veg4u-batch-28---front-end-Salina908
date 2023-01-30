package com.example.merokisanbazar.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merokisanbazar.R;
import com.example.merokisanbazar.apis.ProductApis;
import com.example.merokisanbazar.apis.UserApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.model.Product;
import com.example.merokisanbazar.model.User;
import com.example.merokisanbazar.ui.dashboard.DashboardFragment;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsFragment extends Fragment {

    private ImageView imageView2, imageView, imageView3;
    private TextView textView3, textView4, textView5, textView6, textView7;
    private Button button2;
    private String id;

    private User user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        imageView3 = view.findViewById(R.id.imageView3);
        imageView = view.findViewById(R.id.imageView);
        imageView2 = view.findViewById(R.id.imageView2);
        textView3 = view.findViewById(R.id.textView3);
        textView4 = view.findViewById(R.id.textView4);
        textView5 = view.findViewById(R.id.textView5);
        textView6 = view.findViewById(R.id.textView6);
        textView7 = view.findViewById(R.id.textView7);
        button2 = view.findViewById(R.id.button2);


        UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
        Call<User> userCall = userApis.getUserDetails(BackendConnection.token);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    user = response.body();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("_id");
            getProductDetails(id);
        }


        imageView2.setOnClickListener(v -> {
            EditProductFragment editProductFragment = new EditProductFragment();
            Bundle myBundle = new Bundle();
            myBundle.putString("pid", id);
            editProductFragment.setArguments(myBundle);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (user.getAdmin()) {
                ft.replace(R.id.nav_host_fragment_content_admin, editProductFragment);
            } else {
                ft.replace(R.id.nav_host_fragment_activity_seller_dashboard, editProductFragment);
            }

            ft.addToBackStack(null);
            ft.commit();
        });


        button2.setOnClickListener(v -> {
            ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);

            Call<String> deleteCall = productApis.deleteProduct(BackendConnection.token, id);
            deleteCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 200) {
                        DashboardFragment dashboardFragment = new DashboardFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        if (user.getAdmin()) {
                            ft.replace(R.id.nav_host_fragment_content_admin, dashboardFragment);
                        } else {
                            ft.replace(R.id.nav_host_fragment_activity_seller_dashboard, dashboardFragment);
                        }

                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        imageView.setOnClickListener(v -> {
            DashboardFragment dashboardFragment = new DashboardFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (user.getAdmin()) {
                ft.replace(R.id.nav_host_fragment_content_admin, dashboardFragment);
            } else {
                ft.replace(R.id.nav_host_fragment_activity_seller_dashboard, dashboardFragment);
            }
            ft.addToBackStack(null);
            ft.commit();
        });


        return view;
    }

    private void getProductDetails(String id) {
        ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);
        Call<Product> productCall = productApis.getSingleProduct(id);

        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.code() == 200) {
                    Product product = response.body();
                    Picasso.get().load(BackendConnection.imagePath + product.getProductImage()).into(imageView3);
                    textView3.setText(product.getProductName());
                    textView4.setText("Rs: " + product.getProductPrice());
                    textView5.setText(product.getProductDescription());
                    textView6.setText("Likes: " + product.getLikes().length);
                    textView7.setText("Comments: " + product.getComments().size());

                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.merokisanbazar.ui.dashboard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merokisanbazar.R;
import com.example.merokisanbazar.adpaters.ProductAdapter;
import com.example.merokisanbazar.apis.ProductApis;
import com.example.merokisanbazar.apis.UserApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.fragments.AddProductFragment;
import com.example.merokisanbazar.model.Product;
import com.example.merokisanbazar.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {
    private Button addProduct;
    private RecyclerView productRecycle;
    private ProgressDialog progressDialog;
    private AutoCompleteTextView actv;
    private ImageButton search;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        addProduct = view.findViewById(R.id.btnAddProduct);
        productRecycle = view.findViewById(R.id.recycleProduct);
        actv = view.findViewById(R.id.actv);
        search = view.findViewById(R.id.searchProduct);


        search.setOnClickListener(v -> {
            String query = actv.getText().toString().trim();
            if (query.isEmpty()) {
                if (user.getAdmin()) {
                    getAllProductsAdmin();
                } else {
                    getAllProducts();
                }

            } else {

                searchProducts(query);
            }
        });


        progressDialog = new ProgressDialog(getContext());


        UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
        Call<User> userCall = userApis.getUserDetails(BackendConnection.token);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    user = response.body();
                    if (response.body().getAdmin()) {
                        getAllProductsAdmin();
                    } else {
                        getAllProducts();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //getAllProducts();


        addProduct.setOnClickListener(v -> {
            AddProductFragment addProductFragment = new AddProductFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (user.getAdmin()) {
                ft.replace(R.id.nav_host_fragment_content_admin, addProductFragment);
            } else {
                ft.replace(R.id.nav_host_fragment_activity_seller_dashboard, addProductFragment);
            }

            ft.addToBackStack(null);
            ft.commit();
        });

        return view;
    }

    private void getAllProductsAdmin() {
        progressDialog.setMessage("Fetching your products please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);
        Call<List<Product>> productCall = productApis.getAllProducts();
        productCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {

                    List<Product> products = response.body();

                    if (products.size() == 0) {
                        Toast.makeText(getContext(), "Please add product, no product found", Toast.LENGTH_LONG).show();
                    } else {

                        List<String> name = new ArrayList<>();

                        for (int i = 0; i < products.size(); i++) {
                            name.add(products.get(i).getProductName());
                        }
                        FragmentManager fragmentManager = getFragmentManager();
                        ProductAdapter productAdapter = new ProductAdapter(getContext(), products, fragmentManager);
                        productRecycle.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        productRecycle.setAdapter(productAdapter);


                        if (getContext() != null) {
                            ArrayAdapter<String> productArrayAdapter = new ArrayAdapter<>(
                                    getContext(), android.R.layout.select_dialog_item, name
                            );
                            actv.setAdapter(productArrayAdapter);
                            actv.setThreshold(1);
                        }


                    }


                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchProducts(String query) {
        ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);
        Call<List<Product>> productCall = productApis.searchProduct(query);
        productCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {
                    List<Product> products = response.body();

                    if (products.size() == 0) {
                        Toast.makeText(getContext(), "Product " + query + " not found", Toast.LENGTH_LONG).show();
                    } else {

                        FragmentManager fragmentManager = getFragmentManager();
                        ProductAdapter productAdapter = new ProductAdapter(getContext(), products, fragmentManager);
                        productRecycle.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        productRecycle.setAdapter(productAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllProducts() {

        progressDialog.setMessage("Fetching your products please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);
        Call<List<Product>> productCall = productApis.getAllProductOfSeller(BackendConnection.token);

        productCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {

                    List<Product> products = response.body();

                    if (products.size() == 0) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Please add product, no product found", Toast.LENGTH_LONG).show();
                        }
                    } else {

                        List<String> name = new ArrayList<>();

                        for (int i = 0; i < products.size(); i++) {
                            name.add(products.get(i).getProductName());
                        }
                        FragmentManager fragmentManager = getFragmentManager();
                        ProductAdapter productAdapter = new ProductAdapter(getContext(), products, fragmentManager);
                        productRecycle.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        productRecycle.setAdapter(productAdapter);


                        if (getContext() != null) {
                            ArrayAdapter<String> productArrayAdapter = new ArrayAdapter<>(
                                    getContext(), android.R.layout.select_dialog_item, name
                            );

                            actv.setAdapter(productArrayAdapter);
                            actv.setThreshold(1);
                        }

                    }


                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
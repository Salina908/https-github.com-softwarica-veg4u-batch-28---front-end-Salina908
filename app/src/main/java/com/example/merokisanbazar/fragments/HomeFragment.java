package com.example.merokisanbazar.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.merokisanbazar.R;
import com.example.merokisanbazar.adpaters.HomeCategoryAdapter;
import com.example.merokisanbazar.adpaters.ProductAdapter;
import com.example.merokisanbazar.apis.CategoryApis;
import com.example.merokisanbazar.apis.ProductApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.model.Category;
import com.example.merokisanbazar.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private RecyclerView all_product_rec;
    private LinearLayout llAll, llFruits, llVegetables;
    private AutoCompleteTextView actv;
    private ImageButton search;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        all_product_rec = view.findViewById(R.id.all_product_rec);
        llAll = view.findViewById(R.id.llAll);
        llFruits = view.findViewById(R.id.llFruits);
        llVegetables = view.findViewById(R.id.llVegetables);
        actv = view.findViewById(R.id.actvHome);
        search = view.findViewById(R.id.searchProductHome);

        getAllProducts();

        search.setOnClickListener(v -> {
            String query = actv.getText().toString().trim();
            if (!query.isEmpty()) {
                searchProducts(query);

            }
        });

        llAll.setOnClickListener(v -> getAllProducts());

        llFruits.setOnClickListener(v -> getByCat("fruits"));

        llVegetables.setOnClickListener(v -> getByCat("vegetables"));

        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.slide1, "Vegetables and fruits at your doorsteps", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.slide2, "Fresh fruits and vegetables", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.slide3, "Discount using coupon code", ScaleTypes.CENTER_CROP));


        imageSlider.setImageList(slideModels);

        return view;
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
                        all_product_rec.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        all_product_rec.setAdapter(productAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getByCat(String cat) {
        CategoryApis categoryApis = BackendConnection.getInstance().create(CategoryApis.class);
        Call<Category> categoryCall = categoryApis.getCatByName(BackendConnection.token, cat);

        categoryCall.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.code() == 200) {
                    getAllProductByCatId(response.body().get_id());
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllProductByCatId(String id) {
        ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);
        Call<List<Product>> listCall = productApis.getProductsByCategory(id);
        listCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {
                    List<Product> products = response.body();
                    if (products.size() == 0) {
                        Toast.makeText(getContext(), "No product available", Toast.LENGTH_LONG).show();
                    } else {

                        List<String> name = new ArrayList<>();

                        for (int i = 0; i < products.size(); i++) {
                            name.add(products.get(i).getProductName());
                        }

                        ArrayAdapter<String> productArrayAdapter = new ArrayAdapter<>(
                                getContext(), android.R.layout.select_dialog_item, name
                        );

                        actv.setAdapter(productArrayAdapter);
                        actv.setThreshold(1);

                        FragmentManager fragmentManager = getFragmentManager();
                        ProductAdapter productAdapter = new ProductAdapter(getContext(), products, fragmentManager);
                        all_product_rec.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        all_product_rec.setAdapter(productAdapter);


                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllProducts() {
        ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);
        Call<List<Product>> listCall = productApis.getAllProducts();
        listCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {
                    List<Product> products = response.body();
                    if (products.size() == 0) {
                        Toast.makeText(getContext(), "No product available", Toast.LENGTH_LONG).show();
                    } else {

                        List<String> name = new ArrayList<>();

                        for (int i = 0; i < products.size(); i++) {
                            name.add(products.get(i).getProductName());
                        }
                        FragmentManager fragmentManager = getFragmentManager();
                        ProductAdapter productAdapter = new ProductAdapter(getContext(), products, fragmentManager);
                        all_product_rec.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        all_product_rec.setAdapter(productAdapter);

                        ArrayAdapter<String> productArrayAdapter = new ArrayAdapter<>(
                                getContext(), android.R.layout.select_dialog_item, name
                        );

                        actv.setAdapter(productArrayAdapter);
                        actv.setThreshold(1);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
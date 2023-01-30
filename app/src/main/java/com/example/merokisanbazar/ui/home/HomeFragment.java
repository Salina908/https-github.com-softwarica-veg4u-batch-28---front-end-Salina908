package com.example.merokisanbazar.ui.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merokisanbazar.R;
import com.example.merokisanbazar.adpaters.CategoryAdapter;
import com.example.merokisanbazar.apis.CategoryApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.model.Category;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private Spinner spinner;
    private Button button;

    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private List<Category> categories = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.seller_home_fragment, container, false);

        spinner = root.findViewById(R.id.spinnerCategory);
        button = root.findViewById(R.id.btnCategory);
        recyclerView = root.findViewById(R.id.categoriesRec);
        progressDialog = new ProgressDialog(getContext());

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);


        getAllCategories();

        button.setOnClickListener(v -> {
            progressDialog.setMessage("Adding Category please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            String category = spinner.getSelectedItem().toString();

            CategoryApis categoryApis = BackendConnection.getInstance().create(CategoryApis.class);

            Call<Category> categoryCall = categoryApis.addCategory(new Category(category), BackendConnection.token);
            categoryCall.enqueue(new Callback<Category>() {
                @Override
                public void onResponse(Call<Category> call, Response<Category> response) {
                    if (response.code() == 400) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Category " + category + " already exist..", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (response.code() == 200) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Category Added", Toast.LENGTH_SHORT).show();
                        getAllCategories();

                    }
                }

                @Override
                public void onFailure(Call<Category> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Error :" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        return root;
    }

    private void getAllCategories() {
        progressDialog.setMessage("Fetching Categories please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        CategoryApis categoryApis = BackendConnection.getInstance().create(CategoryApis.class);
        Call<List<Category>> categoryCall = categoryApis.getAllCategories();

        categoryCall.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.code() == 200) {
                    categories = response.body();
                    CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), categories);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.hasFixedSize();
                    recyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
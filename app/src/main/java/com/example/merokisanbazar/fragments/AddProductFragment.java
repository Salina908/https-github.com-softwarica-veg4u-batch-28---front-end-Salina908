package com.example.merokisanbazar.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merokisanbazar.R;
import com.example.merokisanbazar.apis.CategoryApis;
import com.example.merokisanbazar.apis.ProductApis;
import com.example.merokisanbazar.apis.UserApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.backend.StrictModeClass;
import com.example.merokisanbazar.model.Category;
import com.example.merokisanbazar.model.Product;
import com.example.merokisanbazar.model.User;
import com.example.merokisanbazar.response.ImageResponse;
import com.example.merokisanbazar.ui.dashboard.DashboardFragment;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class AddProductFragment extends Fragment {
    private TextView close;

    private ImageView add_a_product_photo;
    private EditText product_name, product_location, product_desc, product_price, product_quantity;
    private RadioButton rbFruits, rbVegetables;
    private Button btn_add_product;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private Uri mUri;
    private String imageName = "";
    private String imagePath;

    private String pId;
    private String uId;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        close = view.findViewById(R.id.closePopup);
        add_a_product_photo = view.findViewById(R.id.add_a_product_photo);
        product_name = view.findViewById(R.id.product_name);
        product_location = view.findViewById(R.id.product_location);
        product_desc = view.findViewById(R.id.product_desc);
        product_price = view.findViewById(R.id.product_price);
        product_quantity = view.findViewById(R.id.product_quantity);
        rbFruits = view.findViewById(R.id.rbFruits);
        rbVegetables = view.findViewById(R.id.rbVegetables);
        btn_add_product = view.findViewById(R.id.btn_add_product);

        getUserDetails();

        add_a_product_photo.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //permission not granted
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    //show pop up
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    //permission already granted
                    pickImageFromGallery();
                }
            } else {
                //system os is less than marshmallow
                pickImageFromGallery();
            }
        });

        close.setOnClickListener(v -> {
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


        btn_add_product.setOnClickListener(v -> {
            String name = product_name.getText().toString().trim();
            String location = product_location.getText().toString().trim();
            String desc = product_desc.getText().toString().trim();
            String price = product_price.getText().toString().trim();
            String quantity = product_quantity.getText().toString().trim();

            if (name.isEmpty()) {
                product_name.setError("Please enter product name");
                product_name.requestFocus();
                return;
            }

            if (location.isEmpty()) {
                product_location.setError("Please enter product name");
                product_location.requestFocus();
                return;
            }

            if (desc.isEmpty()) {
                product_desc.setError("Please enter product name");
                product_desc.requestFocus();
                return;
            }

            if (price.isEmpty()) {
                product_price.setError("Please enter product name");
                product_price.requestFocus();
                return;
            }

            if (quantity.isEmpty()) {
                product_quantity.setError("Please enter product name");
                product_quantity.requestFocus();
                return;
            }

            if (mUri == null) {
                Toast.makeText(getContext(), "Please select image of the product", Toast.LENGTH_LONG).show();
                return;
            }

            String category = "";
            if (rbFruits.isChecked()) {
                category = "fruits";
            } else if (rbVegetables.isChecked()) {
                category = "vegetables";

            }
            CategoryApis categoryApis = BackendConnection.getInstance().create(CategoryApis.class);
            Call<Category> categoryCall = categoryApis.getCatByName(BackendConnection.token, category);

            categoryCall.enqueue(new Callback<Category>() {
                @Override
                public void onResponse(Call<Category> call, Response<Category> response) {
                    if (response.code() == 404) {
                        Toast.makeText(getContext(), "Selected category not added", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.code() == 200) {
                        Category category1 = response.body();
                        pId = category1.get_id();

                        saveProductImage();
                        Product product = new Product(name, location, pId, uId, imageName, desc, Integer.parseInt(price), Integer.parseInt(quantity));
                        addProductDetails(product);
                    }
                }

                @Override
                public void onFailure(Call<Category> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });


        return view;
    }

    private void getUserDetails() {

        UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
        Call<User> userCall = userApis.getUserDetails(BackendConnection.token);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    user = response.body();
                    uId = user.get_id();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProductImage() {

        File file = new File(imagePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("pImage",
                file.getName(), requestBody);

        ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);

        Call<ImageResponse> imageResponseCall = productApis.uploadImage(body);

        //  synchronized call
        StrictModeClass.StrictMode();

        try {
            Response<ImageResponse> imageResponseResponse = imageResponseCall.execute();
            System.out.println(imageResponseResponse.body());
            imageName = imageResponseResponse.body().getFilename();
            Toast.makeText(getContext(), "Image inserted", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private void addProductDetails(Product product) {
        ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);

        Call<Product> productCall = productApis.addProduct(product, BackendConnection.token);

        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.code() == 201) {
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
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }


    //handle result permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    pickImageFromGallery();
                } else {
                    //permission denied
                    Toast.makeText(getContext(), "Permission denied .. !!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    //handling the result of photo from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set the image here
            mUri = data.getData();
            add_a_product_photo.setImageURI(mUri);
            imagePath = getRealImagePath();
        }
    }

    private String getRealImagePath() {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getContext(),
                mUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(colIndex);
        cursor.close();
        return result;
    }
}
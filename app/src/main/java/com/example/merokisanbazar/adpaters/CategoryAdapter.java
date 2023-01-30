package com.example.merokisanbazar.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merokisanbazar.R;
import com.example.merokisanbazar.apis.CategoryApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.model.Category;
import com.example.merokisanbazar.response.RegisterResponse;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private Context context;
    private List<Category> categories;
    private AlertDialog.Builder builder;


    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Category category = categories.get(position);
        builder = new AlertDialog.Builder(context);

        holder.textView.setText(category.getName());
        if (category.getName().equals("fruits")) {
            Picasso.get().load(R.drawable.fruits).into(holder.imageView);
        } else if (category.getName().equals("vegetables")) {
            Picasso.get().load(R.drawable.vegetables).into(holder.imageView);
        }

        holder.delete.setOnClickListener(v -> {
            builder.setMessage("Are you sure to delete??")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteCategory(category.get_id(), position);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(" Delete Confirmation ");
            alert.show();
        });
    }

    private void deleteCategory(String id, int position) {
        CategoryApis categoryApis = BackendConnection.getInstance().create(CategoryApis.class);
        Call<RegisterResponse> responseCall = categoryApis.deleteCategory(BackendConnection.token, id);

        responseCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.code() == 200) {
                    categories.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return categories.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;
        private ImageView delete;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.myImage);
            textView = itemView.findViewById(R.id.catName);
            delete = itemView.findViewById(R.id.deleteCat);
        }
    }
}

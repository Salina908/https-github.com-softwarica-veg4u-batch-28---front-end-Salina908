package com.example.merokisanbazar.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merokisanbazar.R;
import com.example.merokisanbazar.apis.ProductApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.model.Category;
import com.example.merokisanbazar.model.Comment;
import com.example.merokisanbazar.model.Product;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private List<Comment> comments;
    private Context context;
    private String productId;
    private String uid;

    public CommentAdapter(List<Comment> comments, Context context, String productId, String uid) {
        this.comments = comments;
        this.context = context;
        this.productId = productId;
        this.uid = uid;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_content, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.date.setText("" + comment.getCreated());
        holder.comment.setText(comment.getText());

        if (comment.getPostedBy().equals(uid)) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.INVISIBLE);
        }

        holder.delete.setOnClickListener(v -> {

            System.out.println("Posted By: " + comment.getPostedBy());
            System.out.println("User Id: " + uid);


            if (comment.getPostedBy().equals(uid)) {
                ProductApis productApis = BackendConnection.getInstance().create(ProductApis.class);

                com.example.merokisanbazar.request.Comment comment1 = new com.example.merokisanbazar.request.Comment(comment.get_id(), productId);
                Call<Product> unCommentCall = productApis.unComment(BackendConnection.token, comment1);
                unCommentCall.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        if (response.code() == 200) {
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            comments.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "Cannot delete the comment", Toast.LENGTH_SHORT).show();
            }


        });

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView date, comment;
        private ImageView delete;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            comment = itemView.findViewById(R.id.comment);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}

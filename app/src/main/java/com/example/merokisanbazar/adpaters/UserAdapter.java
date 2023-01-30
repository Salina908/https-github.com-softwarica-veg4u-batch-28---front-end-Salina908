package com.example.merokisanbazar.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merokisanbazar.R;
import com.example.merokisanbazar.apis.UserApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.fragments.ProductDetailsFragment;
import com.example.merokisanbazar.fragments.UserFragment;
import com.example.merokisanbazar.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    private List<User> userList;
    private AlertDialog.Builder builder;
    private FragmentManager fragmentManager;

    public UserAdapter(Context context, List<User> userList, FragmentManager fragmentManager) {
        this.context = context;
        this.userList = userList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        User user = userList.get(position);
        builder = new AlertDialog.Builder(context);

        holder.email.setText(user.getEmail());
        holder.name.setText(user.getFullname());

        if (user.getVerified()) {
            holder.verify.setText("Verified");
            holder.verify.setOnClickListener(v -> Toast.makeText(context, "Already Verified", Toast.LENGTH_SHORT).show());
        } else {
            holder.verify.setOnClickListener(v -> verifyUser(user.get_id()));
        }


        holder.delete.setOnClickListener(v -> {
            builder.setMessage("Are you sure to delete??")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteUser(user.get_id(), position);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(" Delete Confirmation ");
            alert.show();
        });
    }

    private void verifyUser(String id) {

        UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
        Call<String> verifyCall = userApis.verifyUser(BackendConnection.token, id);
        verifyCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    UserFragment userFragment = new UserFragment();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.nav_host_fragment_content_admin, userFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteUser(String id, int position) {

        UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
        Call<String> deleteCall = userApis.deleteSeller(BackendConnection.token, id);
        deleteCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    userList.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, email;
        private Button verify, delete;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            verify = itemView.findViewById(R.id.verify);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}

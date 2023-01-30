package com.example.merokisanbazar.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.merokisanbazar.R;
import com.example.merokisanbazar.adpaters.CategoryAdapter;
import com.example.merokisanbazar.adpaters.UserAdapter;
import com.example.merokisanbazar.apis.UserApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    private RecyclerView recyclerView;
    private String uid;
    private AutoCompleteTextView actv;
    private ImageView searchSeller;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        recyclerView = view.findViewById(R.id.recyclerUsers);
        actv = view.findViewById(R.id.actv);
        searchSeller = view.findViewById(R.id.searchSeller);

        getUserDetails();
        getAllUsers();

        searchSeller.setOnClickListener(v -> {
            String query = actv.getText().toString().trim();
            if (query.isEmpty()) {
                getAllUsers();
            } else {
                UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
                Call<List<User>> userSearchCall = userApis.searchUser(BackendConnection.token, query);

                userSearchCall.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        if (response.code() == 200) {

                            List<User> userList = new ArrayList<>();
                            for (int i = 0; i < response.body().size(); i++) {
                                if (!response.body().get(i).get_id().equals(uid)) {
                                    userList.add(response.body().get(i));
                                }
                            }
                            if (userList.size() == 0) {
                                Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            FragmentManager fragmentManager = getFragmentManager();
                            UserAdapter userAdapter = new UserAdapter(getActivity(), userList, fragmentManager);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.hasFixedSize();
                            recyclerView.setAdapter(userAdapter);
                            userAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
                    uid = response.body().get_id();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllUsers() {

        UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
        Call<List<User>> userCall = userApis.getAllSellerUsers(BackendConnection.token);
        userCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.code() == 200) {

                    List<User> userList = new ArrayList<>();

                    List<String> name = new ArrayList<>();

                    for (int i = 0; i < response.body().size(); i++) {
                        if (!response.body().get(i).get_id().equals(uid)) {
                            userList.add(response.body().get(i));
                            name.add(response.body().get(i).getFullname());
                        }
                    }

                    if (userList.size() == 0) {
                        Toast.makeText(getContext(), "No users found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FragmentManager fragmentManager = getFragmentManager();
                    UserAdapter userAdapter = new UserAdapter(getActivity(), userList, fragmentManager);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.hasFixedSize();
                    recyclerView.setAdapter(userAdapter);
                    userAdapter.notifyDataSetChanged();

                    if (getContext() != null) {
                        ArrayAdapter<String> productArrayAdapter = new ArrayAdapter<>(
                                getContext(), android.R.layout.select_dialog_item, name
                        );
                        actv.setAdapter(productArrayAdapter);
                        actv.setThreshold(1);

                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
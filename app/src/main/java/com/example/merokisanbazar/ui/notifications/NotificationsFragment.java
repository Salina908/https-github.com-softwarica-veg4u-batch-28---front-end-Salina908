package com.example.merokisanbazar.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.merokisanbazar.R;
import com.example.merokisanbazar.apis.UserApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private EditText name, email, phone, address;
    private Button btnUpdate;
    private String emailValidator = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private String uid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        name = root.findViewById(R.id.etName);
        email = root.findViewById(R.id.etEmail);
        phone = root.findViewById(R.id.etPhone);
        address = root.findViewById(R.id.etAddress);
        btnUpdate = root.findViewById(R.id.buttonUpdate);

        getUserProfile();


        btnUpdate.setOnClickListener(v -> {
            String userName = name.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userPhone = phone.getText().toString().trim();
            String userAddress = address.getText().toString().trim();

            if (userName.isEmpty()) {
                name.setError("Please enter full name");
                name.requestFocus();
                return;
            }

            if (!userEmail.matches(emailValidator)) {
                email.setError("Please enter valid email");
                email.requestFocus();
                return;
            }
            if (!(userPhone.length() == 10)) {
                phone.setError("Phone number should be  10 numbers");
                phone.requestFocus();
                return;
            }

            if (userAddress.isEmpty()) {
                address.setError("Please enter address");
                address.requestFocus();
                return;
            }

            User user = new User(userName, userEmail, userPhone, userAddress);
            UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
            Call<String> userUpdateCall = userApis.updateUser(BackendConnection.token, uid, user);

            userUpdateCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 201) {
                        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });
        return root;
    }

    private void getUserProfile() {
        UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
        Call<User> userCall = userApis.getUserDetails(BackendConnection.token);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    name.setText(response.body().getFullname());
                    email.setText(response.body().getEmail());
                    phone.setText("" + response.body().getPhoneNumber());
                    address.setText(response.body().getAddress());
                    uid = response.body().get_id();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
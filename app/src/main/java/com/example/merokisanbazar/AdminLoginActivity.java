package com.example.merokisanbazar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.merokisanbazar.apis.UserApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.model.User;
import com.example.merokisanbazar.response.LoginResponse;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etRegisterEmail, etRegisterPassword;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to remove the title feature from window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //to hide the action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        //to make window fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_login);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);

    }

    public void signIn(View view) {
        String userEmail = etRegisterEmail.getText().toString().trim();
        String userPassword = etRegisterPassword.getText().toString().trim();

        if (userEmail.isEmpty()) {
            etRegisterEmail.setError("Please enter email");
            etRegisterEmail.requestFocus();
            return;
        }

        if (!userEmail.matches(emailPattern)) {
            etRegisterEmail.setError("Please enter valid email");
            etRegisterEmail.requestFocus();
            return;
        }

        if (userPassword.isEmpty()) {
            etRegisterPassword.setError("Please enter password");
            etRegisterPassword.requestFocus();
            return;
        }

        if (userPassword.length() < 6) {
            etRegisterPassword.setError("Password must be at least 6 characters");
            etRegisterPassword.requestFocus();
            return;
        }

        User user = new User(userEmail, userPassword);
        UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
        Call<LoginResponse> loginResponseCall;

        loginResponseCall = userApis.checkUserAdmin(user);


        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 403) {
                    Toast.makeText(AdminLoginActivity.this, "User with email " + userEmail + " does not exist", Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.code() == 402) {
                    Toast.makeText(AdminLoginActivity.this, "Please login with admin account", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.code() == 404) {
                    Toast.makeText(AdminLoginActivity.this, "Password does not match", Toast.LENGTH_LONG).show();
                    return;
                }

                if (response.code() == 200) {
                    BackendConnection.token += response.body().getToken();
                    Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(AdminLoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void signUp(View view) {
        Intent intent = new Intent(AdminLoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
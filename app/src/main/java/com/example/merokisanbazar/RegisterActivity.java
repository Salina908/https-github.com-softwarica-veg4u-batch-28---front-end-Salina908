package com.example.merokisanbazar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merokisanbazar.adpaters.SliderAdapter;
import com.example.merokisanbazar.apis.UserApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.model.User;
import com.example.merokisanbazar.response.RegisterResponse;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, email, password, phone, address;
    private RadioButton buyer, seller;
    private Button signUp;

    private String emailValidator = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to remove the title feature from window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //to hide the action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        //to make window fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.etRegisterName);
        email = findViewById(R.id.etRegisterEmail);
        password = findViewById(R.id.etRegisterPassword);
        phone = findViewById(R.id.etRegisterPhone);
        address = findViewById(R.id.etResiterAddress);
        buyer = findViewById(R.id.rbBuyer);
        seller = findViewById(R.id.rbSeller);
        signUp = findViewById(R.id.buttonSignUp);

        name.requestFocus();

    }


    public void signIn(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    public void signUp(View view) {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
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

        if (userPassword.isEmpty()) {
            password.setError("Please enter password");
            password.requestFocus();
            return;
        }


        if (userPassword.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }

        signUp.setVisibility(View.INVISIBLE);

        User user = new User(userName, userEmail, userPassword, userPhone, userAddress);
        UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
        Call<RegisterResponse> registerResponseCall;
        if (seller.isChecked()) {
            //register seller
            registerResponseCall = userApis.registerSeller(user);

        } else {
            //register buyer
            registerResponseCall = userApis.registerBuyer(user);
        }

        registerResponseCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                if (!response.isSuccessful()) {

                    if (response.code() == 403) {
                        Toast.makeText(RegisterActivity.this, "User with email " + userEmail + " already exists", Toast.LENGTH_LONG).show();
                    }
                    return;
                }

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();

            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        signUp.setVisibility(View.VISIBLE);
    }
}
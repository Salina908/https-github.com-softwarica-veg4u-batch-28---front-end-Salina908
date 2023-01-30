package com.example.merokisanbazar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merokisanbazar.apis.UserApis;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.model.User;
import com.example.merokisanbazar.response.LoginResponse;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private SharedPreferences sharedPreferences;
    private RadioButton buyer, seller;
    private Button signIn;
    private TextView textView8;

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

        setContentView(R.layout.activity_login);

        email = findViewById(R.id.etRegisterEmail);
        password = findViewById(R.id.etRegisterPassword);
        buyer = findViewById(R.id.rbBuyer);
        seller = findViewById(R.id.rbSeller);
        signIn = findViewById(R.id.button);
        textView8 = findViewById(R.id.textView8);

        textView8.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
            startActivity(intent);
            finish();
        });

        email.requestFocus();

    }

    public void signIn(View view) {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty()) {
            email.setError("Please enter email");
            email.requestFocus();
            return;
        }

        if (!userEmail.matches(emailPattern)) {
            email.setError("Please enter valid email");
            email.requestFocus();
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


        User user = new User(userEmail, userPassword);
        UserApis userApis = BackendConnection.getInstance().create(UserApis.class);
        Call<LoginResponse> loginResponseCall;
        if (buyer.isChecked()) {
            loginResponseCall = userApis.checkUserBuyer(user);
        } else {
            loginResponseCall = userApis.checkUserSeller(user);
        }

        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 403) {
                    Toast.makeText(LoginActivity.this, "User with email " + userEmail + " does not exist", Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.code() == 402) {
                    Toast.makeText(LoginActivity.this, "User not verified", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.code() == 404) {
                    Toast.makeText(LoginActivity.this, "Password does not match", Toast.LENGTH_LONG).show();
                    return;
                }

                if (response.code() == 200) {
                    BackendConnection.token += response.body().getToken();
                    checkingFirstTime();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void checkingFirstTime() {


        sharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);

        if (buyer.isChecked()) {

            if (isFirstTime) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();


                startActivity(new Intent(LoginActivity.this, SilderActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } else {
                startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            finish();

        } else {
            startActivity(new Intent(LoginActivity.this, SellerDashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    public void signUp(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
}
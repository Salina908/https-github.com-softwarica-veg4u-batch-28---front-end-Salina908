package com.example.merokisanbazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class NumberActivity extends AppCompatActivity {
    private TextInputEditText code;
    private EditText phone;
    private Button verify;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to remove the title feature from window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //to hide the action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        //to make window fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_number);

        code = findViewById(R.id.countryCode);
        phone = findViewById(R.id.phoneNumber);
        verify = findViewById(R.id.btnVerify);
        progressBar = findViewById(R.id.progressBar);


        verify.setOnClickListener(v -> {
            String myCode = code.getText().toString().trim();
            String myPhone = phone.getText().toString().trim();

            //validation of phone number
            if (myPhone.isEmpty() || myPhone.length() < 10) {
                phone.setError("Valid number is required");
                phone.requestFocus();
                return;
            }
            if (myCode.isEmpty()) {
                code.setError("Valid country code is required");
                code.requestFocus();
                return;
            }


            verify.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            String number = myCode + myPhone;
            generateOtpAndSendToTheNumber(number);
        });
    }

    private void generateOtpAndSendToTheNumber(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,   //once the code is send user cannot send otp for 60s
                NumberActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
                        verify.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                        verify.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(NumberActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull @NotNull String verificationId, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        verify.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(NumberActivity.this, "OTP SEND", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NumberActivity.this, VerifyOtpActivity.class);
                        intent.putExtra("number", number);
                        intent.putExtra("verificationId", verificationId);
                        startActivity(intent);
                    }
                }
        );
    }
}
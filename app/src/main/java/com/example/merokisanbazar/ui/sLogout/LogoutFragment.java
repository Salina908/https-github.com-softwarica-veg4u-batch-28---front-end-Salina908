package com.example.merokisanbazar.ui.sLogout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.merokisanbazar.LoginActivity;
import com.example.merokisanbazar.R;
import com.example.merokisanbazar.backend.BackendConnection;

public class LogoutFragment extends Fragment {


    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        button = view.findViewById(R.id.btn_logout_seller);

        button.setOnClickListener(v -> {
            BackendConnection.token = "Bearer ";
            startActivity(new Intent(getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        });
        return view;
    }
}
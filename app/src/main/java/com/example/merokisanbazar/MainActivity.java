package com.example.merokisanbazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.merokisanbazar.adpaters.SellerAdapter;
import com.example.merokisanbazar.backend.BackendConnection;
import com.example.merokisanbazar.fragments.HomeFragment;
import com.example.merokisanbazar.fragments.LogoutFragment;
import com.example.merokisanbazar.fragments.ProfileFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

//    private Fragment homeFragment;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to remove the title feature from window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //to hide the action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        //to make window fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);


        tabLayout.setupWithViewPager(viewPager);
        SellerAdapter loginAdapter = new SellerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        loginAdapter.addFragment(new HomeFragment(), "Home");
        loginAdapter.addFragment(new ProfileFragment(), "Profile");
        loginAdapter.addFragment(new LogoutFragment(), "Logout");
        viewPager.setAdapter(loginAdapter);

    }
}
package com.sevenelevenapp;

import com.sevenelevenapp.HomeFragment;
import com.sevenelevenapp.ProductFragment;
import com.sevenelevenapp.DiscountFragment;
import com.sevenelevenapp.PreorderFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {

    Button buttonHome, buttonProduct, buttonDiscount, buttonPreorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonHome = findViewById(R.id.buttonHome);
        buttonProduct = findViewById(R.id.buttonProduct);
        buttonDiscount = findViewById(R.id.buttonDiscount);
        buttonPreorder = findViewById(R.id.buttonPreorder);

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFragment(new HomeFragment());
            }
        });

        buttonProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFragment(new ProductFragment());
            }
        });

        buttonDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFragment(new DiscountFragment());
            }
        });

        buttonPreorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFragment(new PreorderFragment());
            }
        });
    }

    private void displayFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
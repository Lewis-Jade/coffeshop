package com.example.coffeecafe;

import static com.example.coffeecafe.R.*;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DashBoard extends AppCompatActivity {
BottomNavigationView bottomNavigationView;

DrinksFragment drinksFragment = new DrinksFragment();
OrdersFragment ordersFragment = new OrdersFragment();
HomeFragment homeFragment = new HomeFragment();
CartFragment cartFragment = new CartFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);

        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender,R.color.gender,false);

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);

        // Check for navigation intent
        String navigateTo = getIntent().getStringExtra("navigate_to");
        if ("orders".equals(navigateTo)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,ordersFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.orders_tab);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,drinksFragment).commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {

                if(item.getItemId() == id.drinks_tab){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,drinksFragment).commit();
                    return true;
                } else if (item.getItemId() == id.orders_tab) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,ordersFragment).commit();
                    return true;
                } else if (item.getItemId() == id.home_tab) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,homeFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.cart_tab) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,cartFragment).commit();
                    return true;
                }else {
                    return false;
                }

            }
        });

    }
}
package com.example.coffeecafe;

import static com.example.coffeecafe.R.*;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DashBoard extends AppCompatActivity {
BottomNavigationView bottomNavigationView;


DrinksFragment drinksFragment = new DrinksFragment();
OrdersFragment ordersFragment = new OrdersFragment();
HomeFragment homeFragment = new HomeFragment();
private CartViewModel cartViewModel;
private ImageView iconImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);

        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender,R.color.gender,false);

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);


        getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,drinksFragment).commit();

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
                }else {
                    return false;
                }

            }
        });
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        cartViewModel.getCartItems().observe(this, items -> {
            BadgeDrawable badge =
                    bottomNavigationView.getOrCreateBadge(R.id.orders_tab);

            int count = items.size();

            if (count > 0) {
                badge.setVisible(true);
                badge.setNumber(count);
            } else {
                badge.setVisible(false);
            }

            badge.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.badge_bg)
            );
            badge.setBadgeTextColor(
                    ContextCompat.getColor(this, R.color.white)
            );
        });


        /////Burger menu popup.

        iconImage = findViewById(id.iv_menu);

       iconImage.setOnClickListener(view ->{
           HamburgerMenu.showBurgerPopMenu(this,view);

       });


    }
}
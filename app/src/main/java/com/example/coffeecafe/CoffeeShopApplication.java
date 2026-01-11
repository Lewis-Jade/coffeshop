package com.example.coffeecafe;

import android.app.Application;
import com.example.coffeecafe.config.SupabaseClient;
import com.example.coffeecafe.utils.Constants;

public class CoffeeShopApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize configuration
        Constants.initialize(this);
        
        // Initialize Supabase client with credentials
        SupabaseClient.getInstance().initialize(this);
    }
}

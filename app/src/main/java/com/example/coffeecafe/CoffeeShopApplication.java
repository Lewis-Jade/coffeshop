package com.example.coffeecafe;

import android.app.Application;
import com.example.coffeecafe.config.SupabaseClient;

public class CoffeeShopApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Supabase client
        SupabaseClient.getInstance().initialize();
    }
}

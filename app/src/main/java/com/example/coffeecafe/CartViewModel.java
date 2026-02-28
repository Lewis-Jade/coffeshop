package com.example.coffeecafe;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartViewModel extends AndroidViewModel {

    private final MutableLiveData<ArrayList<CartItem>> cartItems =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<ArrayList<CartItem>> purchasedItems =
            new MutableLiveData<>(new ArrayList<>());

    private static final String PREFS_NAME = "coffeecafe_prefs";
    private static final String KEY_PURCHASED = "purchased_items";
    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public CartViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadPurchasedItems();
    }

    private void loadPurchasedItems() {
        String json = sharedPreferences.getString(KEY_PURCHASED, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
            ArrayList<CartItem> items = gson.fromJson(json, type);
            purchasedItems.setValue(items != null ? items : new ArrayList<>());
        }
    }

    private void savePurchasedItems(ArrayList<CartItem> items) {
        String json = gson.toJson(items);
        sharedPreferences.edit().putString(KEY_PURCHASED, json).apply();
    }

    public void addToCart(DrinksModel drink, int quantity) {
        ArrayList<CartItem> list = cartItems.getValue();
        if (list == null) list = new ArrayList<>();

        for (CartItem item : list) {
            if (item.getName().equals(drink.getName())) {
                list.remove(item);
                break;
            }
        }

        list.add(new CartItem(drink, quantity));
        cartItems.setValue(list);
    }

    public LiveData<ArrayList<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<ArrayList<CartItem>> getPurchasedItems() {
        return purchasedItems;
    }

    public void moveToPurchased() {
        ArrayList<CartItem> currentCart = cartItems.getValue();
        if (currentCart != null && !currentCart.isEmpty()) {
            ArrayList<CartItem> currentPurchased = purchasedItems.getValue();
            if (currentPurchased == null) currentPurchased = new ArrayList<>();
            
            currentPurchased.addAll(0, new ArrayList<>(currentCart)); // Add at top
            purchasedItems.setValue(currentPurchased);
            savePurchasedItems(currentPurchased);
            
            currentCart.clear();
            cartItems.setValue(currentCart);
        }
    }

    public int getGrandTotal() {
        int total = 0;
        ArrayList<CartItem> items = cartItems.getValue();
        if (items != null) {
            for (CartItem item : items) {
                total += item.getTotalPrice();
            }
        }
        return total;
    }
}
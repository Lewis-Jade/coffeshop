package com.example.coffeecafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.coffeecafe.models.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;
    private List<CartItem> cartItems;

    private CartManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadCart();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    private void loadCart() {
        String cartJson = prefs.getString(Constants.PREF_CART_ITEMS, null);
        if (cartJson != null) {
            Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
            cartItems = gson.fromJson(cartJson, type);
        } else {
            cartItems = new ArrayList<>();
        }
    }

    private void saveCart() {
        String cartJson = gson.toJson(cartItems);
        prefs.edit().putString(Constants.PREF_CART_ITEMS, cartJson).apply();
    }

    public void addToCart(CartItem item) {
        // Check if item already exists
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId().equals(item.getProductId())) {
                // Update quantity
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                saveCart();
                return;
            }
        }
        // Add new item
        cartItems.add(item);
        saveCart();
    }

    public void updateCartItem(String productId, int newQuantity) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId().equals(productId)) {
                if (newQuantity <= 0) {
                    removeFromCart(productId);
                } else {
                    cartItem.setQuantity(newQuantity);
                    saveCart();
                }
                return;
            }
        }
    }

    public void removeFromCart(String productId) {
        cartItems.removeIf(item -> item.getProductId().equals(productId));
        saveCart();
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public int getCartItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public double getCartTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
        saveCart();
    }

    public boolean isInCart(String productId) {
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                return true;
            }
        }
        return false;
    }

    public CartItem getCartItem(String productId) {
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                return item;
            }
        }
        return null;
    }
}

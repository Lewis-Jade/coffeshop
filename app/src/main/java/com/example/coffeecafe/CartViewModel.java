package com.example.coffeecafe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class CartViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<CartItem>> cartItems =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<ArrayList<CartItem>> purchasedItems =
            new MutableLiveData<>(new ArrayList<>());

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
            
            currentPurchased.addAll(new ArrayList<>(currentCart));
            purchasedItems.setValue(currentPurchased);
            
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
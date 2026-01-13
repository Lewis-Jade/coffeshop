package com.example.coffeecafe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
public class CartViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<CartItem>> cartItems =
            new MutableLiveData<>(new ArrayList<>());

    public void addToCart(DrinksModel drink, int quantity) {
        ArrayList<CartItem> list = cartItems.getValue();

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

    public int getGrandTotal() {
        int total = 0;
        for (CartItem item : cartItems.getValue()) {
            total += item.getTotalPrice();
        }
        return total;
    }
}


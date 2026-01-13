package com.example.coffeecafe;

import java.io.Serializable;

public class CartItem implements Serializable {

    private DrinksModel drink;
    private int quantity;

    public CartItem(DrinksModel drink, int quantity) {
        this.drink = drink;
        this.quantity = quantity;
    }

    public DrinksModel getDrink() {
        return drink;
    }

    public String getName() {
        return drink.getName();
    }

    public int getPrice() {
        return drink.getPrice();
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTotalPrice() {
        return drink.getPrice() * quantity;
    }
}

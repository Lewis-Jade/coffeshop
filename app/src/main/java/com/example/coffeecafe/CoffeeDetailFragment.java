package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class CoffeeDetailFragment extends Fragment {
     private ImageView coffeeImage;
     private TextView  coffeeName,coffeeDescription,coffeePrice,tvQuantity;

     private Button addToCartButton;
     private ImageButton btnPlus, btnMinus;
     private DrinksModel drinksModel;
     private int quantity = 1;
     private int price;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coffee_detail, container, false);

        // bind views
        coffeeImage = view.findViewById(R.id.coffeeImage);
        coffeeName = view.findViewById(R.id.coffeeName);
        coffeeDescription = view.findViewById(R.id.coffeeDescription);
        coffeePrice = view.findViewById(R.id.coffeePrice);
        btnPlus = view.findViewById(R.id.btnPlus);
        btnMinus = view.findViewById(R.id.btnMinus);
        addToCartButton = view.findViewById(R.id.addToCartButton);
        tvQuantity = view.findViewById(R.id.tvQuantity);


        // Get the coffee from bundle
        Bundle args = getArguments();
        if (args != null) {
            drinksModel = (DrinksModel) args.getSerializable("coffee");
        }

        if (drinksModel == null) {
            Toast.makeText(getContext(), "No coffee data!", Toast.LENGTH_SHORT).show();
            return view;
        }
        price = drinksModel.getPrice();
        //  Display coffee data
        coffeeImage.setImageResource(drinksModel.getImage());
        coffeeName.setText(drinksModel.getName());
        coffeeDescription.setText(drinksModel.getDescription()); // add description to Drinks if needed
        coffeePrice.setText("Ksh " + drinksModel.getPrice());

         //Plus button
        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            coffeePrice.setText("Ksh " + (price * quantity));
        });
        // Minus button
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                coffeePrice.setText("Ksh " + (price * quantity));
            }
        });

        addToCartButton.setOnClickListener(v -> {
            // Add to cart using CartManager
            com.example.coffeecafe.models.CartItem cartItem = new com.example.coffeecafe.models.CartItem();
            cartItem.setProductId(String.valueOf(drinksModel.getImage())); // Using image resource as temp ID
            cartItem.setProductName(drinksModel.getName());
            cartItem.setProductDescription(drinksModel.getDescription());
            cartItem.setProductPrice(drinksModel.getPrice());
            cartItem.setQuantity(quantity);
            cartItem.setLocalImageResource(drinksModel.getImage());
            
            com.example.coffeecafe.utils.CartManager.getInstance(requireContext()).addToCart(cartItem);
            
            Toast.makeText(getContext(),
                    quantity + " x " + drinksModel.getName() + " added to cart!",
                    Toast.LENGTH_SHORT).show();
            
            // Navigate back to drinks list
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }
}
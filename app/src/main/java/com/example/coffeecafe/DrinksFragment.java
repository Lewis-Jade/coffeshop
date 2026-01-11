package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.zip.Inflater;


public class DrinksFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<DrinksModel> itemList;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drinks,container,false);

        recyclerView = view.findViewById(R.id.drinks_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemList = new ArrayList<>();

        itemList.add(new DrinksModel("Latte",10,"Smooth milk coffee",R.drawable.coffee_withmilk));
         itemList.add(new DrinksModel("Cappuccino",20,"Strong and creamy",R.drawable.white_cup_hot_chocolate));
         itemList.add(new DrinksModel("Espresso",30,"Bold and pure",R.drawable.espresso_coffee));

         DrinksAdapter drinksAdapter = new DrinksAdapter( requireActivity(),itemList);
         recyclerView.setAdapter(drinksAdapter);
        return view;
    }
}
package com.example.coffeecafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HamburgerMenu {


//////show burger menu pop up
public static void showBurgerPopMenu(Context context, View anchor) {
    PopupMenu popupMenu = new PopupMenu(context, anchor);
    popupMenu.getMenuInflater().inflate(R.menu.header_menu, popupMenu.getMenu());

    // Force icons to show
    try {
        Field[] fields = popupMenu.getClass().getDeclaredFields();
        for (Field field : fields) {
            if ("mPopup".equals(field.getName())) {
                field.setAccessible(true);
                Object menuPopupHelper = field.get(popupMenu);
                Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                setForceIcons.invoke(menuPopupHelper, true);
                break;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    popupMenu.setOnMenuItemClickListener(item -> {
        if (item.getItemId() == R.id.logout) {
            showLogoutConfirmation(context);
            return true;
        } else if (item.getItemId() == R.id.exit) {
            // Exit the app entirely
            ((Activity) context).finishAffinity();
            return true;
        }
        return false;
    });

    popupMenu.show();
}

    // Show logout confirmation dialog
    private static void showLogoutConfirmation(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> performUserLogout(context))
                .setNegativeButton("Cancel", null)
                .show();
    }


    /////Perform user logout

    public static void performUserLogout(Context context){


        FirebaseAuth.getInstance().signOut();

        SharedPreferences preferences = context.getSharedPreferences("firebaseAuth",Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
        Intent intent = new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }


}

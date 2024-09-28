package com.example.smalltalk.utils;

import android.app.AlertDialog;
import android.content.Context;

public class GeneralUtils {
    private GeneralUtils() {
    }

    public static void showAlert(String title, String message, Context context) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }
}

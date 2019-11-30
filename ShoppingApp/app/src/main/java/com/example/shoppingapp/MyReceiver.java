package com.example.shoppingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    private static final String PRODUCT_ADDED = BuildConfig.APPLICATION_ID + ".PRODUCT_ADDED";

    public MyReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction != null) {
            String toastMessage = "unknown intent action";
            switch (intentAction){
                case PRODUCT_ADDED:
                    toastMessage = intent.getStringExtra("string");
                    break;
            }

            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }
}

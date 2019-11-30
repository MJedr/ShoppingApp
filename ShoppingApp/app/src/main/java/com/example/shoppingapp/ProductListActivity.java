package com.example.shoppingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.example.shoppingapp.db.Item;
import com.example.shoppingapp.db.ItemHelper;

import java.util.ArrayList;


public class ProductListActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ItemHelper mHelper;
    private ListView mItemListView;
    private GroceryItemArrayAdapter mAdapter;
    private Button button;
    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private BroadcastReceiver myReceiver = new MyReceiver();
    private static final String ACTION_SHOW_TOAST =
            BuildConfig.APPLICATION_ID + ".ACTION_SHOW_TOAST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(myReceiver,
                        new IntentFilter(ACTION_SHOW_TOAST));
        String appTheme = mSharedPreferences.getString("theme_prefer", "AppTheme");
        if (appTheme.equals("AppTheme")) {
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeCustom);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_product_list);

    RelativeLayout rl = findViewById(R.id.activity_prod_list_view);
    String backgroundColor = mSharedPreferences.getString(getString(R.string.background_color), "#FFFFFF");
        rl.setBackgroundColor(Color.parseColor(backgroundColor));

    mHelper = new ItemHelper(this);
    mItemListView = findViewById(R.id.list_to_buy);
    button = findViewById(R.id.add_to_list_btn);
        button.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        showDialog(ProductListActivity.this);
    }
    });

    updateUI();

}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        final ArrayList<GroceryItem> groceryItemList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(Item.ItemEntry.TABLE,
                new String[]{Item.ItemEntry._ID, Item.ItemEntry.COL_ITEM_NAME,
                        Item.ItemEntry.COL_AMOUNT, Item.ItemEntry.COL_UNIT_PRICE,
                        Item.ItemEntry.COL_UNITS, Item.ItemEntry.COL_CURRENCY},
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(Item.ItemEntry._ID);
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String amount = cursor.getString(cursor.getColumnIndex("amount"));
            String unit_price = cursor.getString(cursor.getColumnIndex("unit_price"));
            String units = cursor.getString(cursor.getColumnIndex("units"));
            String currency = cursor.getString(cursor.getColumnIndex("currency"));
            groceryItemList.add(new GroceryItem(name, amount, unit_price, units, currency));
//                itemList.add(cursor.getString(index));

        }

        if (mAdapter == null) {
            if(groceryItemList.isEmpty()){

            }
            else{
                mAdapter = new GroceryItemArrayAdapter(this,groceryItemList);
                mItemListView.setAdapter(mAdapter);
            }
        }
        else {
            mAdapter.clear();
            mAdapter.addAll(groceryItemList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    public void deleteItem(View view){
        View parent = (View) view.getParent();
        TextView itemTextView = (TextView) parent.findViewById(R.id.item_title);
        String item = String.valueOf(itemTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(Item.ItemEntry.TABLE, Item.ItemEntry.COL_ITEM_NAME + " = ?", new String[] {item});
        db.close();
        updateUI();
    }

    public void addItem(View view){
        View parent = (View) view.getParent();
        TextView itemTextView = parent.findViewById(R.id.item_title);
        String item = String.valueOf(itemTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT amount FROM items WHERE name = ?",
                new String[]{item});
        ContentValues newValues = new ContentValues();
        if (res.moveToFirst()) {
            Double val_to_change = Double.valueOf(res.getString(res.getColumnIndex("amount")));
            Double newValue = val_to_change + 1;
            String newValueToPut = Double.toString(newValue);
            newValues.put(Item.ItemEntry.COL_AMOUNT, newValueToPut);
        }
        db.update(Item.ItemEntry.TABLE, newValues, Item.ItemEntry.COL_ITEM_NAME + " =?", new String[]{item});
        db.close();
        updateUI();
    }

    public void removeItem(View view){
        View parent = (View) view.getParent();
        TextView itemTextView = parent.findViewById(R.id.item_title);
        String item = String.valueOf(itemTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT amount FROM items WHERE name = ?",
                new String[]{item});
        ContentValues newValues = new ContentValues();
        if (res.moveToFirst()) {
            Double val_to_change = Double.valueOf(res.getString(res.getColumnIndex("amount")));
            if (val_to_change >1){
            Double newValue = val_to_change - 1;
            String newValueToPut = Double.toString(newValue);
            newValues.put(Item.ItemEntry.COL_AMOUNT, newValueToPut);
            db.update(Item.ItemEntry.TABLE, newValues, Item.ItemEntry.COL_ITEM_NAME + " =?", new String[]{item});}
            else{
                db.delete(Item.ItemEntry.TABLE, Item.ItemEntry.COL_ITEM_NAME + " = ?", new String[] {item});
            }
        }
        db.close();
        updateUI();
    }


    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_item_dialog);

        final EditText itemInput = dialog.findViewById(R.id.item_name);
        final EditText amountInput = dialog.findViewById(R.id.amount);
        final EditText unitPriceInput = dialog.findViewById(R.id.unit_price);
        final EditText unitsInput = dialog.findViewById(R.id.units);
        final EditText currencyInput = dialog.findViewById(R.id.currency);

        Button btnok = dialog.findViewById(R.id.btnok);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = itemInput.getText().toString();
                String amount = amountInput.getText().toString();
                String unitPrice = unitPriceInput.getText().toString();
                String units = unitsInput.getText().toString();
                String currency = currencyInput.getText().toString();

                SQLiteDatabase db = mHelper.getWritableDatabase();
//                mHelper.onUpgrade(db, 5, 6);

                ContentValues values = new ContentValues();
                values.put(Item.ItemEntry.COL_ITEM_NAME, item);
                values.put(Item.ItemEntry.COL_AMOUNT, amount);
                values.put(Item.ItemEntry.COL_UNIT_PRICE, unitPrice);
                values.put(Item.ItemEntry.COL_UNITS, units);
                values.put(Item.ItemEntry.COL_CURRENCY, currency);
                db.insertWithOnConflict(Item.ItemEntry.TABLE,
                        null, values, SQLiteDatabase.CONFLICT_REPLACE);
                db.close();

                dialog.dismiss();
                String string = "Dodano " + item + " (" + amount + " " + units + ") do listy";
                registerReceiver(myReceiver,new IntentFilter("com.example.shoppingapp.PRODUCT_ADDED"));
                Intent intent = new Intent("com.example.shoppingapp.PRODUCT_ADDED");
                intent.putExtra("string", string);
                sendBroadcast(intent);

                updateUI();
            }
        });

        Button btncn = (Button) dialog.findViewById(R.id.btncn);
        btncn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart(){
        super.onStart();
        registerReceiver(myReceiver, new IntentFilter("com.example.shoppingapp.PRODUCT_ADDED"));
        Log.i("ReceiverTest", "zarejestrowano odbiorcę");
    }

    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(myReceiver);
        Log.i("ReceiverTest", "wyrejestrowano odbiorcę");
    }

    public void sendBroadcast(View v) {
        Intent customBroadcastIntent = new Intent(ACTION_SHOW_TOAST);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(customBroadcastIntent);
        Log.i("ReceiverTest", "dodano produkt");
    }

}

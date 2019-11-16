package com.example.shoppingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shoppingapp.db.Item;
import com.example.shoppingapp.db.ItemHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";
    private ItemHelper mHelper;
    private ListView mItemListView;
    private GroceryItemArrayAdapter mAdapter;
    private Button button;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.activity_main);
        mContext = getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String backgroundColor = mSharedPreferences.getString(getString(R.string.background_color),"#FFFFFF");
        rl.setBackgroundColor(Color.parseColor(backgroundColor));


        mHelper = new ItemHelper(this);
        mItemListView = (ListView) findViewById(R.id.list_to_buy);

        button = (Button) findViewById(R.id.add_to_list_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(MainActivity.this);
            }
        });
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setColor(boolean background) {
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.activity_main);
        if (background == true) {
            rl.setBackgroundColor(Color.WHITE);
        } else {
            rl.setBackgroundColor(Color.YELLOW);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("background_color")) {
            setColor(sharedPreferences.getBoolean("background_color",true));
        }
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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
            GroceryItem groceryItem;
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

        Button btnok = (Button) dialog.findViewById(R.id.btnok);

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
}

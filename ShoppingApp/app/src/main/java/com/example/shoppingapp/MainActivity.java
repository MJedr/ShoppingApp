package com.example.shoppingapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.shoppingapp.db.Item;
import com.example.shoppingapp.db.ItemHelper;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ItemHelper mHelper;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelper = new ItemHelper(this);
        mContext = getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String appTheme = mSharedPreferences.getString("theme_prefer", "AppTheme");
        if (appTheme.equals("AppTheme")) {
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppThemeCustom);
        }
        setContentView(R.layout.activity_main);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_main);
        String backgroundColor = mSharedPreferences.getString(getString(R.string.background_color), "#FFFFFF");
        rl.setBackgroundColor(Color.parseColor(backgroundColor));

        TextView nb_of_items_view = (TextView) findViewById(R.id.nb_of_it);
        TextView descr2 = (TextView) findViewById(R.id.nb_of_items_descr2);
        Long nb_of_items = queryNumberItems();
        String info_nb_of_items;
        if (nb_of_items == 0) {
            info_nb_of_items = "Hello!\n At your list there are no items to buy!";
        } else if (nb_of_items == 1) {
            info_nb_of_items = "item to buy";
        } else {
            info_nb_of_items = "items to buy";
        }

        descr2.setText(info_nb_of_items);
        nb_of_items_view.setText(Long.toString(nb_of_items));
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
        }else if (id == R.id.create_list) {
                Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
                startActivity(intent);
                return true;
            }
        return super.onOptionsItemSelected(item);
    }

    public long queryNumberItems(){
        SQLiteDatabase db = mHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, Item.ItemEntry.TABLE);
        db.close();
        return count;
    }

}

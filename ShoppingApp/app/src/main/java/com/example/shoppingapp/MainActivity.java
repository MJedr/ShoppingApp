package com.example.shoppingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shoppingapp.db.Item;
import com.example.shoppingapp.db.ItemHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ItemHelper mHelper;
    private ListView mItemListView;
    private ArrayAdapter<String> mAdapter;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        switch (item.getItemId()) {
            case R.id.action_add_item:
                final EditText itemEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("New item")
                        .setMessage("Add a new item: ")
                        .setView(itemEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String item = String.valueOf(itemEditText.getText());
                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(Item.ItemEntry.COL_ITEM_NAME, item);
                                db.insertWithOnConflict(Item.ItemEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUI();
                            }
                        })
                        .setNegativeButton("Cancel", null).create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI(){
            ArrayList<String> itemList = new ArrayList<>();
            SQLiteDatabase db = mHelper.getReadableDatabase();
            Cursor cursor = db.query(Item.ItemEntry.TABLE,
                    new String[] {Item.ItemEntry._ID, Item.ItemEntry.COL_ITEM_NAME},
                    null, null, null, null, null, null);

            while(cursor.moveToNext()){
                int index = cursor.getColumnIndex(Item.ItemEntry.COL_ITEM_NAME);
                itemList.add(cursor.getString(index));

            }
            if(mAdapter == null){
                mAdapter = new ArrayAdapter<>(this, R.layout.item_toadd, R.id.item_title, itemList);
                mItemListView.setAdapter(mAdapter);
            }
            else {
                mAdapter.clear();
                mAdapter.addAll(itemList);
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

    public void openDialog() {
        ItemDialog exampleDialog = new ItemDialog();
        exampleDialog.show(getSupportFragmentManager(), "dialog");
        EditText itemEditText = (EditText) findViewById(R.id.item_name);
        try{
        String strValue = itemEditText.getText().toString();
        String item = String.valueOf(itemEditText.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Item.ItemEntry.COL_ITEM_NAME, item);
        db.insertWithOnConflict(Item.ItemEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();}
        catch(Exception e){
            System.out.println("No element!!!");
        }
    }

    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_item_dialog);

        final EditText itemInput = dialog.findViewById(R.id.item_name);
        final EditText amountInput = dialog.findViewById(R.id.amount);
        final EditText unitPriceInput = dialog.findViewById(R.id.unit_price);

        Button btnok = (Button) dialog.findViewById(R.id.btnok);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = itemInput.getText().toString();
                String _amount = amountInput.getText().toString();
                int amount = Integer.parseInt(_amount);
                String _unitPrice = unitPriceInput.getText().toString();
                int unitPrice = Integer.parseInt(_unitPrice);
                SQLiteDatabase db = mHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Item.ItemEntry.COL_ITEM_NAME, item);
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

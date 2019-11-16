package com.example.shoppingapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemHelper extends SQLiteOpenHelper{

    public ItemHelper(Context context){
        super(context, Item.DB_NAME, null, Item.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + Item.ItemEntry.TABLE + " (" +
                Item.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Item.ItemEntry.COL_ITEM_NAME + " TEXT NOT NULL, " +
                Item.ItemEntry.COL_AMOUNT + " TEXT, " +
                Item.ItemEntry.COL_UNIT_PRICE + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
       db.execSQL("DROP TABLE IF EXISTS " + Item.ItemEntry.TABLE);
       onCreate(db);
    }
}

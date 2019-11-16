package com.example.shoppingapp.db;
import android.provider.BaseColumns;


public class Item {

    public static final String DB_NAME = " com.example.shoppingapp.db";
    public static final int DB_VERSION = 1;


    public class ItemEntry implements BaseColumns{
        public static final String TABLE = "items";
        public static final String COL_ITEM_NAME = "name";
        public static final String COL_AMOUNT = "amount";
        public static final String COL_UNIT_PRICE = "unit_price";

    }

}

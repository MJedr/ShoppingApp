package com.example.shoppingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class GroceryItemArrayAdapter extends ArrayAdapter<GroceryItem> {

    private Context mContext;
    private List<GroceryItem> itemsList = new ArrayList<>();

    public GroceryItemArrayAdapter(@NonNull Context context, ArrayList<GroceryItem> list) {
        super(context, 0 , list);
        mContext = context;
        itemsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_toadd,parent,false);

        GroceryItem item = itemsList.get(position);

        TextView name = (TextView)listItem.findViewById(R.id.item_title);
        TextView amount = (TextView)listItem.findViewById(R.id.item_amount);
        TextView unit_price = (TextView)listItem.findViewById(R.id.item_price);

        name.setText(item.name);
        amount.setText(item.amount);
        unit_price.setText(item.unit_price);

        return listItem;
    }
}
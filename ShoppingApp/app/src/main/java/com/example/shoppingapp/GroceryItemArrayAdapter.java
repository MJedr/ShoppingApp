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

import org.w3c.dom.Text;

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
        TextView units = (TextView)listItem.findViewById(R.id.units);
        TextView currency = (TextView)listItem.findViewById(R.id.currency);

        String amount_text = item.amount + " " + item.units;
        String price_text = item.unit_price + " " + item.currency + "/per unit";

        name.setText(item.name);
        amount.setText(amount_text);
        unit_price.setText(price_text);


        return listItem;
    }
}
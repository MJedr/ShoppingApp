package com.example.shoppingapp;


public class GroceryItem {
    String name;
    String amount;
    String unit_price;
    String units;
    String currency;


    public GroceryItem(String name, String amount, String unit_price,
                       String units, String currency) {
        this.name = name;
        this.amount = amount;
        this.unit_price = unit_price;
        this.units = units;
        this.currency = currency;
    }
}
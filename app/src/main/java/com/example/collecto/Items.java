package com.example.collecto;

import java.io.Serializable;
import java.util.ArrayList;

public class Items {
    //Array List of Collection of Items
    private ArrayList<Item> lsItems = new ArrayList<Item>();

    public ArrayList<Item> GetItems() {
        return lsItems;
    }

    public void AddItem(Item new_item) {
        lsItems.add(new_item);
    }
}

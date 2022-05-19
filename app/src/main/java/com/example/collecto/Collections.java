package com.example.collecto;

import java.io.Serializable;
import java.util.ArrayList;

public class Collections {
    private ArrayList<Collection> lsCollections = new ArrayList<Collection>();

    public ArrayList<Collection> GetCollections() {
        return lsCollections;
    }

    public void AddCollection(Collection new_collection) {
        lsCollections.add(new_collection);
    }

    public void ClearList() {
        lsCollections =  new ArrayList<Collection>();
    }
}

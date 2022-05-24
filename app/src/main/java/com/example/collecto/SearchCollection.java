package com.example.collecto;

import android.widget.Filter;

import java.util.ArrayList;

public class SearchCollection extends Filter {
    ArrayList<ModelCollection> alCollections;
    AdapterCollection adapCollection;

    public SearchCollection(ArrayList<ModelCollection> alCollections, AdapterCollection adapCollection) {
        this.alCollections = alCollections;
        this.adapCollection = adapCollection;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults fResults = new FilterResults();

        if (charSequence != null && charSequence.length() > 0) {
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelCollection> filteredModels = new ArrayList<>();

            for (int i = 0; i < alCollections.size(); i++) {
                if (alCollections.get(i).getName().toUpperCase().contains(charSequence)) {
                    filteredModels.add(alCollections.get(i));
                }

                fResults.count = filteredModels.size();
                fResults.values = filteredModels;
            }
        } else {
            fResults.count = alCollections.size();
            fResults.values = alCollections;
        }


        return fResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapCollection.collArrayList = (ArrayList<ModelCollection>)filterResults.values;

        adapCollection.notifyDataSetChanged();
    }
}

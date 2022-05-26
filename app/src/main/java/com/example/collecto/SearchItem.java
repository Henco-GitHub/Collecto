package com.example.collecto;

import android.widget.Filter;

import java.util.ArrayList;

public class SearchItem extends Filter {
    ArrayList<ModelItem> alItems;
    AdapterItem adapItem;

    public SearchItem(ArrayList<ModelItem> alItems, AdapterItem adapItem) {
        this.alItems = alItems;
        this.adapItem = adapItem;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults fResults = new FilterResults();

        if (charSequence != null && charSequence.length() > 0) {
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelItem> filteredModels = new ArrayList<>();

            for (int i = 0; i < alItems.size(); i++) {
                if (alItems.get(i).getName().toUpperCase().contains(charSequence)) {
                    filteredModels.add(alItems.get(i));
                }

                fResults.count = filteredModels.size();
                fResults.values = filteredModels;
            }
        } else {
            fResults.count = alItems.size();
            fResults.values = alItems;
        }


        return fResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapItem.itemArrayList = (ArrayList<ModelItem>)filterResults.values;

        adapItem.notifyDataSetChanged();
    }
}

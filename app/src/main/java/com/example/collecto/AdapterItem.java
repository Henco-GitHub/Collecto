package com.example.collecto;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collecto.databinding.RowCollectionBinding;
import com.example.collecto.databinding.RowItemBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AdapterItem extends RecyclerView.Adapter<AdapterItem.HolderCollection> implements Filterable {
    private Context context;
    public ArrayList<ModelItem> itemArrayList, itemFiltered;

    private RowItemBinding binding;

    private SearchItem search;

    public AdapterItem(Context context, ArrayList<ModelItem> itemArrayList) {
        this.context = context;
        this.itemArrayList = itemArrayList;
        this.itemFiltered = itemFiltered;
    }


    @Override
    public Filter getFilter() {
        if (search == null) {
            search = new SearchItem(itemFiltered, this);
        }

        return search;
    }

    @NonNull
    @Override
    public AdapterItem.HolderCollection onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AdapterItem.HolderCollection(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterItem.HolderCollection holder, int position) {
        ModelItem model = itemArrayList.get(position);
        String id = model.getId();
        String name = model.getName();
        String description = model.getDescription();
        String date = model.getDate();
        String pic = model.getPic();
        String collection = model.getCollection();
        String uid = model.getUid();

        holder.tvItem.setText(name);

        holder.imgBtnEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("description", description);
                hashMap.put("date", date);
                hashMap.put("pic", pic);
                hashMap.put("collection", collection);
                hashMap.put("uid", uid);

                //ADD EDIT ITEM CLASS
                Intent i = new Intent(context, EditCollection.class);
                i.putExtra("Info", hashMap);

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    class HolderCollection extends RecyclerView.ViewHolder {
        TextView tvItem;
        ImageButton imgBtnEditItem;

        public HolderCollection(@NonNull View itemView) {
            super(itemView);

            tvItem = binding.tvItem;
            imgBtnEditItem = binding.imgBtnEditItem;
        }
    }
}

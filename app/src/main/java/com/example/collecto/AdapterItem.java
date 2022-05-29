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

    public AdapterItem() {
    }

    public AdapterItem(Context context, ArrayList<ModelItem> itemArrayList) {
        this.context = context;
        this.itemArrayList = itemArrayList;
        this.itemFiltered = itemArrayList;
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
        String i_id = model.getId();
        String i_name = model.getName();
        String i_description = model.getDescription();
        String i_date = model.getDate();
        String i_pic = model.getPic();
        String i_collection = model.getCollection();
        String i_uid = model.getUid();

        holder.tvItem.setText(i_name);

        holder.imgBtnEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("id", i_id);
                hashMap.put("name", i_name);
                hashMap.put("description", i_description);
                hashMap.put("date", i_date);
                hashMap.put("pic", i_pic);
                hashMap.put("collection", i_collection);
                hashMap.put("uid", i_uid);

                //ADD EDIT ITEM CLASS
                Intent i = new Intent(context, EditItem.class);
                i.putExtra("item", hashMap);
                i.putExtra("coll", CollInfo());

                context.startActivity(i);
            }
        });
    }

    String c_id, c_name, c_description, c_uid;
    static HashMap<String,Object> c_info;
    public void SetCollData(HashMap<String,Object> info) {
        c_info = info;

        c_id = (String) info.get("id");
        c_name = (String) info.get("name");
        c_description = (String) info.get("description");
        c_uid = (String) info.get("uid");
    }

    public HashMap<String,Object> CollInfo() {
        return c_info;
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

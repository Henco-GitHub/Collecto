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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collecto.databinding.RowCollectionBinding;
import com.example.collecto.databinding.RowItemBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AdapterItem extends RecyclerView.Adapter<AdapterItem.HolderCollection> implements Filterable {
    //Adapter Class Variables
    //Context of current page Adapter is on
    private Context context;
    public ArrayList<ModelItem> itemArrayList, itemFiltered;
    private String c_id, c_name, c_description, c_uid;
    private static HashMap<String,Object> c_info;

    //View Binding
    private RowItemBinding binding;

    //Filtered List based on search
    private SearchItem search;

    //Constructor
    public AdapterItem() {
    }

    //Constructor
    public AdapterItem(Context context, ArrayList<ModelItem> itemArrayList) {
        this.context = context;
        this.itemArrayList = itemArrayList;
        this.itemFiltered = itemArrayList;
    }

    @NonNull
    @Override //When view for adapter is created
    public AdapterItem.HolderCollection onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AdapterItem.HolderCollection(binding.getRoot());
    }

    @Override //Binding Adapter Info with View's frontend elements
    public void onBindViewHolder(@NonNull AdapterItem.HolderCollection holder, int position) {
        //Temp ModelItem for easier use
        ModelItem model = itemArrayList.get(position);

        //Set variables of Object from ArrayList
        String i_id = model.getId();
        String i_name = model.getName();
        String i_description = model.getDescription();
        String i_date = model.getDate();
        String i_pic = model.getPic();
        String i_collection = model.getCollection();
        String i_uid = model.getUid();

        //Set Frontend Element Data
        holder.tvItem.setText(i_name);

        //CardView OnClick
        holder.cvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create Hashmap to send to next intent
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("id", i_id);
                hashMap.put("name", i_name);
                hashMap.put("description", i_description);
                hashMap.put("date", i_date);
                hashMap.put("pic", i_pic);
                hashMap.put("collection", i_collection);
                hashMap.put("uid", i_uid);

                //ADD EDIT ITEM CLASS
                //Intent
                Intent i = new Intent(context, ViewItem.class);
                //Send Collection and Item through Intent
                i.putExtra("item", hashMap);
                i.putExtra("coll", CollInfo());

                context.startActivity(i);
            }
        });

        //Edit Button OnClick
        holder.imgBtnEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create Hashmap to send to next intent
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("id", i_id);
                hashMap.put("name", i_name);
                hashMap.put("description", i_description);
                hashMap.put("date", i_date);
                hashMap.put("pic", i_pic);
                hashMap.put("collection", i_collection);
                hashMap.put("uid", i_uid);

                //Intent
                Intent i = new Intent(context, EditItem.class);
                //Send Collection and Item through Intent
                i.putExtra("item", hashMap);
                i.putExtra("coll", CollInfo());

                context.startActivity(i);
            }
        });


    }

    @Override //Get the count of collections
    public int getItemCount() {
        return itemArrayList.size();
    }

    @Override //Filter through arraylist with search query
    public Filter getFilter() {
        if (search == null) {
            search = new SearchItem(itemFiltered, this);
        }

        return search;
    }

    //Set Item's Collection Info from Intent's Extra
    public void SetCollData(HashMap<String,Object> info) {
        c_info = info;

        c_id = (String) info.get("id");
        c_name = (String) info.get("name");
        c_description = (String) info.get("description");
        c_uid = (String) info.get("uid");
    }

    //Get Info of Item's Collection
    public HashMap<String,Object> CollInfo() {
        return c_info;
    }

    //Holder Class to connect Binding with View
    class HolderCollection extends RecyclerView.ViewHolder {
        TextView tvItem;
        ImageButton imgBtnEditItem;
        CardView cvItem;

        //Constructor
        public HolderCollection(@NonNull View itemView) {
            super(itemView);

            //Set connect frontend elements with frontend
            tvItem = binding.tvItem;
            imgBtnEditItem = binding.imgBtnEditItem;
            cvItem = binding.cvItem;
        }
    }
}

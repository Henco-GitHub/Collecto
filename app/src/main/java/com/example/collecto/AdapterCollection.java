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
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterCollection extends RecyclerView.Adapter<AdapterCollection.HolderCollection> implements Filterable {

    private Context context;
    public ArrayList<ModelCollection> collArrayList, collFiltered;

    private RowCollectionBinding binding;

    private SearchCollection search;

    public AdapterCollection(Context context, ArrayList<ModelCollection> collArrayList) {
        this.context = context;
        this.collArrayList = collArrayList;
        this.collFiltered = collArrayList;
    }

    @NonNull
    @Override
    public HolderCollection onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCollectionBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderCollection(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCollection holder, int position) {
        ModelCollection model = collArrayList.get(position);
        String id = model.getId();
        String name = model.getName();
        String description = model.getDescription();
        String uid = model.getUid();

        holder.tvCollection.setText(name);

        holder.imgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("description", description);
                hashMap.put("uid", uid);

                Intent i = new Intent(context, EditCollection.class);
                i.putExtra("Info", hashMap);

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (search == null) {
            search = new SearchCollection(collFiltered, this);
        }

        return search;
    }

    class HolderCollection extends RecyclerView.ViewHolder {
        TextView tvCollection;
        ImageButton imgBtnEdit;

        public HolderCollection(@NonNull View itemView) {
            super(itemView);

            tvCollection = binding.tvCollection;
            imgBtnEdit = binding.imgBtnEdit;
        }
    }
}

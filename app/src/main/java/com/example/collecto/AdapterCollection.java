package com.example.collecto;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collecto.databinding.RowCollectionBinding;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterCollection extends RecyclerView.Adapter<AdapterCollection.HolderCollection> implements Filterable {
    private Context context;
    public ArrayList<ModelCollection> collArrayList, collFiltered;

    private RowCollectionBinding binding;
    private FirebaseAuth FireAuth;

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
        String goal = model.getGoal();
        String uid = model.getUid();

        FireAuth = FirebaseAuth.getInstance();

        holder.tvCollection.setText(name);
        LoadGoal(holder, id, goal);

        holder.cvCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("description", description);
                hashMap.put("goal", goal);
                hashMap.put("uid", uid);

                AdapterItem adapItem = new AdapterItem();
                adapItem.SetCollData(hashMap);

                Intent i = new Intent(context, myItems.class);
                i.putExtra("Info", hashMap);

                context.startActivity(i);
            }
        });

        holder.imgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("description", description);
                hashMap.put("goal", goal);
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

    private void LoadGoal(HolderCollection holder, String id, String goal) {
        ArrayList<ModelItem> itemArrayList = new ArrayList<>();
        DatabaseReference refItems = FirebaseDatabase.getInstance().getReference("items");

        refItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemArrayList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    ModelItem mi = snap.getValue(ModelItem.class);

                    FirebaseUser firebaseUser = FireAuth.getCurrentUser();

                    if (mi.getUid().equals(firebaseUser.getUid())) {
                        if (mi.getCollection().equals(id)) {
                            itemArrayList.add(mi);
                        }
                    }
                }

                holder.tvGoal.setText("Goal: " + itemArrayList.size() + "/" + goal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class HolderCollection extends RecyclerView.ViewHolder {
        TextView tvCollection;
        ImageButton imgBtnEdit;
        CardView cvCollection;
        TextView tvGoal;

        public HolderCollection(@NonNull View itemView) {
            super(itemView);

            tvCollection = binding.tvCollection;
            imgBtnEdit = binding.imgBtnEdit;
            cvCollection = binding.cvCollection;
            tvGoal = binding.tvGoal;
        }
    }
}

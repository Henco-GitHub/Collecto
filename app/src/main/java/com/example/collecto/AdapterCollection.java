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
    //Adapter Class Variables
    //Context of current page Adapter is on
    private Context context;
    public ArrayList<ModelCollection> collArrayList, collFiltered;

    //View Binding
    private RowCollectionBinding binding;

    //Firebase Auth
    private FirebaseAuth FireAuth;

    //Filtered List based on search
    private SearchCollection search;

    //Constructor
    public AdapterCollection(Context context, ArrayList<ModelCollection> collArrayList) {
        this.context = context;
        this.collArrayList = collArrayList;
        this.collFiltered = collArrayList;
    }

    @NonNull
    @Override //When view for adapter is created
    public HolderCollection onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCollectionBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderCollection(binding.getRoot());
    }

    @Override //Binding Adapter Info with View's frontend elements
    public void onBindViewHolder(@NonNull HolderCollection holder, int position) {
        //Temp ModelCollection for easier use
        ModelCollection model = collArrayList.get(position);

        //Set variables of Object from ArrayList
        String id = model.getId();
        String name = model.getName();
        String description = model.getDescription();
        String goal = model.getGoal();
        String uid = model.getUid();

        //Set Firebase instance
        FireAuth = FirebaseAuth.getInstance();

        //Set Frontend Element Data
        holder.tvCollection.setText(name);
        //Load collection's set goal, and progress
        LoadGoal(holder, id, goal);

        //CardView OnClick
        holder.cvCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create Hashmap to send to next intent
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("description", description);
                hashMap.put("goal", goal);
                hashMap.put("uid", uid);

                //Set static public variable for current collection in view
                AdapterItem adapterItem = new AdapterItem();
                adapterItem.SetCollData(hashMap);

                //Intent
                Intent i = new Intent(context, myItems.class);
                //Send Collection through Intent
                i.putExtra("Info", hashMap);

                context.startActivity(i);
            }
        });

        //Edit Button OnClick
        holder.imgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create Hashmap to send to next intent
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("description", description);
                hashMap.put("goal", goal);
                hashMap.put("uid", uid);

                //Intent
                Intent i = new Intent(context, EditCollection.class);
                //Send Collection through Intent
                i.putExtra("Info", hashMap);

                context.startActivity(i);
            }
        });
    }

    @Override //Get the count of collections
    public int getItemCount() {
        return collArrayList.size();
    }

    @Override //Filter through arraylist with search query
    public Filter getFilter() {
        if (search == null) {
            search = new SearchCollection(collFiltered, this);
        }

        return search;
    }

    //Load Set Goal and Progress for Collection
    private void LoadGoal(HolderCollection holder, String id, String goal) {
        ArrayList<ModelItem> itemArrayList = new ArrayList<>();
        DatabaseReference refItems = FirebaseDatabase.getInstance().getReference("items");

        //Get Collection's Children from Firebase
        refItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Clear Arraylist for collection items
                itemArrayList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    //Set instance of Object Item
                    ModelItem mi = snap.getValue(ModelItem.class);

                    //Get current logged in user data
                    FirebaseUser firebaseUser = FireAuth.getCurrentUser();

                    //Check User ID and Item ID match
                    if (mi.getUid().equals(firebaseUser.getUid())) {
                        if (mi.getCollection().equals(id)) {
                            //Add Item to Item Arraylist
                            itemArrayList.add(mi);
                        }
                    }
                }

                //Set frontend element to "Progress/Goal" (e.g. "5/10")
                holder.tvGoal.setText("Goal: " + itemArrayList.size() + "/" + goal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Holder Class to connect Binding with View
    class HolderCollection extends RecyclerView.ViewHolder {
        TextView tvCollection;
        ImageButton imgBtnEdit;
        CardView cvCollection;
        TextView tvGoal;

        //Constructor
        public HolderCollection(@NonNull View itemView) {
            super(itemView);

            //Set connect frontend elements with frontend
            tvCollection = binding.tvCollection;
            imgBtnEdit = binding.imgBtnEdit;
            cvCollection = binding.cvCollection;
            tvGoal = binding.tvGoal;
        }
    }
}

package com.umashankar.localitem.ui.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.umashankar.localitem.Model.Products;
import com.umashankar.localitem.R;
import com.umashankar.localitem.ViewHolder.ProductAdapter;
import com.umashankar.localitem.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

public class MainFragment extends Fragment {


    private RecyclerView recyclerView;
    private ArrayList<Products> arrayList;
    private EditText searchText;
    private ProductAdapter productAdapter;

    private DatabaseReference ProductReference;
    private ValueEventListener eventListener;
    private ProgressDialog loadingBar;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter;
    private FirebaseRecyclerOptions<Products> options;

    TextView layoutRefresh;
    CarouselView carouselView;
    int[] simpleImages = {R.drawable.bgpng, R.drawable.gajarpak,R.drawable.gajar_pak};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        carouselView = root.findViewById(R.id.carouselview);
        carouselView.setPageCount(simpleImages.length);
        carouselView.setImageListener(imageListener);

        searchText = root.findViewById(R.id.etSearch);
        recyclerView = root.findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        loadingBar = new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading..");
        loadingBar.setCanceledOnTouchOutside(false);

        arrayList = new ArrayList<>();

        ProductReference = FirebaseDatabase.getInstance().getReference().child("Products");
        layoutRefresh = root.findViewById(R.id.layoutRefresh);
        layoutRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProducts();
            }
        });
        getProducts();

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable text) {
                filter(text.toString());
            }
        });

        return root;
    }

    private void getProducts() {
        loadingBar.show();
        arrayList.clear();
        eventListener = ProductReference.orderByChild("position")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                            Products products = itemSnapshot.getValue(Products.class);
                            arrayList.add(products);
                            productAdapter = new ProductAdapter(getActivity(), arrayList);
                            recyclerView.setAdapter(productAdapter);
                        }
                        productAdapter.notifyDataSetChanged();
                        loadingBar.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingBar.dismiss();
                    }
                });
    }

    private void filter(String text) {
        ArrayList<Products> filterList = new ArrayList<>();
        for (Products item : arrayList) {
            if (item.getPname().toLowerCase().contains(text.toLowerCase())) {
                filterList.add(item);
            }
        }
        productAdapter.filteredList(filterList);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(simpleImages[position]);
        }
    };
}
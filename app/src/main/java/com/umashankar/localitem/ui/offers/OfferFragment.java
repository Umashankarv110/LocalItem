package com.umashankar.localitem.ui.offers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umashankar.localitem.Model.Coupon;
import com.umashankar.localitem.R;
import com.umashankar.localitem.ViewHolder.CouponViewHolder;
import com.umashankar.localitem.ViewOffersActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;



public class OfferFragment extends Fragment {

    private AppBarConfiguration mAppBarConfiguration;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference couponReference;
    private ProgressDialog loadingBar;
    private String Status="Activate";

    public OfferFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_offer, container, false);
        couponReference = FirebaseDatabase.getInstance().getReference().child("CouponsCode").child("Activate");

        recyclerView = root.findViewById(R.id.recycler_coupon);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Coupon> options =
                new FirebaseRecyclerOptions.Builder<Coupon>()
                        .setQuery(couponReference, Coupon.class)
                        .build();

        FirebaseRecyclerAdapter<Coupon, CouponViewHolder> adapter = new FirebaseRecyclerAdapter<Coupon, CouponViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CouponViewHolder couponHolder, int i, @NonNull final Coupon coupon) {
                couponHolder.txtCouponCode.setText(coupon.getCouponCode());
                couponHolder.txtCouponDescription.setText(coupon.getDescription());
                couponHolder.txtCouponexpDate.setText("Exp Date: " + coupon.getEndDate());
                couponHolder.txtCouponValue.setText("Rs."+coupon.getCouponValue() + " Off");
                Picasso.get().load(coupon.getCouponTheme()).into(couponHolder.CouponImage);

                couponHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ViewOffersActivity.class);
                        intent.putExtra("offerCode",coupon.getCouponCode());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_coupon_item_layout, parent,false);
                CouponViewHolder holder = new CouponViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}

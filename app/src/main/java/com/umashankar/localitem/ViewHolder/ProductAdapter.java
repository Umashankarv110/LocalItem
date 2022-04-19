package com.umashankar.localitem.ViewHolder;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.umashankar.localitem.Model.Products;
import com.umashankar.localitem.ProductDetailActivity;
import com.umashankar.localitem.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyProductViewHolder> {

    private Context mContext;
    private List<Products> productsList;

    public ProductAdapter(Context mContext, List<Products> productsList) {
        this.mContext = mContext;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public MyProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent,false);
        return new MyProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProductViewHolder holder, final int position) {
        holder.txtproductName.setText(productsList.get(position).getPname());
        holder.txtproductDescription.setText(productsList.get(position).getDetails());
        holder.txtproductPrice.setText("₹"+productsList.get(position).getPrice());
        Glide.with(mContext).load(productsList.get(position).getImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.productImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class );
                intent.putExtra("pid",productsList.get(position).getPid());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public void filteredList(ArrayList<Products> filterList) {
        productsList = filterList;
        notifyDataSetChanged();
    }

    class MyProductViewHolder extends RecyclerView.ViewHolder{
        TextView txtproductName,txtproductDescription,txtproductPrice;
        ImageView productImage;

        public MyProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            txtproductName = itemView.findViewById(R.id.productName);
            txtproductDescription = itemView.findViewById(R.id.productDescription);
            txtproductPrice = itemView.findViewById(R.id.productPrice);
        }
    }

}

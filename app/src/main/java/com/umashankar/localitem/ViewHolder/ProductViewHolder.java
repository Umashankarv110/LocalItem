package com.umashankar.localitem.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.umashankar.localitem.Interface.ItemClickListner;
import com.umashankar.localitem.Model.Products;
import com.umashankar.localitem.R;
import java.util.List;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtproductName,txtproductDescription,txtproductPrice;
    public ImageView productImage;
    public ItemClickListner listner;
    public List<Products> myProductsList;
    public ProductViewHolder  myProductAdapter;


    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage = itemView.findViewById(R.id.productImage);
        txtproductName = itemView.findViewById(R.id.productName);
        txtproductDescription = itemView.findViewById(R.id.productDescription);
        txtproductPrice = itemView.findViewById(R.id.productPrice);

    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }

}

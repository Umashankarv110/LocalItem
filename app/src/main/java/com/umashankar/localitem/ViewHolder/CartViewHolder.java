package com.umashankar.localitem.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.umashankar.localitem.Interface.ItemClickListner;
import com.umashankar.localitem.R;


public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName,txtProductPrice,txtTotalProductPrice,txtProductQty;
    public ImageView cartProductImage,cartItemDelete,cartEdit;
    public ItemClickListner listner;

    public CartViewHolder(View itemView) {
        super(itemView);
        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductQty = itemView.findViewById(R.id.cart_product_qty);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price);
        cartProductImage = itemView.findViewById(R.id.cart_product_image);
        txtTotalProductPrice = itemView.findViewById(R.id.cart_product_Total_price);
        cartItemDelete = itemView.findViewById(R.id.cartItemDelete);
        cartEdit = itemView.findViewById(R.id.cartItemEdit);
    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }
}

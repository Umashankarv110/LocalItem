package com.umashankar.localitem.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.umashankar.localitem.Interface.ItemClickListner;
import com.umashankar.localitem.R;

public class OrderProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName, txtProductPrice, txtTotalProductPrice, txtProductQty;
    public ImageView cartProductImage, cartItemDelete;
    public ItemClickListner listner;

    public OrderProductViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductName = itemView.findViewById(R.id.orderProductTitle);
        txtProductQty = itemView.findViewById(R.id.orderQty);
        txtProductPrice = itemView.findViewById(R.id.orderProductPrice);
        cartProductImage = itemView.findViewById(R.id.orderDetailImg);
    }

    public void setItemClickListner(ItemClickListner listner) {
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }
}

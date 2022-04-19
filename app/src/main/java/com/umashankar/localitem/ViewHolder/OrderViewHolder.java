package com.umashankar.localitem.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.umashankar.localitem.Interface.ItemClickListner;
import com.umashankar.localitem.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName,txtOrderDate,txtOrderId,txtOrderStatus;
    public ImageView productImage;
    public ItemClickListner listner;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductName = itemView.findViewById(R.id.orderProductName);
        txtOrderDate = itemView.findViewById(R.id.orderDate);
        txtOrderId = itemView.findViewById(R.id.orderId);
        txtOrderStatus = itemView.findViewById(R.id.orderStatus);
    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }

}

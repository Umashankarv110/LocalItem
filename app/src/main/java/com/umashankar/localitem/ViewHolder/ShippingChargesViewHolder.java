package com.umashankar.localitem.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.umashankar.localitem.Interface.ItemClickListner;
import com.umashankar.localitem.R;

public class ShippingChargesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtStateName, txtCompanyStatus,txtCompName,txtDeliverIn,state,txtCharges;
    public ItemClickListner listner;

    public ShippingChargesViewHolder(@NonNull View itemView) {
        super(itemView);
        state = itemView.findViewById(R.id.comp_ship_status);
        txtStateName = itemView.findViewById(R.id.state_name);
        txtCompanyStatus = itemView.findViewById(R.id.comp_ship_status);
        txtCompName = itemView.findViewById(R.id.comp_ship_name);
        txtDeliverIn = itemView.findViewById(R.id.ship_deliver_in);
        txtCharges = itemView.findViewById(R.id.ship_deliver_charges);
    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }
}

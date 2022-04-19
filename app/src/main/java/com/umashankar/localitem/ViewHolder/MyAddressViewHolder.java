package com.umashankar.localitem.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.umashankar.localitem.Interface.ItemClickListner;
import com.umashankar.localitem.R;

public class MyAddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtName,txtAddress,txtCity,txtPincode,txtState,txtLandmark,txtContact;
    public ImageButton deleteAddress,editAddress;
    public ItemClickListner listner;
    public Button SelectBTN;

    public MyAddressViewHolder(@NonNull View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.address_item_name);
        txtAddress = itemView.findViewById(R.id.address_item_line_1);
        txtCity = itemView.findViewById(R.id.address_item_city);
        txtPincode = itemView.findViewById(R.id.address_item_pincode);
        txtState = itemView.findViewById(R.id.address_item_state);
        txtLandmark = itemView.findViewById(R.id.address_item_landmark);
        txtContact = itemView.findViewById(R.id.address_item_contact);
        deleteAddress = itemView.findViewById(R.id.iv_Delete_address);
        editAddress = itemView.findViewById(R.id.iv_Edit_Address);

        SelectBTN = itemView.findViewById(R.id.aadressSelect);

    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }
}

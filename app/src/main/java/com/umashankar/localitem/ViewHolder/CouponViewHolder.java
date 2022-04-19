package com.umashankar.localitem.ViewHolder;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.umashankar.localitem.Interface.ItemClickListner;
import com.umashankar.localitem.R;

public class CouponViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtCouponCode,txtCouponDescription,txtCouponexpDate,txtCouponValue;
    public ImageView CouponImage;
    public ItemClickListner listner;
    public Button apply;

    public CouponViewHolder(@NonNull View itemView) {
        super(itemView);
        txtCouponCode = itemView.findViewById(R.id.coupon_Code);
        txtCouponDescription = itemView.findViewById(R.id.coupon_Detail);
        txtCouponexpDate = itemView.findViewById(R.id.coupon_Exp_Date);
        txtCouponValue = itemView.findViewById(R.id.coupon_Value);
        CouponImage = itemView.findViewById(R.id.coupon_Image);
        apply = itemView.findViewById(R.id.appyCoupons);

    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }

}

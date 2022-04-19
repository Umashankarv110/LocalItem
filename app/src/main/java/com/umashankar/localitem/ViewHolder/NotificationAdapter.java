package com.umashankar.localitem.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.umashankar.localitem.Interface.ItemClickListner;
import com.umashankar.localitem.R;


public class NotificationAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView imageView;
    public TextView mesgTitle,mesgDetails,mesgTime;
    public ItemClickListner listner;

    public NotificationAdapter(@NonNull View itemView) {
        super(itemView);
        mesgTitle = itemView.findViewById(R.id.Notifytitle);
        mesgDetails = itemView.findViewById(R.id.NotifyDetails);
        mesgTime = itemView.findViewById(R.id.NotifyTime);

    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
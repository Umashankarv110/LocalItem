package com.umashankar.localitem.ui.shop;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.umashankar.localitem.R;

public class ShopFragment extends Fragment {

    private GridLayout GridMenu;
    private Button b1,b2;
    private LinearLayout linearLayout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shop, container, false);

        GridMenu = root.findViewById(R.id.gridMenu);
        linearLayout = root.findViewById(R.id.linearMenu);
        b1 = root.findViewById(R.id.newArrivals);
        b2 = root.findViewById(R.id.upComing);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComingsoonDialog();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComingsoonDialog();
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComingsoonDialog();
            }
        });



        return root;
    }
    private void ComingsoonDialog() {
        final Dialog soonDialog = new Dialog(getActivity());
        soonDialog.setContentView(R.layout.comingsoon_layout);
        soonDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        soonDialog.setCancelable(true);
        TextView textView = soonDialog.findViewById(R.id.textView39);
        textView.setVisibility(View.GONE);
        Button Ok = soonDialog.findViewById(R.id.buttonOk);
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soonDialog.dismiss();
            }
        });
        soonDialog.show();

    }
}

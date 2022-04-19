package com.umashankar.localitem.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umashankar.localitem.Model.StateName;
import com.umashankar.localitem.R;

import java.util.List;

public class StateAdapter extends ArrayAdapter<StateName> {
    Context context;
    List<StateName> arrayListStateName;

    public StateAdapter(@NonNull Context context, List<StateName> arrayListStateName) {
        super(context, R.layout.layout_state, arrayListStateName);
        this.context = context;
        this.arrayListStateName = arrayListStateName;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_state, null, true);
        TextView tv_Title = view.findViewById(R.id.stateName);
        tv_Title.setText(""+ arrayListStateName.get(position).getStateName());
        return view;
    }
}
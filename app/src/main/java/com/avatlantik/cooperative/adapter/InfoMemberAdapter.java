package com.avatlantik.cooperative.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.model.ParameterInfo;

import java.util.ArrayList;

public class InfoMemberAdapter extends BaseAdapter {

    private ArrayList<ParameterInfo> list;
    private LayoutInflater layoutInflater;

    public InfoMemberAdapter(Context context, ArrayList<ParameterInfo> list) {
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.milk_param_fragment, parent, false);
        }
        ParameterInfo parameterInfo = getDataTable(position);

        TextView infoNameTextView = (TextView) view.findViewById(R.id.name_par_TextView);
        infoNameTextView.setText(parameterInfo.getName() + " :");

        TextView valParTV = (TextView) view.findViewById(R.id.value_par_TextView);
        valParTV.setText(parameterInfo.getValue());

        EditText valParET = (EditText) view.findViewById(R.id.value_par_editText);
        valParET.setVisibility(View.GONE);
        valParET.setEnabled(false);

        return view;
    }

    private ParameterInfo getDataTable(int position) {
        return (ParameterInfo) getItem(position);
    }
}

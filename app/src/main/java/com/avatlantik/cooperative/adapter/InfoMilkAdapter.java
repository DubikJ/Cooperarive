package com.avatlantik.cooperative.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.model.ParameterInfo;

import java.util.ArrayList;
import java.util.List;

public class InfoMilkAdapter extends BaseAdapter {

    private List<ParameterInfo> parameters;
    private LayoutInflater layoutInflater;
    private Context context;

    public InfoMilkAdapter(Context context, ArrayList<ParameterInfo> parameters) {
        this.context = context;
        this.parameters = parameters;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return parameters.size();
    }

    @Override
    public Object getItem(int position) {
        return parameters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MilkRowViewHolder viewHolder;
        View row = convertView;
        if (row == null) {
            row = layoutInflater.inflate(R.layout.milk_param_fragment, parent, false);
            viewHolder = new MilkRowViewHolder(row);
            row.setTag(viewHolder);
        } else {
            viewHolder = (MilkRowViewHolder) row.getTag();
        }
        ParameterInfo parameterInfo = getDataTable(position);
        viewHolder.paramNameView.setText(parameterInfo.getName());
        viewHolder.position = position;

        if (parameterInfo.isEditable()) {
            viewHolder.paramValueTextView.setVisibility(View.GONE);
            viewHolder.paramValueTextView.setEnabled(false);
            viewHolder.paramValueEditView.setVisibility(View.VISIBLE);
            viewHolder.paramValueEditView.setText(parameterInfo.getValue());
            viewHolder.paramValueEditView.setId(position);
            viewHolder.paramValueEditView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    EditText editText = (EditText) view;
                    String value = String.valueOf(editText.getText());
                    if (hasFocus) {
                       if (TextUtils.isEmpty(value) || value.equals("0.0")) {
                           editText.setText("");
                       }
                    }else {
                        if (TextUtils.isEmpty(value)) {
                          editText.setText("0.0");
                        }
                    }
                    parameters.get(editText.getId()).setValue(String.valueOf(editText.getText()));
                    }
                    });


        } else {
            viewHolder.paramValueTextView.setText(parameterInfo.getValue());
            viewHolder.paramValueTextView.setVisibility(View.VISIBLE);
            viewHolder.paramValueEditView.setVisibility(View.GONE);
            viewHolder.paramValueEditView.setEnabled(false);
        }

        return row;
    }

    private class MilkRowViewHolder {
        int position;
        TextView paramNameView;
        TextView paramValueTextView;
        EditText paramValueEditView;

        MilkRowViewHolder(View view) {
            paramNameView = (TextView) view.findViewById(R.id.name_par_TextView);
            paramValueTextView = (TextView) view.findViewById(R.id.value_par_TextView);
            paramValueEditView = (EditText) view.findViewById(R.id.value_par_editText);
        }
    }

    private ParameterInfo getDataTable(int position) {
        return (ParameterInfo) getItem(position);
    }
}

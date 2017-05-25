package com.avatlantik.cooperative.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.adapter.treelistview.Element;
import com.avatlantik.cooperative.model.db.ServiceDemand;
import com.avatlantik.cooperative.model.db.Visit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesListAdapter extends BaseAdapter {
    private ArrayList<Element> elements;
    private ArrayList<Element> elementsData;
    private int indentionBase;
    private Map<String, ServiceDemand> serviceDemandsMap;
    private LayoutInflater layoutInflater;
    private Visit visit;

    public ServicesListAdapter(Context context, Visit visit, ArrayList<Element> elements,
                               ArrayList<Element> elementsData, List<ServiceDemand> serviceDemands) {
        this.elements = elements;
        this.elementsData = elementsData;
        this.visit = visit;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        serviceDemandsMap = new HashMap<>();
        for (ServiceDemand serviceDemand : serviceDemands) {
            serviceDemandsMap.put(serviceDemand.getCode(), serviceDemand);
        }
        indentionBase = 20;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public ArrayList<Element> getElementsData() {
        return elementsData;
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public Object getItem(int position) {
        return elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.fragment_service_element, null);
            holder.disclosureImg = (ImageView) convertView.findViewById(R.id.service_el_imageview);
            holder.aSwitch = (Switch) convertView.findViewById(R.id.service_el_switch);
            holder.aSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Switch aSwitch = (Switch) view;
                    serviceDemandsMap.get(aSwitch.getTag().toString()).setValue(aSwitch.isChecked());
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Element element = elements.get(position);
        int level = element.getLevel();
        holder.disclosureImg.setPadding(
                indentionBase * level,
                holder.disclosureImg.getPaddingTop(),
                holder.disclosureImg.getPaddingRight(),
                holder.disclosureImg.getPaddingBottom());

        String serviceCode = element.getExternalId();
        String serviceName = element.getContentText();

        holder.aSwitch.setTag(serviceCode);
        holder.aSwitch.setText(serviceName);

        ServiceDemand serviceDemand = serviceDemandsMap.get(serviceCode);
        if (serviceDemand != null) {
            holder.aSwitch.setChecked(serviceDemand.getValue());
        } else {
            serviceDemandsMap.put(
                    serviceCode,
                    ServiceDemand.builder()
                            .visitId(visit.getId())
                            .code(serviceCode)
                            .value(false).build());
            holder.aSwitch.setSelected(false);
        }

        if (element.isHasChildren() && !element.isExpanded()) {
            holder.disclosureImg.setImageResource(R.drawable.ic_expand_right);
            holder.disclosureImg.setVisibility(View.VISIBLE);
        } else if (element.isHasChildren() && element.isExpanded()) {
            holder.disclosureImg.setImageResource(R.drawable.ic_expand_down);
            holder.disclosureImg.setVisibility(View.VISIBLE);
        } else if (!element.isHasChildren()) {
            holder.disclosureImg.setImageResource(R.drawable.ic_expand_right);
            holder.disclosureImg.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
    static class ViewHolder{
        ImageView disclosureImg;
        Switch aSwitch;
    }

    public Object getItemServiceDemand(int position) {
        return serviceDemandsMap.get(elements.get(position).getExternalId());
    }
}
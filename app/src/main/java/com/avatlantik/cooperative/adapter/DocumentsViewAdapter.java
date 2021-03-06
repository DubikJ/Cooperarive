package com.avatlantik.cooperative.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.model.DocumentItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DocumentsViewAdapter extends ArrayAdapter<DocumentItem> {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<DocumentItem> mGridData = new ArrayList<>();

    public DocumentsViewAdapter(Context mContext, int layoutResourceId, ArrayList<DocumentItem> mGridData) {
        super(mContext, layoutResourceId, mGridData);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    /**
     * Updates grid data and refresh grid items.
     *
     * @param mGridData
     */
    public void setGridData(ArrayList<DocumentItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        DocumentItem item = mGridData.get(position);
        holder.titleTextView.setText(item.getTitle());
        Picasso.with(mContext).load(item.getFile()).placeholder(R.drawable.shape_camera).into(holder.imageView);

        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }
}

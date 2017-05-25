package com.avatlantik.cooperative.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.activity.MemberActivity;
import com.avatlantik.cooperative.db.CooperativeContract;
import com.avatlantik.cooperative.model.db.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MembersSearchAdapter extends BaseAdapter implements Filterable {

    private List<Member> modelValues;
    private List<Member> mOriginalValues;
    private Context context;
    private LayoutInflater layoutInflater;

    public MembersSearchAdapter(Context context, List<Member> modelValues) {
        this.modelValues = modelValues;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return modelValues.size();
    }

    @Override
    public Object getItem(int position) {
        return modelValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public
    @NonNull
    View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        final Member member = getMember(position);

        TextView infoNameTextView = (TextView) view.findViewById(android.R.id.text1);
        infoNameTextView.setText(member.getName());
        infoNameTextView.setTextSize(context.getResources().getDimension(R.dimen.text_size_medium));
        infoNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(
                        new Intent(context, MemberActivity.class)
                                .putExtra(CooperativeContract.MemberContract._ID, member.getId()));
            }
        });

        return view;
    }

    private Member getMember(int position) {
        return (Member) getItem(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                modelValues = (ArrayList<Member>) results.values; // has

                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the

                List<Member> FilteredArrList = new ArrayList<Member>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<>(modelValues); // saves

                }
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    Locale locale = Locale.getDefault();
                    constraint = constraint.toString().toLowerCase(locale);
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        Member member = mOriginalValues.get(i);

                        String data = member.getName();
                        if (data.toLowerCase(locale).contains(constraint.toString())) {

                            FilteredArrList.add(member);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;

                }
                return results;
            }
        };
        return filter;
    }


}
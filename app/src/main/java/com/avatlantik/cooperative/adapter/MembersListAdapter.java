package com.avatlantik.cooperative.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.activity.MemberActivity;
import com.avatlantik.cooperative.db.CooperativeContract;
import com.avatlantik.cooperative.model.LandingMember;

import java.util.List;

import static java.lang.String.format;

public class MembersListAdapter extends ArrayAdapter<LandingMember> {

    public MembersListAdapter(Context context, List<LandingMember> objects) {
        super(context, 0, objects);
    }

    @Override
    public
    @NonNull
    View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final LandingMember member = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.landing_member, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.memberNameView = (TextView) convertView.findViewById(R.id.member_name);
            viewHolder.fatView = (TextView) convertView.findViewById(R.id.fat);
            viewHolder.litresView = (TextView) convertView.findViewById(R.id.litres);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.memberNameView.setText(member.getName());
        viewHolder.litresView.setText(format("%s", member.getLitres() == null ? "" : member.getLitres()));
        viewHolder.fatView.setText(format("%s", member.getFat() == null ? "" : member.getFat()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(
                        new Intent(getContext(), MemberActivity.class)
                                .putExtra(CooperativeContract.MemberContract._ID, getItem(position).getId()));
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        int position;
        TextView memberNameView;
        TextView litresView;
        TextView fatView;
    }
}
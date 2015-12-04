package com.codepath.hansel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codepath.hansel.R;
import com.codepath.hansel.models.Mapper;
import com.codepath.hansel.models.Pebble;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LogAdapter extends ArrayAdapter<Pebble> {

    private class ViewHolder {
        RoundedImageView ivProfileImage;
        TextView tvName;
        TextView tvGeolocation;
        TextView tvTimestamp;
    }

    public LogAdapter(Context context, List<Pebble> pebbles) {
        super(context, 0, pebbles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pebble pebble = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pebble, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivProfileImage = (RoundedImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvGeolocation = (TextView) convertView.findViewById(R.id.tvGeolocation);
            viewHolder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();

        Picasso.with(getContext())
                .load(pebble.getUserImageUrl())
                .placeholder(R.mipmap.ic_default_profile)
                .into(viewHolder.ivProfileImage);
        int color = Mapper.getInstance().getColorForUser(pebble.getUser());
        viewHolder.ivProfileImage.setBorderColor(getContext().getResources().getColor(color));
        viewHolder.tvName.setText(pebble.getUser().getFullName());
        viewHolder.tvGeolocation.setText(pebble.getCoordinate());
        viewHolder.tvTimestamp.setText(pebble.getRelativeTimeAgo());

        return convertView;
    }
}

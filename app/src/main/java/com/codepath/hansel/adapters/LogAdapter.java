package com.codepath.hansel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codepath.hansel.R;
import com.codepath.hansel.models.Pebble;

import java.util.List;

public class LogAdapter extends ArrayAdapter<Pebble> {

    private class ViewHolder {
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
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvGeolocation = (TextView) convertView.findViewById(R.id.tvGeolocation);
            viewHolder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.tvName.setText(pebble.getUser().getFullName());
        viewHolder.tvGeolocation.setText(pebble.getCoordinate());
        viewHolder.tvTimestamp.setText(pebble.getRelativeTimeAgo());

        return convertView;
    }
}

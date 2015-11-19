package com.codepath.hansel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.hansel.R;
import com.codepath.hansel.models.GeoPoint;

import java.util.List;

public class GeolocationAdapter extends ArrayAdapter<GeoPoint> {

    private class ViewHolder {
        TextView tvName;
        TextView tvGeolocation;
        TextView tvTimestamp;
    }

    public GeolocationAdapter(Context context, List<GeoPoint> geopoints) {
        super(context, 0, geopoints);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GeoPoint geoPoint = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_geopoint, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvGeolocation = (TextView) convertView.findViewById(R.id.tvGeolocation);
            viewHolder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.tvName.setText(geoPoint.getName());
        viewHolder.tvGeolocation.setText(geoPoint.getGeolocation());
        viewHolder.tvTimestamp.setText(geoPoint.getTimestamp());

        return convertView;
    }
}

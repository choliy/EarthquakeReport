package com.choliy.igor.earthquakereport.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.choliy.igor.earthquakereport.R;
import com.choliy.igor.earthquakereport.model.Earthquake;

import java.text.DecimalFormat;
import java.util.List;

import static com.choliy.igor.earthquakereport.web.EarthquakeContract.DATE_FORMAT;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.MAGNITUDE_FORMAT;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.TIME_FORMAT;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.QuakeHolder> {

    private Context mContext;
    private List<Earthquake> mEarthquakes;

    public EarthquakeAdapter(Context context, List<Earthquake> earthquakes) {
        mContext = context;
        mEarthquakes = earthquakes;
    }

    @Override
    public QuakeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.earthquake_list_item, parent, false);
        return new QuakeHolder(view);
    }

    @Override
    public void onBindViewHolder(QuakeHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return mEarthquakes.size();
    }

    public void addData(List<Earthquake> earthquakes) {
        mEarthquakes.addAll(earthquakes);
        notifyDataSetChanged();
    }

    public void clearData() {
        int size = mEarthquakes.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mEarthquakes.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }

    class QuakeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String LOCATION_SEPARATOR = " of ";
        private TextView mMagnitude;
        private TextView mLocationOffset;
        private TextView mLocation;
        private TextView mDate;
        private TextView mTime;
        private LinearLayout mListItem;

        QuakeHolder(View itemView) {
            super(itemView);
            mMagnitude = (TextView) itemView.findViewById(R.id.list_item_magnitude);
            mLocationOffset = (TextView) itemView.findViewById(R.id.list_item_location_offset);
            mLocation = (TextView) itemView.findViewById(R.id.list_item_location);
            mDate = (TextView) itemView.findViewById(R.id.list_item_date);
            mTime = (TextView) itemView.findViewById(R.id.list_item_time);
            mListItem = (LinearLayout) itemView.findViewById(R.id.earthquake_list_item);
            mListItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Uri earthquakeUri = Uri.parse(mEarthquakes.get(getAdapterPosition()).getUrl());
            Intent webIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
            mContext.startActivity(webIntent);
        }

        private void bindView(int position) {
            GradientDrawable magnitudeCircle = (GradientDrawable) mMagnitude.getBackground();
            int magnitudeColor = getMagnitudeColor(mEarthquakes.get(position).getMagnitude());
            magnitudeCircle.setColor(magnitudeColor);
            String formatMagnitude = formatMagnitude(mEarthquakes.get(position).getMagnitude());
            mMagnitude.setText(formatMagnitude);

            String originalLocation = mEarthquakes.get(position).getLocation();
            if (originalLocation.contains(LOCATION_SEPARATOR)) {
                String[] parts = originalLocation.split(LOCATION_SEPARATOR);
                String locationOffset = parts[0] + LOCATION_SEPARATOR;
                mLocationOffset.setText(locationOffset);
                mLocation.setText(parts[1]);
            } else {
                mLocationOffset.setText(mContext.getString(R.string.text_offset));
                mLocation.setText(originalLocation);
            }

            mDate.setText(formatTime(DATE_FORMAT, position));
            mTime.setText(formatTime(TIME_FORMAT, position));
        }

        private int getMagnitudeColor(double magnitude) {
            int magnitudeColorResourceId;
            int magnitudeFloor = (int) Math.floor(magnitude);
            switch (magnitudeFloor) {
                case 0:
                    magnitudeColorResourceId = R.color.magnitude_00;
                    break;
                case 1:
                    magnitudeColorResourceId = R.color.magnitude_01;
                    break;
                case 2:
                    magnitudeColorResourceId = R.color.magnitude_02;
                    break;
                case 3:
                    magnitudeColorResourceId = R.color.magnitude_03;
                    break;
                case 4:
                    magnitudeColorResourceId = R.color.magnitude_04;
                    break;
                case 5:
                    magnitudeColorResourceId = R.color.magnitude_05;
                    break;
                case 6:
                    magnitudeColorResourceId = R.color.magnitude_06;
                    break;
                case 7:
                    magnitudeColorResourceId = R.color.magnitude_07;
                    break;
                case 8:
                    magnitudeColorResourceId = R.color.magnitude_08;
                    break;
                case 9:
                    magnitudeColorResourceId = R.color.magnitude_09;
                    break;
                default:
                    magnitudeColorResourceId = R.color.magnitude_10;
                    break;
            }
            return ContextCompat.getColor(mContext, magnitudeColorResourceId);
        }

        private String formatMagnitude(double magnitude) {
            DecimalFormat magnitudeFormat = new DecimalFormat(MAGNITUDE_FORMAT);
            return magnitudeFormat.format(magnitude);
        }

        private String formatTime(String dateFormat, int position) {
            return (String) DateFormat.format(dateFormat, mEarthquakes.get(position).getTime());
        }
    }
}
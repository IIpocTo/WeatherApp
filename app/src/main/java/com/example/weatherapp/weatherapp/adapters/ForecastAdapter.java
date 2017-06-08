package com.example.weatherapp.weatherapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weatherapp.weatherapp.R;
import com.example.weatherapp.weatherapp.utilities.DateTimeUtils;
import com.example.weatherapp.weatherapp.utilities.WeatherUtils;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private final Context mContext;
    private final ForecastAdapterOnClickHandler mClickHandler;

    private Cursor mCursor;

    public interface ForecastAdapterOnClickHandler {
        void onListWeatherItemClick(long clickedItemDate);
    }

    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler, Context context) {
        this.mClickHandler = clickHandler;
        this.mContext = context;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.forecast_list_item, parent, false);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        long date = mCursor.getLong(WeatherEntry.INDEX_COLUMN_DATE);
        String dateString = DateTimeUtils.getReadableDateString(mContext, date);
        int weatherConditionId = mCursor.getInt(WeatherEntry.INDEX_COLUMN_WEATHER_ID);
        String description = WeatherUtils.getStringForWeatherCondition(mContext, weatherConditionId);
        double min = mCursor.getDouble(WeatherEntry.INDEX_COLUMN_MIN);
        double max = mCursor.getDouble(WeatherEntry.INDEX_COLUMN_MAX);
        String highLowTemp = WeatherUtils.formatHighLows(mContext, min, max);
        holder.mWeatherTextView.setText(dateString + " - " + description + " - " + highLowTemp);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            mWeatherTextView = (TextView) itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            mClickHandler.onListWeatherItemClick(mCursor.getLong(WeatherEntry.INDEX_COLUMN_DATE));
        }
    }

}

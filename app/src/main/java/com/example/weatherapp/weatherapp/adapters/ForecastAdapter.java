package com.example.weatherapp.weatherapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        holder.dateView.setText(dateString);

        int weatherConditionId = mCursor.getInt(WeatherEntry.INDEX_COLUMN_WEATHER_ID);
        int iconId = WeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherConditionId);
        holder.iconView.setImageResource(iconId);

        String description = WeatherUtils.getStringForWeatherCondition(mContext, weatherConditionId);
        String descriptionA11y = mContext.getString(R.string.a11y_forecast, description);
        holder.descriptionView.setText(description);
        holder.descriptionView.setContentDescription(descriptionA11y);

        double lowTemp = mCursor.getDouble(WeatherEntry.INDEX_COLUMN_MIN);
        String lowTempA11y = mContext.getString(R.string.a11y_low_temp, String.valueOf(lowTemp));
        holder.lowTempView.setText(WeatherUtils.formatTemperature(mContext, lowTemp));
        holder.lowTempView.setContentDescription(lowTempA11y);

        double highTemp = mCursor.getDouble(WeatherEntry.INDEX_COLUMN_MAX);
        String highTempA11y = mContext.getString(R.string.a11y_high_temp, String.valueOf(highTemp));
        holder.highTempView.setText(WeatherUtils.formatTemperature(mContext, highTemp));
        holder.highTempView.setContentDescription(highTempA11y);

    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView iconView;
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            iconView = (ImageView) itemView.findViewById(R.id.weather_icon);
            dateView = (TextView) itemView.findViewById(R.id.date);
            descriptionView = (TextView) itemView.findViewById(R.id.weatherDescription);
            highTempView = (TextView) itemView.findViewById(R.id.highTemp);
            lowTempView = (TextView) itemView.findViewById(R.id.lowTemp);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            mClickHandler.onListWeatherItemClick(mCursor.getLong(WeatherEntry.INDEX_COLUMN_DATE));
        }
    }

}

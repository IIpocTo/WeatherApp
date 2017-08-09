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

    public static final int NOW_VIEW_TYPE = 0;
    public static final int FUTURE_VIEW_TYPE = 1;

    private final Context mContext;
    private final ForecastAdapterOnClickHandler mClickHandler;

    private Cursor mCursor;
    private boolean mUseTodayLayout;

    public interface ForecastAdapterOnClickHandler {
        void onListWeatherItemClick(long clickedItemDate);
    }

    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler, Context context) {
        this.mClickHandler = clickHandler;
        this.mContext = context;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutId;

        switch (viewType) {
            case NOW_VIEW_TYPE:
                layoutId = R.layout.list_item_forecast_today;
                break;
            case FUTURE_VIEW_TYPE:
                layoutId = R.layout.forecast_list_item;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type of " + viewType);
        }

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutId, parent, false);
        view.setFocusable(true);

        return new ForecastAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        long date = mCursor.getLong(WeatherEntry.INDEX_COLUMN_DATE);
        String dateString = DateTimeUtils.getReadableDateString(mContext, date);
        holder.dateView.setText(dateString);

        int weatherConditionId = mCursor.getInt(WeatherEntry.INDEX_COLUMN_WEATHER_ID);
        int iconId;
        int viewType = getItemViewType(position);

        switch (viewType) {
            case NOW_VIEW_TYPE:
                iconId = WeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherConditionId);
                break;
            case FUTURE_VIEW_TYPE:
                iconId = WeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherConditionId);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type of " + viewType);
        }
        holder.iconView.setImageResource(iconId);

        String description = WeatherUtils.getStringForWeatherCondition(mContext, weatherConditionId);
        String descriptionA11y = mContext.getString(R.string.a11y_forecast, description);
        holder.descriptionView.setText(description);
        holder.descriptionView.setContentDescription(descriptionA11y);

        double temp = mCursor.getDouble(WeatherEntry.INDEX_COLUMN_TEMP);
        String tempA11y = mContext.getString(R.string.a11y_temp, String.valueOf(temp));
        holder.tempView.setText(WeatherUtils.formatTemperature(mContext, temp));
        holder.tempView.setContentDescription(tempA11y);

    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (mUseTodayLayout && position == 0) {
            return NOW_VIEW_TYPE;
        } else {
            return FUTURE_VIEW_TYPE;
        }
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView iconView;
        final TextView dateView;
        final TextView descriptionView;
        final TextView tempView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            iconView = (ImageView) itemView.findViewById(R.id.weather_icon);
            dateView = (TextView) itemView.findViewById(R.id.date);
            descriptionView = (TextView) itemView.findViewById(R.id.weatherDescription);
            tempView = (TextView) itemView.findViewById(R.id.temp);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            mClickHandler.onListWeatherItemClick(mCursor.getLong(WeatherEntry.INDEX_COLUMN_DATE));
        }
    }

}

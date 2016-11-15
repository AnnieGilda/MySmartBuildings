package com.giovannirizzotti.mysmartbuildings.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.giovannirizzotti.mysmartbuildings.R;
import com.giovannirizzotti.mysmartbuildings.REST.Pojo.Meter;
import com.giovannirizzotti.mysmartbuildings.Utils.RecyclerViewAnimation;
import com.giovannirizzotti.mysmartbuildings.Utils.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

public class MetersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final Context context;
    private final RecyclerViewClickListener listener;
    private final LayoutInflater mInflater;
    private final ItemFilter mFilter = new ItemFilter();
    private List<Meter> originalData = null;
    private List<Meter> filteredData = null;
    private int lastPosition = -1;
    private boolean isTablet = false;
    private int meterType = -1;

    public MetersAdapter(Context context, List<Meter> data, RecyclerViewClickListener listener, boolean isTablet, int meterType) {
        this.context = context;
        this.originalData = data;
        this.filteredData = data;
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
        this.isTablet = isTablet;
        this.meterType = meterType;
    }

    public void addItem(List<Meter> metersToAdd, int buildingNumber) {
        for (Meter meter : metersToAdd) {
            meter.setMeterName("B" + buildingNumber + " - " + meter.getMeterName());
            this.originalData.add(meter);
        }
    }

    public int getCount() {
        return filteredData.size();
    }

    public Meter getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_meter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        Meter meter = filteredData.get(position);

        String title = meter.getMeterName();
        myViewHolder.textTitle.setText(title);
        myViewHolder.textValue.setText(meter.getValue());

        if (meterType == context.getResources().getInteger(R.integer.water_meter))
            myViewHolder.imageMeter.setImageDrawable(context.getResources().getDrawable(R.drawable.water, null));
        else if (meterType == context.getResources().getInteger(R.integer.gas_meter))
            myViewHolder.imageMeter.setImageDrawable(context.getResources().getDrawable(R.drawable.gas, null));
        else if (meterType == context.getResources().getInteger(R.integer.electricity_meter))
            myViewHolder.imageMeter.setImageDrawable(context.getResources().getDrawable(R.drawable.electricity, null));

        if (!isTablet) {
            if (position > lastPosition)
                new RecyclerViewAnimation().animate(holder, true);
            else
                new RecyclerViewAnimation().animate(holder, false);
        }
        lastPosition = position;
    }


    public Filter getFilter() {
        return mFilter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView textTitle;
        final TextView textValue;
        final ImageView imageMeter;

        public MyViewHolder(View view) {
            super(view);
            textTitle = (TextView) view.findViewById(R.id.tTitle);
            textValue = (TextView) view.findViewById(R.id.tValue);
            imageMeter = (ImageView) view.findViewById(R.id.imageMeter);

            textTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_bold)));
            textValue.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_normal)));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onRowClicked(getAdapterPosition());
                }
            });
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            final ArrayList<Meter> nlist = new ArrayList<>();

            String name;
            String data1;

            for (Meter meter : originalData) {
                name = meter.getMeterName().toLowerCase();
                data1 = meter.getValue().toLowerCase();

                if (name.contains(filterString) || data1.contains(filterString))
                    nlist.add(meter);
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Meter>) results.values;
            notifyDataSetChanged();
        }
    }
}
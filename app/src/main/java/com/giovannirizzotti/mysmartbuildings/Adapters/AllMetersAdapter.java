package com.giovannirizzotti.mysmartbuildings.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.giovannirizzotti.mysmartbuildings.R;
import com.giovannirizzotti.mysmartbuildings.REST.Pojo.Meter;
import com.giovannirizzotti.mysmartbuildings.Utils.RecyclerViewAnimation;
import com.giovannirizzotti.mysmartbuildings.Utils.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class AllMetersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final Context context;
    private final RecyclerViewClickListener listener;
    private final LayoutInflater mInflater;
    private final ItemFilter mFilter = new ItemFilter();
    private List<List<Meter>> originalData = null;
    private List<List<Meter>> filteredData = null;
    private int lastPosition = -1;
    private int originalSize = 0;
    private boolean isTablet = false;

    public AllMetersAdapter(Context context, List<List<Meter>> data, RecyclerViewClickListener listener, boolean isTablet) {
        this.context = context;
        addDummiesValues(data);
        this.originalData = data;
        this.filteredData = data;
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
        this.isTablet = isTablet;
        this.originalSize = originalData.size();
    }

    private void addDummiesValues(List<List<Meter>> listToModify) {
        List<Meter> waterList = listToModify.get(0);
        List<Meter> gasList = listToModify.get(1);
        List<Meter> electricityList = listToModify.get(2);

        int maxSize = waterList.size();
        if (gasList.size() > maxSize)
            maxSize = gasList.size();
        if (electricityList.size() > maxSize)
            maxSize = electricityList.size();

        if (waterList.size() < maxSize) {
            int iterations = maxSize - waterList.size();
            for (int i=0; i<iterations; i++)
                waterList.add(new Meter("", ""));
        }

        if (gasList.size() < maxSize) {
            int iterations = maxSize - gasList.size();
            for (int i=0; i<iterations; i++)
                gasList.add(new Meter("", ""));
        }

        if (electricityList.size() < maxSize) {
            int iterations = maxSize - electricityList.size();
            for (int i=0; i<iterations; i++)
                electricityList.add(new Meter("", ""));
        }
    }

    public void addItem(List<List<Meter>> metersToAdd, int buildingNumber) {
       addDummiesValues(metersToAdd);
        for (int i=0; i<metersToAdd.size(); i++) {
            List<Meter> newMeterList = metersToAdd.get(i);
            List<Meter> oldMeterList = originalData.get(i);
            for (Meter meter : newMeterList) {
                meter.setMeterName("B" + buildingNumber + " - " + meter.getMeterName());
                oldMeterList.add(meter);
            }
        }
    }

    public int getCount() {
        return filteredData.get(0).size();
    }

    public Meter getItem(int position) {
        return filteredData.get(0).get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return filteredData.get(0).size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_meters, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        List<Meter> waterList = filteredData.get(0);
        List<Meter> gasList = filteredData.get(1);
        List<Meter> electricityList = filteredData.get(2);

        Meter waterMeter = waterList.size() >= position+1 ? waterList.get(position) : new Meter("", "");
        Meter gasMeter = gasList.size() >= position+1 ? gasList.get(position) : new Meter("", "");
        Meter electricityMeter = electricityList.size() >= position+1 ? electricityList.get(position) : new Meter("", "");

        String title = waterMeter != null ? waterMeter.getMeterName() : "Meters";
        if (title.contains("-"))
            title = title.substring(0, title.indexOf('-') + 1) + " Meters " + title.substring(title.length()-2, title.length());
        else
            title = "B1 - Meters " + (position + 1);
        myViewHolder.textTitle.setText(title);

        if (waterMeter.getValue().length() > 0) {
            myViewHolder.waterIcon.setVisibility(View.VISIBLE);
            myViewHolder.textWater.setVisibility(View.VISIBLE);
            myViewHolder.textWater.setText(waterMeter.getValue());
        }
        else {
            myViewHolder.textWater.setVisibility(GONE);
            myViewHolder.waterIcon.setVisibility(GONE);
        }
        if (gasMeter.getValue().length() > 0) {
            myViewHolder.gasIcon.setVisibility(View.VISIBLE);
            myViewHolder.textGas.setVisibility(View.VISIBLE);
            myViewHolder.textGas.setText(gasMeter.getValue());
        }
        else {
            myViewHolder.textGas.setVisibility(GONE);
            myViewHolder.gasIcon.setVisibility(GONE);
        }
        if (electricityMeter.getValue().length() > 0) {
            myViewHolder.electricityIcon.setVisibility(View.VISIBLE);
            myViewHolder.textElectricity.setVisibility(View.VISIBLE);
            myViewHolder.textElectricity.setText(electricityMeter.getValue());
        }
        else {
            myViewHolder.textElectricity.setVisibility(GONE);
            myViewHolder.electricityIcon.setVisibility(GONE);
        }
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
        final TextView textWater;
        final TextView textGas;
        final TextView textElectricity;
        final View waterIcon;
        final View gasIcon;
        final View electricityIcon;

        public MyViewHolder(View view) {
            super(view);
            textTitle = (TextView) view.findViewById(R.id.tTitle);
            textWater = (TextView) view.findViewById(R.id.tValueWater);
            textGas = (TextView) view.findViewById(R.id.tValueGas);
            textElectricity = (TextView) view.findViewById(R.id.tValueElectricity);
            waterIcon = view.findViewById(R.id.waterIcon);
            gasIcon = view.findViewById(R.id.gasIcon);
            electricityIcon = view.findViewById(R.id.electricityIcon);

            textTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_bold)));
            textWater.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_normal)));
            textGas.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_normal)));
            textElectricity.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_normal)));

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

            final ArrayList<List<Meter>> nlist = new ArrayList<>();
            final ArrayList<Meter> waterList = new ArrayList<>();
            final ArrayList<Meter> gasList = new ArrayList<>();
            final ArrayList<Meter> electricityList = new ArrayList<>();

            String name;
            String water;
            String gas;
            String electricity;

            List<Meter> waterMeterOriginalList = originalData.get(0);
            List<Meter> gasMeterOriginalList = originalData.get(1);
            List<Meter> electricityMeterOriginalList = originalData.get(2);

            for (int i = 0; i < waterMeterOriginalList.size(); i++) {
                Meter waterMeter = waterMeterOriginalList.get(i);
                Meter gasMeter = gasMeterOriginalList.get(i);
                Meter electricityMeter = electricityMeterOriginalList.get(i);
                name = waterMeter.getMeterName().toLowerCase();
                water = waterMeter.getValue().toLowerCase();
                gas = gasMeter.getValue().toLowerCase();
                electricity = electricityMeter.getValue().toLowerCase();

                if (name.contains(filterString) || water.contains(filterString) || gas.contains(filterString) || electricity.contains(filterString)) {
                    waterList.add(waterMeter);
                    gasList.add(gasMeter);
                    electricityList.add(electricityMeter);
                }
            }

            nlist.add(waterList);
            nlist.add(gasList);
            nlist.add(electricityList);

            results.values = nlist;
            results.count = waterList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<List<Meter>>) results.values;
            notifyDataSetChanged();
        }
    }
}
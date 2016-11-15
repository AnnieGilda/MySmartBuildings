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

import com.giovannirizzotti.mysmartbuildings.Item.Building;
import com.giovannirizzotti.mysmartbuildings.R;
import com.giovannirizzotti.mysmartbuildings.Utils.RecyclerViewAnimation;
import com.giovannirizzotti.mysmartbuildings.Utils.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;


public class BuildingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final Context context;
    private final RecyclerViewClickListener listener;
    private final LayoutInflater mInflater;
    private final ItemFilter mFilter = new ItemFilter();
    private List<Building> originalData = null;
    private List<Building> filteredData = null;
    private int lastPosition = -1;
    private boolean isTablet = false;

    public BuildingsAdapter(Context context, List<Building> data, RecyclerViewClickListener listener, boolean isTablet) {
        this.context = context;
        this.originalData = data;
        this.filteredData = data;
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
        this.isTablet = isTablet;
    }

    public int getCount() {
        return filteredData.size();
    }

    public Building getItem(int position) {
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
        View view = mInflater.inflate(R.layout.item_building, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        Building building = filteredData.get(position);

        myViewHolder.textName.setText(building.getName());
        myViewHolder.textCoapPort.setText(building.getCoapPort());

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
        final TextView textName;
        final TextView textCoapPort;

        public MyViewHolder(View view) {
            super(view);
            textName = (TextView) view.findViewById(R.id.tTitolo);
            textCoapPort = (TextView) view.findViewById(R.id.tCoapPort);

            textName.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_normal)));
            textCoapPort.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_bold_italic)));

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

            final ArrayList<Building> nlist = new ArrayList<>();

            String name;
            String coapPort;

            for (Building building : originalData) {
                name = building.getName().toLowerCase();
                coapPort = building.getCoapPort().toLowerCase();

                if (name.contains(filterString) || coapPort.contains(filterString))
                    nlist.add(building);
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Building>) results.values;
            notifyDataSetChanged();
        }
    }
}
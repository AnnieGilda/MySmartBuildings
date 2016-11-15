package com.giovannirizzotti.mysmartbuildings.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giovannirizzotti.mysmartbuildings.Adapters.AllMetersAdapter;
import com.giovannirizzotti.mysmartbuildings.Adapters.MetersAdapter;
import com.giovannirizzotti.mysmartbuildings.MainActivity;
import com.giovannirizzotti.mysmartbuildings.R;
import com.giovannirizzotti.mysmartbuildings.REST.MyRestAdapter;
import com.giovannirizzotti.mysmartbuildings.REST.Pojo.AllMetersReceiver;
import com.giovannirizzotti.mysmartbuildings.REST.Pojo.Meter;
import com.giovannirizzotti.mysmartbuildings.REST.Pojo.MeterReceiver;
import com.giovannirizzotti.mysmartbuildings.Utils.FragmentType;
import com.giovannirizzotti.mysmartbuildings.Utils.RecyclerViewClickListener;
import com.giovannirizzotti.mysmartbuildings.Utils.ServerConfiguration;

import java.util.ArrayList;
import java.util.List;

import pl.tajchert.sample.DotsTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMeters extends Fragment {
    private RecyclerViewClickListener recyclerViewClickListener = null;
    private MetersAdapter adapter = null;
    private AllMetersAdapter allMetersadapter = null;

    private boolean isWorking = false;
    private boolean interrupted = false;
    private int meterType = -1;
    private boolean isAllMeters = false;
    private boolean isAllBuildings = false;
    private String coapPort;

    private RecyclerView recyclerView = null;
    private View rootView = null;
    private DotsTextView waitingDots = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    private ServerConfiguration serverConfiguration;
    private MyRestAdapter myRestAdapter;
    private Call<MeterReceiver> call;
    private Call<AllMetersReceiver> callAllMeters;

    private void Refresh() {
       if (!isWorking) {
           if (isAllBuildings) {
               for (int i=0; i<serverConfiguration.getServerNumber(); i++) {
                   String newCoapPort = Integer.parseInt(serverConfiguration.getCoapServerPort()) + i + "";
                   myRestAdapter = new MyRestAdapter(serverConfiguration.getProxyServerIP(), serverConfiguration.getProxyServerPort(), serverConfiguration.getCoapServerIP(), newCoapPort,  true);
                   checkWhatToStart(i+1);
                   try {
                       Thread.sleep(100);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
           }
           else {
               myRestAdapter = new MyRestAdapter(serverConfiguration.getProxyServerIP(), serverConfiguration.getProxyServerPort(), serverConfiguration.getCoapServerIP(), coapPort,  true);
               checkWhatToStart(0);
           }
        }
    }

    public void Filter(String filter) {
        if (!isAllMeters) {
            if (adapter != null && !isWorking)
                adapter.getFilter().filter(filter);
        }
        else {
            if (allMetersadapter != null && !isWorking)
                allMetersadapter.getFilter().filter(filter);
        }
    }

    private void AllMeters(final int buildingNumber) {
        isAllMeters = true;
        Init();
        callAllMeters.enqueue(new Callback<AllMetersReceiver>() {
            @Override
            public void onResponse(Call<AllMetersReceiver> call, Response<AllMetersReceiver> response) {
                if (response.isSuccessful()) {
                    int responseCode = response.code();
                    if (responseCode == getResources().getInteger(R.integer.response_ok)) {
                        AllMetersReceiver results = response.body();
                        if (results != null) {
                            List<List<Meter>> allMetersList = new ArrayList<>();
                            allMetersList.add(results.getWater());
                            allMetersList.add(results.getGas());
                            allMetersList.add(results.getElectricity());
                            if (allMetersList.size() == 3) {
                                if (allMetersadapter != null && isAllBuildings) { //Adapter not empty, this is not the first building
                                    allMetersadapter.addItem(allMetersList, buildingNumber);
                                    allMetersadapter.notifyDataSetChanged();
                                    allMetersadapter.notifyItemRangeInserted(allMetersadapter.getCount(), results.getWater().size());
                                }
                                else {
                                    allMetersadapter = new AllMetersAdapter(getActivity(), allMetersList, recyclerViewClickListener, ((MainActivity) getActivity()).getIsTablet());
                                    recyclerView.setAdapter(allMetersadapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            } else
                                recyclerView.setAdapter(null);
                            isWorking = false;
                            waitingDots.hideAndStop();
                            waitingDots.setVisibility(View.GONE);
                            swipeRefreshLayout.setEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AllMetersReceiver> call, Throwable t) {
                isWorking = false;
                waitingDots.hideAndStop();
                waitingDots.setVisibility(View.GONE);
                swipeRefreshLayout.setEnabled(true);
                Log.e("RESPONSE FAILED", t.getCause() + "\n" + t.getStackTrace() + "\n" + (t.getMessage() != null ? t.getMessage() : ""));
                DialogError(t.getMessage() != null ? t.getMessage() : getString(R.string.error));
            }
        });
    }

    private void Init() {
        isWorking = true;
        swipeRefreshLayout.setEnabled(false);
        waitingDots.setVisibility(View.VISIBLE);
        waitingDots.showAndPlay();
    }

    private void Meters(final int buildingNumber) {
        isAllMeters = false;
        Init();
        call.enqueue(new Callback<MeterReceiver>() {
            @Override
            public void onResponse(Call<MeterReceiver> call, Response<MeterReceiver> response) {
                if (response.isSuccessful()) {
                    int responseCode = response.code();
                    if (responseCode == getResources().getInteger(R.integer.response_ok)) {
                        MeterReceiver results = response.body();
                        if (results != null) {
                            List<Meter> meters = results.getOutput();
                            if (meters.size() > 0) {
                                if (adapter != null && isAllBuildings) { //Adapter not empty, this is not the first building
                                    adapter.addItem(meters, buildingNumber);
                                    adapter.notifyDataSetChanged();
                                    adapter.notifyItemRangeInserted(adapter.getCount(), meters.size());
                                }
                                else {
                                    adapter = new MetersAdapter(getActivity(), meters, recyclerViewClickListener, ((MainActivity) getActivity()).getIsTablet(), meterType);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            } else
                                recyclerView.setAdapter(null);
                            isWorking = false;
                            waitingDots.hideAndStop();
                            waitingDots.setVisibility(View.GONE);
                            swipeRefreshLayout.setEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MeterReceiver> call, Throwable t) {
                isWorking = false;
                waitingDots.hideAndStop();
                waitingDots.setVisibility(View.GONE);
                swipeRefreshLayout.setEnabled(true);
                Log.e("RESPONSE FAILED", t.getCause() + "\n" + t.getStackTrace() + "\n" + (t.getMessage() != null ? t.getMessage() : ""));
                DialogError(t.getMessage() != null ? t.getMessage() : getString(R.string.error));
            }
        });
    }

    private void DialogError(String message) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View mView = layoutInflaterAndroid.inflate(R.layout.error_dialog, null);
        ((TextView)mView.findViewById(R.id.tMessage)).setText(Html.fromHtml(message));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView)
                .setCancelable(false)
                .setTitle(getString(R.string.error))
                .setPositiveButton("OK", null).create().show();
    }

    private void checkWhatToStart(int buildingNumber) {
        if (meterType == getResources().getInteger(R.integer.all_meters)) {
            callAllMeters = myRestAdapter.getApiService().allMeters();
            AllMeters(buildingNumber);
            return;
        }
        if (meterType == getResources().getInteger(R.integer.water_meter))
            call = myRestAdapter.getApiService().waterMeter();
        else if (meterType == getResources().getInteger(R.integer.gas_meter))
            call = myRestAdapter.getApiService().gasMeter();
        else if (meterType == getResources().getInteger(R.integer.electricity_meter))
            call = myRestAdapter.getApiService().electricityMeter();
        Meters(buildingNumber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Filter("");
        ((MainActivity) getActivity()).setOpenedFragment(FragmentType.METERS);
        ((MainActivity) getActivity()).HideSearchBar();
        ((MainActivity) getActivity()).MenuFind(true);
        ((MainActivity) getActivity()).MenuReload();

        Bundle bundle = this.getArguments();
        meterType = bundle.getInt(getString(R.string.bundle_meter_type), -1);
        coapPort = bundle.getString(getString(R.string.bundle_coap_port), "");
        isAllBuildings = bundle.getBoolean(getString(R.string.bundle_is_all_buildings));
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(bundle.getString(getString(R.string.bundle_building_name), ""));
        if (meterType >= 0) {
            if (rootView == null) {
                rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
                recyclerViewClickListener = new RecyclerViewClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                       /* final Building building = ((BuildingsAdapter) recyclerView.getAdapter()).getItem(position);
                        OnClick(building);*/
                    }
                };
                waitingDots = (DotsTextView) rootView.findViewById(R.id.dots);
                swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.listViewSwipeRefresh);
                swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefreshLayout.setRefreshing(false);
                        Refresh();
                    }
                });

                recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewNews);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                serverConfiguration = ((MainActivity) getActivity()).getServerConfiguration();

                if (isAllBuildings) {
                    for (int i=0; i<serverConfiguration.getServerNumber(); i++) {
                        String newCoapPort = Integer.parseInt(coapPort) + i + "";
                        myRestAdapter = new MyRestAdapter(serverConfiguration.getProxyServerIP(), serverConfiguration.getProxyServerPort(), serverConfiguration.getCoapServerIP(), newCoapPort,  true);
                        checkWhatToStart(i+1);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    myRestAdapter = new MyRestAdapter(serverConfiguration.getProxyServerIP(), serverConfiguration.getProxyServerPort(), serverConfiguration.getCoapServerIP(), coapPort,  true);
                    checkWhatToStart(0);
                }
            }
        }
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isWorking) {
            isWorking = false;
            waitingDots.hideAndStop();
            waitingDots.setVisibility(View.GONE);
            interrupted = true;
        }
    }

    @Override
    public void onDetach() {
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(getString(R.string.app_name));
        super.onDetach();
    }
}
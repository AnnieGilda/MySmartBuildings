package com.giovannirizzotti.mysmartbuildings.Fragments;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.giovannirizzotti.mysmartbuildings.Adapters.BuildingsAdapter;
import com.giovannirizzotti.mysmartbuildings.Item.Building;
import com.giovannirizzotti.mysmartbuildings.MainActivity;
import com.giovannirizzotti.mysmartbuildings.R;
import com.giovannirizzotti.mysmartbuildings.REST.MyRestAdapter;
import com.giovannirizzotti.mysmartbuildings.Utils.FragmentType;
import com.giovannirizzotti.mysmartbuildings.Utils.Functions;
import com.giovannirizzotti.mysmartbuildings.Utils.RecyclerViewClickListener;
import com.giovannirizzotti.mysmartbuildings.Utils.ServerConfiguration;

import java.util.ArrayList;
import java.util.List;

import pl.tajchert.sample.DotsTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentBuildings extends Fragment {
    private RecyclerViewClickListener recyclerViewClickListener = null;
    private List<Building> buildingsList;
    private BuildingsAdapter adapter = null;

    private boolean isWorking = false;
    private boolean interrupted = false;

    private RecyclerView recyclerView = null;
    private View rootView = null;
    private DotsTextView waitingDots = null;
    private TextView tEmpty = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    private ServerConfiguration serverConfiguration;

    private DataLoading loadData = new DataLoading();

    private void Refresh() {
        if (!isWorking) {
            loadData.cancel(true);
            loadData = new DataLoading();
            loadData.execute();
        }
    }

    public void Filter(String filter) {
        if (adapter != null && !isWorking)
            adapter.getFilter().filter(filter);
    }

    private void DialogOperationSuccess(String title, int img, String buildingName) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View mView = layoutInflaterAndroid.inflate(R.layout.operation_success_dialog, null);
        ((TextView)mView.findViewById(R.id.tTitle)).setText(Html.fromHtml(title));
        ((ImageView)mView.findViewById(R.id.imageOperation)).setImageDrawable(getResources().getDrawable(img));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView)
                .setCancelable(false)
                .setTitle(buildingName)
                .setPositiveButton("OK", null).create().show();
        Functions.writeLog();
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

    private void OnClick(Building building, final String coapPort) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View mView = layoutInflaterAndroid.inflate(R.layout.rest_operation_dialog, null);
        final Button buttonSetLighting = (Button) mView.findViewById(R.id.bSetLighting);
        final Button buttonOpenDoors = (Button) mView.findViewById(R.id.bOpenDoors);
        final Button buttonWaterMeter = (Button) mView.findViewById(R.id.bWaterMeter);
        final Button buttonGasMeter = (Button) mView.findViewById(R.id.bGasMeter);
        final Button buttonElectricityMeter = (Button) mView.findViewById(R.id.bElectricityMeter);
        final Button buttonAllMeters = (Button) mView.findViewById(R.id.bAllMeters);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView)
                .setCancelable(true)
                .setNegativeButton(getString(R.string.cancel), null);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final String buildingName = building.getName();

        buttonSetLighting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                SetLighting(coapPort, buildingName);
            }
        });

        buttonOpenDoors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                OpenDoors(coapPort, buildingName);
            }
        });

        buttonWaterMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                OpenMetersList(getResources().getInteger(R.integer.water_meter), coapPort, buildingName);
            }
        });

        buttonGasMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                OpenMetersList(getResources().getInteger(R.integer.gas_meter), coapPort, buildingName);
            }
        });

        buttonElectricityMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                OpenMetersList(getResources().getInteger(R.integer.electricity_meter),coapPort, buildingName);
            }
        });

        buttonAllMeters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                OpenMetersList(getResources().getInteger(R.integer.all_meters),coapPort, buildingName);
            }
        });
    }

    private void OpenMetersList(int typeMeter, String coapPort, String buildingName) {
        FragmentMeters fragment = new FragmentMeters();
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.bundle_meter_type),typeMeter);
        bundle.putString(getString(R.string.bundle_coap_port), coapPort);
        bundle.putString(getString(R.string.bundle_building_name), buildingName);
        bundle.putBoolean(getString(R.string.bundle_is_all_buildings),buildingName.equals("All Buildings"));
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.activity_open_translate, 0, 0, R.anim.activity_close_translate);
        fragmentTransaction.replace(R.id.content_main_activity_smart, fragment, getString(R.string.tag_meters_fragment));
        fragmentTransaction.addToBackStack(null).commit();
    }

    private void OpenDoors(String coapPort, final String buildingName) {
        serverConfiguration = ((MainActivity) getActivity()).getServerConfiguration();
        final MyRestAdapter myRestAdapter = new MyRestAdapter(serverConfiguration.getProxyServerIP(), serverConfiguration.getProxyServerPort(), serverConfiguration.getCoapServerIP(), coapPort, false);
        Call<String> call = myRestAdapter.getApiService().openDoors();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    int responseCode = response.code();
                    if (responseCode == getResources().getInteger(R.integer.response_ok)) {
                        String results = response.body();
                        if (results != null)
                            DialogOperationSuccess(results, R.drawable.door, buildingName);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                isWorking = false;
                waitingDots.hideAndStop();
                waitingDots.setVisibility(View.GONE);
                swipeRefreshLayout.setEnabled(true);
                Log.e("RESPONSE FAILED", t.getCause() + "\n" + t.getStackTrace() + "\n" + (t.getMessage() != null ? t.getMessage() : ""));
                DialogError(t.getMessage() != null ? t.getMessage() : getString(R.string.error));
            }
        });
    }

    private void SetLighting(String coapPort, final String buildingName) {
        serverConfiguration = ((MainActivity) getActivity()).getServerConfiguration();
        final MyRestAdapter myRestAdapter = new MyRestAdapter(serverConfiguration.getProxyServerIP(), serverConfiguration.getProxyServerPort(), serverConfiguration.getCoapServerIP(), coapPort, false);
        Call<String> call = myRestAdapter.getApiService().setLighting();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    int responseCode = response.code();
                    if (responseCode == getResources().getInteger(R.integer.response_ok)) {
                        String results = response.body();
                        if (results != null)
                            DialogOperationSuccess(results, R.drawable.lamp, buildingName);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                isWorking = false;
                waitingDots.hideAndStop();
                waitingDots.setVisibility(View.GONE);
                swipeRefreshLayout.setEnabled(true);
                Log.e("RESPONSE FAILED", t.getCause() + "\n" + t.getStackTrace() + "\n" + (t.getMessage() != null ? t.getMessage() : ""));
                DialogError(t.getMessage() != null ? t.getMessage() : getString(R.string.error));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Filter("");
        ((MainActivity) getActivity()).setOpenedFragment(FragmentType.BUILDINGS_LIST);
        ((MainActivity) getActivity()).HideSearchBar();
        ((MainActivity) getActivity()).MenuFind(true);
        ((MainActivity) getActivity()).MenuReload();

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
            recyclerViewClickListener = new RecyclerViewClickListener() {
                @Override
                public void onRowClicked(int position) {
                    final Building building = ((BuildingsAdapter) recyclerView.getAdapter()).getItem(position);
                    OnClick(building, building.getCoapPort());
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
            tEmpty = (TextView) rootView.findViewById(R.id.tNessunaInfo);
            tEmpty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Refresh();
                }
            });

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewNews);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            loadData.execute();
        }
        return rootView;
    }

    @Override
    public void onPause() {
        loadData.cancel(true);
        super.onPause();
        if (isWorking) {
            isWorking = false;
            waitingDots.hideAndStop();
            waitingDots.setVisibility(View.GONE);
            interrupted = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (interrupted) {
            Refresh();
            interrupted = false;
        }
    }

private class DataLoading extends AsyncTask<Void, Void, Void> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isWorking = true;
        swipeRefreshLayout.setEnabled(false);
        waitingDots.setVisibility(View.VISIBLE);
        waitingDots.showAndPlay();
        tEmpty.setVisibility(View.GONE);
        buildingsList = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            serverConfiguration = ((MainActivity) getActivity()).getServerConfiguration();
            if (serverConfiguration.getServerNumber() > 1)
                buildingsList.add(new Building("All Buildings", serverConfiguration.getCoapServerPort(), "VPN 1"));
            for (int i = 1; i < serverConfiguration.getServerNumber() +1; i++)
                buildingsList.add(new Building("Building " + i, Integer.parseInt(serverConfiguration.getCoapServerPort()) + (i-1) + "", "VPN 1"));
        } catch (Exception e) {
            Log.e("ERROR BUILDINGS LIST", e.getMessage() != null ? e.getMessage() : "");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        if (buildingsList.size() > 0) {
            adapter = new BuildingsAdapter(getActivity(), buildingsList, recyclerViewClickListener, ((MainActivity) getActivity()).getIsTablet());
            recyclerView.setAdapter(adapter);

        } else {
            tEmpty.setText(getString(R.string.no_info));
            tEmpty.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.font_bold)));
            tEmpty.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(null);
        }

        isWorking = false;
        waitingDots.hideAndStop();
        waitingDots.setVisibility(View.GONE);
        swipeRefreshLayout.setEnabled(true);
    }
}
}
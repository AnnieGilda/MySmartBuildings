package com.giovannirizzotti.mysmartbuildings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.giovannirizzotti.mysmartbuildings.Fragments.FragmentBuildings;
import com.giovannirizzotti.mysmartbuildings.Fragments.FragmentMeters;
import com.giovannirizzotti.mysmartbuildings.Utils.FragmentType;
import com.giovannirizzotti.mysmartbuildings.Utils.Functions;
import com.giovannirizzotti.mysmartbuildings.Utils.ServerConfiguration;

import java.util.HashMap;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final Pattern PARTIAl_IP_ADDRESS =  Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}"+
                    "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$");
    private static final Pattern PORT_NUMBER =  Pattern.compile("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$");
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private final HashMap<String, Boolean> hashMapMenu = new HashMap<>();
    private final TextWatcher ipWatcher = new TextWatcher() {
        private String mPreviousText = "";

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(PARTIAl_IP_ADDRESS.matcher(s).matches()) {
                mPreviousText = s.toString();
            } else {
                s.replace(0, s.length(), mPreviousText);
            }
        }
    };
    private final TextWatcher portWatcher = new TextWatcher() {
        private String mPreviousText = "";

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                if (PORT_NUMBER.matcher(s).matches()) {
                    mPreviousText = s.toString();
                } else {
                    s.replace(0, s.length(), mPreviousText);
                }
            }
        }
    };
    private SearchView searchView = null;
    private FragmentType openedFragment;
    private Menu menu = null;
    private boolean isTablet = false;
    private ServerConfiguration serverConfiguration;

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public void setOpenedFragment(FragmentType fragmentType) {
        Log.d("OPENED FRAGMENT: ", fragmentType.toString());
        openedFragment = fragmentType;
    }

    public void HideSearchBar() {
        if (searchView != null)
            searchView.onActionViewCollapsed();
    }

    public void MenuFind(Boolean tf) {
        hashMapMenu.put("Find", tf);
    }

    public void MenuReload() {
        if (menu != null)
            onPrepareOptionsMenu(menu);
    }

    private void ServerConfiguration() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.server_configuration_dialog, null);
        final EditText textProxyIP = (EditText) mView.findViewById(R.id.textProxyIP);
        final EditText textProxyPort = (EditText) mView.findViewById(R.id.textProxyPort);
        final EditText textCoapIP = (EditText) mView.findViewById(R.id.textCoapIP);
        final EditText textCoapPort = (EditText) mView.findViewById(R.id.textCoapPort);
        final EditText textServerNumber = (EditText) mView.findViewById(R.id.textServerNumber);

        textProxyIP.addTextChangedListener(ipWatcher);
        textProxyPort.addTextChangedListener(portWatcher);
        textCoapIP.addTextChangedListener(ipWatcher);
        textCoapPort.addTextChangedListener(portWatcher);

        textProxyIP.setText(serverConfiguration.getProxyServerIP());
        textProxyPort.setText(serverConfiguration.getProxyServerPort());
        textCoapIP.setText(serverConfiguration.getCoapServerIP());
        textCoapPort.setText(serverConfiguration.getCoapServerPort());
        textServerNumber.setText(serverConfiguration.getServerNumber() + "");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mView)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean wantToCloseDialog = true;
                String proxyServerIp = textProxyIP.getText().toString().trim();
                if (!Patterns.IP_ADDRESS.matcher(proxyServerIp).matches())
                    Toast.makeText(MainActivity.this, "Enter a valid IP address", Toast.LENGTH_LONG).show();
                String coapServerIp = textCoapIP.getText().toString().trim();
                String proxyServerPort = textProxyPort.getText().toString().trim();
                String coapServerPort = textCoapPort.getText().toString().trim();
                int serverNumber = Integer.parseInt(textServerNumber.getText().toString().trim());

                if (coapServerIp.length() == 0 || proxyServerIp.length() == 0 || proxyServerPort.length() == 0 || coapServerPort.length() == 0 || serverNumber < 1) {
                    wantToCloseDialog = false;
                    Toast.makeText(MainActivity.this, getString(R.string.configuration_parameters_mandatory), Toast.LENGTH_LONG).show();
                }

                serverConfiguration.setAll(proxyServerIp, proxyServerPort, coapServerIp, coapServerPort, serverNumber);

                if(wantToCloseDialog) {
                    OpenBuildingsList();
                    dialog.dismiss();
                }
            }
        });
    }

    public boolean getIsTablet() {
        return this.isTablet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_smart);

        serverConfiguration = new ServerConfiguration(this); //Load server configuration

        /******************CHECK IF IS A TABLET*********************************/
        DisplayMetrics met = new DisplayMetrics();
        MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(met);// get display metrics object
        int size = (int) Math.sqrt(((met.widthPixels / met.xdpi) * (met.widthPixels / met.xdpi)) + ((met.heightPixels / met.ydpi) * (met.heightPixels / met.ydpi)));
        if (size >= 6)
            isTablet = true;
        /*********************************************************************/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Functions.verifyStoragePermissions(this);
        if (serverConfiguration.getRequireConfiguration())
            ServerConfiguration();
        else
            OpenBuildingsList();
    }

    private void OpenBuildingsList() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentBuildings fragmentBuildings = new FragmentBuildings();
        fragmentTransaction.setCustomAnimations(R.anim.activity_open_translate, 0, 0, R.anim.activity_close_translate);
        fragmentTransaction.replace(R.id.content_main_activity_smart, fragmentBuildings, getString(R.string.tag_buildings_list));
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_activity_smart, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_find));
        searchView.setQueryHint(Html.fromHtml("<font color = " + getResources().getColor(R.color.tabSelectorDisabled) + ">" + getString(R.string.text_search) + "</font>"));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (openedFragment == FragmentType.BUILDINGS_LIST) {
                        FragmentBuildings fragmentBuildings = (FragmentBuildings) getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_buildings_list));
                        if (fragmentBuildings != null && fragmentBuildings.isVisible())
                            fragmentBuildings.Filter(newText);
                    }
                    else if (openedFragment == FragmentType.METERS) {
                        FragmentMeters fragmentMeters = (FragmentMeters) getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_meters_fragment));
                        if (fragmentMeters != null && fragmentMeters.isVisible())
                            fragmentMeters.Filter(newText);
                    }
                    return true;
                } catch (Exception exception) {
                    return false;
                }
            }
        });
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_buildings_list)
            OpenBuildingsList();
        else if (id == R.id.nav_server_configuration)
            ServerConfiguration();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (hashMapMenu.containsKey("Find"))
            menu.findItem(R.id.menu_find).setVisible(hashMapMenu.get("Find"));


        return true;
    }
}

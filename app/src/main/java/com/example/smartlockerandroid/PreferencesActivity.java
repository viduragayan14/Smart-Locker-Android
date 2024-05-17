package com.example.smartlockerandroid;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.databinding.ActivityPreferencesRBinding;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;
import com.google.android.material.navigation.NavigationView;

public class PreferencesActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {

    public ActionBarDrawerToggle actionBarDrawerToggle;
    NavController navController;
    private ActivityPreferencesRBinding binding;
    UdpServerThread bh;
    private PreferenceViewModel preferenceViewModel;
    private Preference preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        binding = ActivityPreferencesRBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bh = UdpServerThread.getInstance(PreferencesActivity.this, this);
        bh.setViewModelStoreOwner(PreferencesActivity.this,this);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_preferences);
        Toolbar toolBar = binding.appBarPreferences.toolbarSettings;
        setSupportActionBar(toolBar);

        DrawerLayout drawer = binding.drawerLayout;
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        drawer.openDrawer(GravityCompat.START);
        NavigationView navigationView = binding.navView;

        TextView title = toolBar.findViewById(R.id.toolbar_title);
        TextView button = toolBar.findViewById(R.id.custom_btn);
        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        preference = preferenceViewModel.getPreference();

        LinearLayout users = navigationView.findViewById(R.id.users);
        LinearLayout orderApps = navigationView.findViewById(R.id.order_apps);
        LinearLayout bayManagement = navigationView.findViewById(R.id.bay_management);
        LinearLayout bayAlerts = navigationView.findViewById(R.id.bay_alerts);
        LinearLayout system = navigationView.findViewById(R.id.system);
        LinearLayout backLoading = navigationView.findViewById(R.id.back_loading);
        LinearLayout history = navigationView.findViewById(R.id.history);
        LinearLayout home = navigationView.findViewById(R.id.home);

        TextView usersTxt = navigationView.findViewById(R.id.txt_users);
        TextView orderAppsTxt = navigationView.findViewById(R.id.txt_order_apps);
        TextView bayManagementTxt = navigationView.findViewById(R.id.txt_bay_management);
        TextView bayAlertsTxt = navigationView.findViewById(R.id.txt_bay_alerts);
        TextView systemTxt = navigationView.findViewById(R.id.txt_system);
        TextView backLoadingTxt = navigationView.findViewById(R.id.txt_back_loading);
        TextView historyTxt = navigationView.findViewById(R.id.txt_history);

        ImageView usersIm = navigationView.findViewById(R.id.im_users);
        ImageView orderAppsIm = navigationView.findViewById(R.id.im_order_apps);
        ImageView bayManagementIm = navigationView.findViewById(R.id.im_bay_management);
        ImageView bayAlertsIm = navigationView.findViewById(R.id.im_bay_alerts);
        ImageView systemIm = navigationView.findViewById(R.id.im_system);
        ImageView backLoadingIm = navigationView.findViewById(R.id.im_back_loading);
        ImageView historyIm = navigationView.findViewById(R.id.im_history);

        home.setOnClickListener(view -> {
            if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                Intent intent = new Intent(PreferencesActivity.this, LoadingAreaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            } else {
                Intent intent = new Intent(PreferencesActivity.this, SelectCourierActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        users.setOnClickListener(view -> {

            usersTxt.setTextColor(getColor(R.color.orange));
            orderAppsTxt.setTextColor(getColor(R.color.black));
            bayManagementTxt.setTextColor(getColor(R.color.black));
            bayAlertsTxt.setTextColor(getColor(R.color.black));
            systemTxt.setTextColor(getColor(R.color.black));
            backLoadingTxt.setTextColor(getColor(R.color.black));
            historyTxt.setTextColor(getColor(R.color.black));

            usersIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.orange)));
            orderAppsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayManagementIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayAlertsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            systemIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            backLoadingIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            historyIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));

            users.setBackgroundColor(getColor(R.color.beige2));
            orderApps.setBackgroundColor(getColor(R.color.white));
            bayManagement.setBackgroundColor(getColor(R.color.white));
            bayAlerts.setBackgroundColor(getColor(R.color.white));
            system.setBackgroundColor(getColor(R.color.white));
            backLoading.setBackgroundColor(getColor(R.color.white));
            history.setBackgroundColor(getColor(R.color.white));
            navController.navigate(R.id.nav_user_settings);

            drawer.closeDrawers();

        });
        orderApps.setOnClickListener(view -> {
            usersTxt.setTextColor(getColor(R.color.black));
            orderAppsTxt.setTextColor(getColor(R.color.orange));
            bayManagementTxt.setTextColor(getColor(R.color.black));
            bayAlertsTxt.setTextColor(getColor(R.color.black));
            systemTxt.setTextColor(getColor(R.color.black));
            backLoadingTxt.setTextColor(getColor(R.color.black));
            historyTxt.setTextColor(getColor(R.color.black));

            usersIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            orderAppsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.orange)));
            bayManagementIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayAlertsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            systemIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            backLoadingIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            historyIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));

            users.setBackgroundColor(getColor(R.color.white));
            orderApps.setBackgroundColor(getColor(R.color.beige2));
            bayManagement.setBackgroundColor(getColor(R.color.white));
            bayAlerts.setBackgroundColor(getColor(R.color.white));
            system.setBackgroundColor(getColor(R.color.white));
            backLoading.setBackgroundColor(getColor(R.color.white));
            history.setBackgroundColor(getColor(R.color.white));
            navController.navigate(R.id.nav_courier_settings);

            drawer.closeDrawers();
        });
        bayManagement.setOnClickListener(view -> {
            usersTxt.setTextColor(getColor(R.color.black));
            orderAppsTxt.setTextColor(getColor(R.color.black));
            bayManagementTxt.setTextColor(getColor(R.color.orange));
            bayAlertsTxt.setTextColor(getColor(R.color.black));
            systemTxt.setTextColor(getColor(R.color.black));
            backLoadingTxt.setTextColor(getColor(R.color.black));
            historyTxt.setTextColor(getColor(R.color.black));

            usersIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            orderAppsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayManagementIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.orange)));
            bayAlertsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            systemIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            backLoadingIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            historyIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));

            users.setBackgroundColor(getColor(R.color.white));
            orderApps.setBackgroundColor(getColor(R.color.white));
            bayManagement.setBackgroundColor(getColor(R.color.beige2));
            bayAlerts.setBackgroundColor(getColor(R.color.white));
            system.setBackgroundColor(getColor(R.color.white));
            backLoading.setBackgroundColor(getColor(R.color.white));
            history.setBackgroundColor(getColor(R.color.white));
            navController.navigate(R.id.nav_bay_settings);

            drawer.closeDrawers();
        });
        bayAlerts.setOnClickListener(view -> {
            usersTxt.setTextColor(getColor(R.color.black));
            orderAppsTxt.setTextColor(getColor(R.color.black));
            bayManagementTxt.setTextColor(getColor(R.color.black));
            bayAlertsTxt.setTextColor(getColor(R.color.orange));
            systemTxt.setTextColor(getColor(R.color.black));
            backLoadingTxt.setTextColor(getColor(R.color.black));
            historyTxt.setTextColor(getColor(R.color.black));

            usersIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            orderAppsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayManagementIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayAlertsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.orange)));
            systemIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            backLoadingIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            historyIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));

            users.setBackgroundColor(getColor(R.color.white));
            orderApps.setBackgroundColor(getColor(R.color.white));
            bayManagement.setBackgroundColor(getColor(R.color.white));
            bayAlerts.setBackgroundColor(getColor(R.color.beige2));
            system.setBackgroundColor(getColor(R.color.white));
            backLoading.setBackgroundColor(getColor(R.color.white));
            history.setBackgroundColor(getColor(R.color.white));

            navController.navigate(R.id.nav_late_pickup_settings);

            drawer.closeDrawers();
        });
        system.setOnClickListener(view -> {

            usersTxt.setTextColor(getColor(R.color.black));
            orderAppsTxt.setTextColor(getColor(R.color.black));
            bayManagementTxt.setTextColor(getColor(R.color.black));
            bayAlertsTxt.setTextColor(getColor(R.color.black));
            systemTxt.setTextColor(getColor(R.color.orange));
            backLoadingTxt.setTextColor(getColor(R.color.black));
            historyTxt.setTextColor(getColor(R.color.black));

            usersIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            orderAppsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayManagementIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayAlertsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            systemIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.orange)));
            backLoadingIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            historyIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));

            users.setBackgroundColor(getColor(R.color.white));
            orderApps.setBackgroundColor(getColor(R.color.white));
            bayManagement.setBackgroundColor(getColor(R.color.white));
            bayAlerts.setBackgroundColor(getColor(R.color.white));
            system.setBackgroundColor(getColor(R.color.beige2));
            backLoading.setBackgroundColor(getColor(R.color.white));
            history.setBackgroundColor(getColor(R.color.white));
            navController.navigate(R.id.nav_home_screen_settings);

            drawer.closeDrawers();
        });
        backLoading.setOnClickListener(view -> {
            usersTxt.setTextColor(getColor(R.color.black));
            orderAppsTxt.setTextColor(getColor(R.color.black));
            bayManagementTxt.setTextColor(getColor(R.color.black));
            bayAlertsTxt.setTextColor(getColor(R.color.black));
            systemTxt.setTextColor(getColor(R.color.black));
            backLoadingTxt.setTextColor(getColor(R.color.orange));
            historyTxt.setTextColor(getColor(R.color.black));

            usersIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            orderAppsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayManagementIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayAlertsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            systemIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            backLoadingIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.orange)));
            historyIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));

            users.setBackgroundColor(getColor(R.color.white));
            orderApps.setBackgroundColor(getColor(R.color.white));
            bayManagement.setBackgroundColor(getColor(R.color.white));
            bayAlerts.setBackgroundColor(getColor(R.color.white));
            system.setBackgroundColor(getColor(R.color.white));
            backLoading.setBackgroundColor(getColor(R.color.beige2));
            history.setBackgroundColor(getColor(R.color.white));
            navController.navigate(R.id.nav_back_loading);

            drawer.closeDrawers();
        });
        history.setOnClickListener(view -> {
            usersTxt.setTextColor(getColor(R.color.black));
            orderAppsTxt.setTextColor(getColor(R.color.black));
            bayManagementTxt.setTextColor(getColor(R.color.black));
            bayAlertsTxt.setTextColor(getColor(R.color.black));
            systemTxt.setTextColor(getColor(R.color.black));
            backLoadingTxt.setTextColor(getColor(R.color.black));
            historyTxt.setTextColor(getColor(R.color.orange));

            usersIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            orderAppsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayManagementIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            bayAlertsIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            systemIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            backLoadingIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            historyIm.setImageTintList(ColorStateList.valueOf(getColor(R.color.orange)));

            users.setBackgroundColor(getColor(R.color.white));
            orderApps.setBackgroundColor(getColor(R.color.white));
            bayManagement.setBackgroundColor(getColor(R.color.white));
            bayAlerts.setBackgroundColor(getColor(R.color.white));
            system.setBackgroundColor(getColor(R.color.white));
            backLoading.setBackgroundColor(getColor(R.color.white));
            history.setBackgroundColor(getColor(R.color.beige2));

            navController.navigate(R.id.nav_history);

            drawer.closeDrawers();
        });

        button.setOnClickListener(view -> {
            if (navController.getCurrentDestination().getId() == R.id.nav_user_settings) {
                Intent intent = new Intent(PreferencesActivity.this, UserSettingsActivity.class);
                intent.putExtra("flow", "CREATE_USER");
                startActivity(intent);
            } else if (navController.getCurrentDestination().getId() == R.id.nav_courier_settings) {
                Intent intent = new Intent(PreferencesActivity.this, CourierSettingsActivity.class);
                intent.putExtra("flow", "CREATE_COURIER");
                intent.putExtra("size", "CREATE_COURIER");
                startActivity(intent);
            }
        });

        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if ((navDestination.getId() == R.id.nav_bay_settings)) {
                title.setText("Bay Management");
                button.setVisibility(View.GONE);
            } else if (navDestination.getId() == R.id.nav_user_settings) {
                button.setVisibility(View.VISIBLE);
                title.setText("Users");
                button.setText("ADD NEW USER +");
            } else if (navDestination.getId() == R.id.nav_courier_settings) {
                button.setVisibility(View.VISIBLE);
                button.setText("ADD NEW +");
                title.setText("Order Apps");
            } else if (navDestination.getId() == R.id.nav_late_pickup_settings) {
                button.setVisibility(View.GONE);
                title.setText("Bay Alerts");
            } else if (navDestination.getId() == R.id.nav_home_screen_settings) {
                button.setVisibility(View.GONE);
                title.setText("System");
            } else if (navDestination.getId() == R.id.nav_back_loading) {
                button.setVisibility(View.GONE);
                title.setText("Backloading");
            } else if (navDestination.getId() == R.id.nav_history) {
                button.setVisibility(View.GONE);
                title.setText("History");
            } else {
                button.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.preferences, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMessageReceived2(String message) {

    }
}
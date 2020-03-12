package com.example.Financial;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import static com.example.Financial.R.id.nav_host_fragment;

public class MainActivity extends AppCompatActivity {

    String TAG = "YEN";
    NavController mNavController;
    BottomNavigationView mBottomNavigationView;
    NavigationView mNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        // bottom & side navigation
        mNavController = Navigation.findNavController(this, nav_host_fragment);
        mBottomNavigationView = findViewById(R.id.nav_view_bottom);
        mBottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationItemSelectedListener);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

//        mNavController.navigate(R.id.nav_home);
    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.d(TAG, "navigationItemSelectedListener: " + item.getTitle());

            // set bottom nav checked
            checkBottomItemExist(item);

            mNavController.navigate(item.getItemId());
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            return false;
        }
    };

    BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.d(TAG, "bottomNavigationItemSelectedListener: " + item.getTitle());

            mBottomNavigationView.getMenu().setGroupCheckable(0, true, true);
            mNavController.navigate(item.getItemId());
            item.setChecked(true);

            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getTitle());

        checkBottomItemExist(item);
        mNavController.navigate(item.getItemId());

        return super.onOptionsItemSelected(item);
    }

    private void checkBottomItemExist(MenuItem item){
        if(mBottomNavigationView.getMenu().findItem(item.getItemId()) != null) {
            mBottomNavigationView.setSelectedItemId(item.getItemId());
            mBottomNavigationView.getMenu().setGroupCheckable(0, true, true);
        }
        else {
            mBottomNavigationView.getMenu().setGroupCheckable(0, false, true);
        }
    }
}

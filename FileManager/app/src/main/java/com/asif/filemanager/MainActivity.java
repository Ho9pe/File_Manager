package com.asif.filemanager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.asif.filemanager.Fragments.CardFragment;
import com.asif.filemanager.Fragments.HomeFragment;
import com.asif.filemanager.Fragments.InternalFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open_Drawer, R.string.Close_Drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).addToBackStack(null).commit();
        }
        else if (itemId == R.id.nav_internal) {
            InternalFragment internalFragment = new InternalFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit();
        }
        else if (itemId == R.id.nav_card) {
            CardFragment cardFragment = new CardFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, cardFragment).addToBackStack(null).commit();
        }
        else if (itemId == R.id.nav_about) {
            Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        MenuItem homeMenuItem = navigationView.getMenu().findItem(R.id.nav_home);
        if (homeMenuItem != null) {
            homeMenuItem.setChecked(true);
        }
        updateDrawerSelection();
    }
    private void updateDrawerSelection() {
        MenuItem selectedItem = navigationView.getCheckedItem();
        if (selectedItem != null) {
            int selectedItemId = selectedItem.getItemId();
            if (selectedItemId == R.id.nav_internal || selectedItemId == R.id.nav_card) {
                navigationView.setCheckedItem(R.id.nav_home);
            } else {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof InternalFragment) {
                    navigationView.setCheckedItem(R.id.nav_internal);
                } else if (currentFragment instanceof CardFragment) {
                    navigationView.setCheckedItem(R.id.nav_card);
                } else {
                    navigationView.setCheckedItem(R.id.nav_home);
                }
            }
        }
    }


}

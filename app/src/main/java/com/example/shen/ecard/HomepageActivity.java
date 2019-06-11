package com.example.shen.ecard;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fab;
    public static String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        if ( ContextCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomepageActivity.this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

        Intent intentlast=getIntent();
        username=intentlast.getStringExtra("username");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkTool.isNetworkConnected(HomepageActivity.this)) {
                    Intent intent=new Intent(HomepageActivity.this, CaptureActivity.class);
                    intent.putExtra("type","add");
                    startActivity(intent);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displayScreen(R.id.nav_cards);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==2){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
            }else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        getMenuInflater().inflate(R.menu.homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Flag","");
            editor.putString("username","");
            editor.apply();
            Intent intent=new Intent(HomepageActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayScreen(int id){
        android.support.v4.app.Fragment fragment=null;

        switch(id){
            case R.id.nav_cards:
                fragment=new CardsFragment();
                fab.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_location:
                fragment=new LocationFragment();
                fab.setVisibility(View.GONE);
                break;
            case R.id.nav_discounts:
                fragment=new DiscountsFragment();
                fab.setVisibility(View.GONE);
                break;
        }

        if(fragment!=null){
            android.support.v4.app.FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main,fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        displayScreen(id);
        return true;
    }
}

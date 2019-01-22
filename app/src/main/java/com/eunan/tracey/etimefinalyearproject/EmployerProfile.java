package com.eunan.tracey.etimefinalyearproject;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;


public class EmployerProfile extends AppCompatActivity {
    private final String TAG = "Employer Profile";

    private ViewPager mViewPager;
    // Create Toolbar
    private Toolbar mToolbar;
    // Create TabLayout
    private TabLayout mTabLayout;
    // Create sectionsPageAdapter
    // Used to split each fragment into its own tab
    private SectionsPageAdapter mSectionsPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);

        // Initialise Toolbar and set its constraints
        mToolbar = findViewById(R.id.employer_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ETime");

        // Initialise ViewPager
        mViewPager = findViewById(R.id.employer_tab_pager);
        // Initialise SectionsPageAdapter
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Initialise Adapter
        mViewPager.setAdapter(mSectionsPageAdapter);
        // Initialise TabLayout
        mTabLayout = findViewById(R.id.employer_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: starts");
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: starts");
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.employer_logout_button){
            FirebaseAuth.getInstance().signOut();
        }
        if(item.getItemId()== R.id.user_settings_button){
//            Intent settingsIntent = new Intent(EmployerProfile.this,MainActivity.class);
//            startActivity(settingsIntent);
        }
        Log.d(TAG, "onOptionsItemSelected: ends");
        return true;
    }
}

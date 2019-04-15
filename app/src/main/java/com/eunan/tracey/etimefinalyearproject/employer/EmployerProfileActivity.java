package com.eunan.tracey.etimefinalyearproject.employer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProfileActivity;
import com.eunan.tracey.etimefinalyearproject.main.MainActivity;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.settings.SettingsActivity;


public class EmployerProfileActivity extends AppCompatActivity {
    private final String TAG = "Employer Profile";

    private ViewPager viewPager;
    // Create Toolbar
    private Toolbar toolbar;
    // Create TabLayout
    private TabLayout tabLayout;
    // Create sectionsPageAdapter
    // Used to split each fragment into its own tab
    private SectionsPageAdapterEmployer sectionsPageAdapterEmployer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Initialise Toolbar and set its constraints
        toolbar = findViewById(R.id.employer_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Time-Sheet");
        Drawable dr = getResources().getDrawable(R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        // Initialise ViewPager
        viewPager = findViewById(R.id.employer_tab_pager);
        // Initialise SectionsPageAdapterEmployee
        sectionsPageAdapterEmployer = new SectionsPageAdapterEmployer(getSupportFragmentManager());

        // Initialise Adapter
        viewPager.setAdapter(sectionsPageAdapterEmployer);
        // Initialise TabLayout
        tabLayout = findViewById(R.id.employer_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: starts");
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: starts");
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.logout_button_app_bar :
                // FirebaseAuth.getInstance().signOut();
                Intent mainIntent = new Intent(EmployerProfileActivity.this, MainActivity.class);
                startActivity(mainIntent);
                break;

            case R.id.user_settings_button :
                Intent settingsIntent = new Intent(EmployerProfileActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            case R.id.view_employee :
                Intent employeeIntent = new Intent(EmployerProfileActivity.this, EmployeeProfileActivity.class);
                startActivity(employeeIntent);
                break;
            default :
                break;
        }

        Log.d(TAG, "onOptionsItemSelected: ends");
        return true;
    }
}

package com.example.social.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.social.App;
import com.example.social.R;
import com.example.social.constants.Constants;
import com.example.social.databinding.ActivityMainBinding;
import com.example.social.fragment.FeedFragment;
import com.example.social.fragment.MessageFragment;
import com.example.social.fragment.ProfileFragment;
import com.example.social.utils.DateUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private FeedFragment feedFragment;
    private ProfileFragment profileFragment;
    private MessageFragment messageFragment;
    private App app = new App();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        initializeToolbar();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new FeedFragment())
                .commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.navigation_headlines:
                            if (feedFragment == null){
                                feedFragment = FeedFragment.newInstance();
                            }
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container,feedFragment)
                                    .commit();
                            return true;
                        case R.id.navigation_saved:
                            if (messageFragment == null){
                                messageFragment = MessageFragment.newInstance();
                            }
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container,messageFragment)
                                    .commit();
                            return true;
                        case R.id.navigation_sources:
                            if (profileFragment == null){
                                profileFragment = ProfileFragment.newInstance();
                            }
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container,profileFragment)
                                    .commit();
                            return true;
                    }
                    return false;
                }
            };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_log_out){
            firebaseLogout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void firebaseLogout() {
        AuthUI.getInstance()
                .signOut(MainActivity.this)
                .addOnCompleteListener(task -> startActivity(new Intent(this,LoginActivity.class))).addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(),"Fail while logging out..",Toast.LENGTH_LONG).show());
        getApplicationContext().getSharedPreferences(Constants.AUTH_PERSISTENCE, Context.MODE_PRIVATE).edit().clear().apply();
        app.setUserStatus(DateUtils.getLocalTime(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    private void initializeToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Social");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        app.setUserStatus("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        app.setUserStatus(DateUtils.getLocalTime(getApplicationContext()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.setUserStatus(DateUtils.getLocalTime(getApplicationContext()));
    }
}

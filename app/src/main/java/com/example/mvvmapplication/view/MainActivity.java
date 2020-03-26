package com.example.mvvmapplication.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;


import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mvvmapplication.Application;
import com.example.mvvmapplication.constants.Constants;
import com.example.mvvmapplication.databinding.FeedActivityBinding;
import com.example.mvvmapplication.datasource.FeedViewModel;
import com.example.mvvmapplication.adapter.FeedListAdapter;
import com.example.mvvmapplication.R;
import com.firebase.ui.auth.AuthUI;

public class MainActivity extends AppCompatActivity {
    FeedViewModel feedViewModel;
    FeedListAdapter feedListAdapter;
    FeedActivityBinding feedItemBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFeed();
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_log_out){
          firebaseLogout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeFeed(){
        feedItemBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        feedViewModel = new FeedViewModel(Application.create(this));
        feedItemBinding.listFeed.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        feedListAdapter = new FeedListAdapter(getApplicationContext());
        feedViewModel.getPagedListLiveData().observe(this, pagedList -> feedListAdapter.submitList(pagedList));
        feedViewModel.getNetworkState().observe(this,networkState -> feedListAdapter.setNetworkState(networkState));
        feedItemBinding.listFeed.setAdapter(feedListAdapter);
    }
    private void firebaseLogout(){
        AuthUI.getInstance()
                .signOut(MainActivity.this)
                .addOnCompleteListener(task -> startActivity(new Intent(this,LoginActivity.class))).addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(),"Fail while logging out..",Toast.LENGTH_LONG).show());
        getApplicationContext().getSharedPreferences(Constants.AUTH_PERSISTENCE, Context.MODE_PRIVATE).edit().clear().apply();
    }
}

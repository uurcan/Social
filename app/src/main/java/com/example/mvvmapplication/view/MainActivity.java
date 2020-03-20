package com.example.mvvmapplication.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.mvvmapplication.databinding.FeedActivityBinding;
import com.example.mvvmapplication.datasource.FeedViewModel;
import com.example.mvvmapplication.adapter.FeedListAdapter;
import com.example.mvvmapplication.R;

public class MainActivity extends AppCompatActivity {
    FeedViewModel feedViewModel;
    FeedListAdapter feedListAdapter;
    FeedActivityBinding feedItemBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedItemBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        feedViewModel = new FeedViewModel(AppController.create(this));
        feedItemBinding.listFeed.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        feedListAdapter = new FeedListAdapter(getApplicationContext());
        feedViewModel.getPagedListLiveData().observe(this, pagedList -> feedListAdapter.submitList(pagedList));
        feedViewModel.getNetworkState().observe(this,networkState -> feedListAdapter.setNetworkState(networkState));
        feedItemBinding.listFeed.setAdapter(feedListAdapter);

    }
}

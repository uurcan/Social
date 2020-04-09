package com.example.mvvmapplication.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mvvmapplication.Application;
import com.example.mvvmapplication.constants.Constants;
import com.example.mvvmapplication.databinding.FeedActivityBinding;
import com.example.mvvmapplication.datasource.FeedViewModel;
import com.example.mvvmapplication.adapter.FeedListAdapter;
import com.example.mvvmapplication.R;
import com.example.mvvmapplication.model.Article;
import com.firebase.ui.auth.AuthUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FeedListAdapter.OnItemClickListener {
    FeedViewModel feedViewModel;
    FeedListAdapter feedListAdapter;
    FeedActivityBinding feedItemBinding;
    List<Article> articleList = new ArrayList<>();
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

    @Override
    public void onItemClick(View view, int position) {
        ImageView imageView = view.findViewById(R.id.item_detail_image);
        Intent intent = new Intent(MainActivity.this,FeedDetailsActivity.class);
        Article article = articleList.get(position);
        intent.putExtra(Constants.TITLE,article.getTitle());
        intent.putExtra(Constants.IMAGE,article.getUrlToImage());
        intent.putExtra(Constants.DATE,article.getPublishedAt());
        intent.putExtra(Constants.AUTHOR,article.getAuthor());
        androidx.core.util.Pair<View,String> viewStringPair = Pair.create(imageView, ViewCompat.getTransitionName(imageView));
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, viewStringPair);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            startActivity(intent,optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }
}

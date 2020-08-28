package com.example.social.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;


import com.bumptech.glide.Glide;
import com.example.social.R;
import com.example.social.databinding.ActivityFeedDetailsBinding;
import com.example.social.model.feed.Article;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.Objects;

public class FeedDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    public static final String PARAM_ARTICLE = "article_params";
    private boolean isToolbarHidden;
    private ActivityFeedDetailsBinding activityDetailsBinding;
    private Article article;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDetailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_feed_details);
        initializeToolbar();
        initializeComponents();
        initializeFeedDetails();
        initializeWebView(article);
    }

    private void initializeComponents() {
        activityDetailsBinding.applicationBar.addOnOffsetChangedListener(this);
    }

    private void initializeFeedDetails(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(PARAM_ARTICLE)){
            final Article article = bundle.getParcelable(PARAM_ARTICLE);
            if (article != null){
                this.article = article;
                activityDetailsBinding.setArticle(article);
                initializeWebView(article);
                Glide.with(getApplicationContext()).load(article.getUrlToImage()).into(activityDetailsBinding.backDropFeedDetail);
            }
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView(Article article) {
        WebView webView = findViewById(R.id.feed_detail_web_view);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(article.getUrl());
    }
    private void initializeToolbar(){
        Toolbar toolbar = findViewById(R.id.feed_detail_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_feed_detail);
        collapsingToolbarLayout.setTitle("");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset)/(float) maxScroll;
        if (percentage == 1f && isToolbarHidden){
            activityDetailsBinding.feedDetailsDate.setVisibility(View.GONE);
            activityDetailsBinding.toolbarFeedDetail.setVisibility(View.VISIBLE);
            isToolbarHidden = !isToolbarHidden;
        }else if(percentage < 1f && !isToolbarHidden){
            activityDetailsBinding.feedDetailsDate.setVisibility(View.VISIBLE);
            activityDetailsBinding.toolbarFeedDetail.setVisibility(View.GONE);
            isToolbarHidden = !isToolbarHidden;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.view_web:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(article.getUrl()));
                startActivity(intent);
                break;
            case R.id.share:
                try {
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType(getString(R.string.textPlan));
                    intent.putExtra(Intent.EXTRA_SUBJECT,article.getSource());
                    String body = article.getTitle() + " " + article.getUrl();
                    intent.putExtra(Intent.EXTRA_TEXT,body);
                    startActivity(Intent.createChooser(intent,getString(R.string.textShareWith)));
                    break;
                } catch (Exception e){
                    Toast.makeText(this, "Unable to share", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
        }
        return super.onOptionsItemSelected(item);
    }
}

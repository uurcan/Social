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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.social.R;
import com.example.social.constants.Constants;
import com.example.social.databinding.ActivityDetailsBinding;
import com.example.social.utils.ApplicationUtils;
import com.example.social.utils.DateFormat;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.Objects;

public class FeedDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private String feedURL,feedSource,feedTitle;
    private boolean isToolbarHidden;
    private ActivityDetailsBinding activityDetailsBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDetailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_feed_details);
        initializeToolbar();
        initializeComponents();
        initializeFeedDetails();
        initializeWebView(feedURL);
    }

    private void initializeComponents() {
        activityDetailsBinding.applicationBar.addOnOffsetChangedListener(this);
    }

    private void initializeFeedDetails(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null ){
            feedURL = bundle.getString(Constants.URL);
            feedTitle = bundle.getString(Constants.TITLE);
            feedSource = bundle.getString(Constants.SOURCE);

            String feedAuthor = bundle.getString(Constants.AUTHOR);
            StringBuilder feedInfo = new StringBuilder().append(feedSource == null ||
                            feedAuthor == null ? "Anonymous" : feedSource)
                    .append(" - ").append(ApplicationUtils.getDate(bundle.getString(Constants.DATE)));

            activityDetailsBinding.feedTitleToolbar.setText(bundle.getString(Constants.TITLE));
            activityDetailsBinding.feedSubtitleToolbar.setText(feedURL);
            activityDetailsBinding.feedDetailDatetime.setText(DateFormat.formatDate(bundle.getString(Constants.DATE)));
            activityDetailsBinding.feedDetailTitle.setText(feedTitle);
            activityDetailsBinding.feedDetailsTimezone.setText(feedInfo);
            activityDetailsBinding.feedDetailDescription.setText(bundle.getString(Constants.DESCRIPTION));
            Glide.with(this)
                    .load(bundle.getString(Constants.IMAGE))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.placeholder640)
                    .into(activityDetailsBinding.backDropFeedDetail);

        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView(String feedURL) {
        WebView webView = findViewById(R.id.feed_detail_web_view);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(feedURL);
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
                intent.setData(Uri.parse(feedURL));
                startActivity(intent);
                break;
            case R.id.share:
                try {
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType(getString(R.string.textPlan));
                    intent.putExtra(Intent.EXTRA_SUBJECT,feedSource);
                    String body = feedTitle + " " + feedURL;
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

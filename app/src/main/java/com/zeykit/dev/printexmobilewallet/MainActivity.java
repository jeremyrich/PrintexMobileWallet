package com.zeykit.dev.printexmobilewallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    final String mPrintexWalletUrl = "http://webwallet.printex.tech/";

    ActionBar mActionBar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    AdvancedWebView mWebView;
    FrameLayout mFrameLayout;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimaryDark),
                    ContextCompat.getColor(this, R.color.colorAccent));
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (mWebView != null) {
                        mWebView.loadUrl(mWebView.getUrl());
                    }
                }
            });
        }

        mFrameLayout = findViewById(R.id.activity_main_frame_layout);

        mProgressBar = findViewById(R.id.activity_main_progress_bar);
        if (mProgressBar != null) {
            mProgressBar.setMax(100);
            mProgressBar.setProgress(0);
        }

        mWebView = findViewById(R.id.activity_main_web_view);
        if (mWebView != null) {
            mWebView.setListener(this, this);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setVerticalScrollBarEnabled(false);

            mWebView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    if (mFrameLayout != null && mProgressBar != null) {
                        mFrameLayout.setVisibility(View.VISIBLE);
                        mProgressBar.setProgress(progress);

                        if (progress == 100) {
                            mFrameLayout.setVisibility(View.GONE);
                        }
                    }
                    super.onProgressChanged(view, progress);
                }
            });

            mWebView.loadUrl(mPrintexWalletUrl);
        }
    }

    private void setupActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            TextView mTextView = new TextView(this);

            ActionBar.LayoutParams mLayoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.WRAP_CONTENT);

            mTextView.setLayoutParams(mLayoutParams);
            mTextView.setText(mActionBar.getTitle());
            mTextView.setTextColor(Color.BLACK);
            mTextView.setGravity(Gravity.CENTER);

            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            mActionBar.setCustomView(mTextView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (mWebView != null) {
            mWebView.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && !mWebView.onBackPressed()) { return; }
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        if (mFrameLayout != null && (mFrameLayout.getVisibility() == View.GONE || mFrameLayout.getVisibility() == View.INVISIBLE)) {
            mFrameLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageFinished(String url) {
        if (mSwipeRefreshLayout != null
                && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {}

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDescription, String userAgent) {}

    @Override
    public void onExternalPageRequest(String url) {}
}

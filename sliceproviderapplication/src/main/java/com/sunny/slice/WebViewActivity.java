package com.sunny.slice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private WebView mWebview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebview = findViewById(R.id.view_webview);
        mWebview.getSettings().setAllowFileAccess(true);
        Intent intent = getIntent();
        if (intent != null) {
            Uri uri = intent.getData();
            mWebview.loadUrl(uri.toString());
        }
    }
}

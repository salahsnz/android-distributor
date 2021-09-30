package com.zopnote.android.merchant.agreement;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.util.Utils;

public class AgreementActivity extends AppCompatActivity {
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
    private AgreementViewModel viewmodel;
    private ProgressDialog progressDialog;

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
         setContentView(R.layout.agreement_act);

        viewmodel = obtainViewModel(this);
        viewmodel.init();

        if (savedInstanceState != null) {
            stateBundle = savedInstanceState;
        } else {
            stateBundle = getIntent().getExtras();
        }

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(AgreementActivity.this);
                    progressDialog.setMessage(AgreementActivity.this.getResources().getString(R.string.submitting_agreement_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });

        viewmodel.apiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(AgreementActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(AgreementActivity.this,
                            AgreementActivity.this.getResources().getString(R.string.submitted_agreement_success_message),
                            Toast.LENGTH_LONG);
                    Prefs.putBoolean(AppConstants.PREFS_ACCEPT_MERCHANT_AGREMENT, true);
                    AgreementActivity.this.finish();
                }
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                System.out.println("Merchant id: " + merchant.getId());
            }
        });
        Button action = findViewById(R.id.action);
        if (!Prefs.getBoolean(AppConstants.PREFS_ACCEPT_MERCHANT_AGREMENT, false)){
          action.setText("Accept");
          action.setBackgroundColor(getResources().getColor(R.color.warning_red));
        }else {
            action.setClickable(false);
            action.setTextColor(getResources().getColor(R.color.black_100));
        }

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.enforceNetworkConnection(AgreementActivity.this)) {
                    if (Prefs.getBoolean(AppConstants.PREFS_ACCEPT_MERCHANT_AGREMENT, false)){
                        Toast.makeText(getApplicationContext(),"Your agreement already submitted",Toast.LENGTH_LONG).show();
                        return;
                    }
                    viewmodel.submitAgreement();
                }
            }
        });

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

    public static AgreementViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        AgreementViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(AgreementViewModel.class);

        return viewModel;
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
            AgreementActivity.this.onAction();
        }

    }
}

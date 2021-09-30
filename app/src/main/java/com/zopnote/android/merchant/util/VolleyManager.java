package com.zopnote.android.merchant.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

import java.io.File;


public class VolleyManager {

    public static final String CACHE_FOLDER_NETWORK = "net-zopnote";

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "VolleyManager";

    private static Context context;
    private static VolleyManager instance;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private VolleyManager(Context ctx) {

        // Currently context is not used, but will be required when we need disk based cache
        // When using context, remember to use context.getApplicationContext() to prevent leaking of passed in contexts
        context = ctx.getApplicationContext();

        requestQueue = getRequestQueue();

        // Customize the log tag for your application, so that other apps using Volley don't mix their logs with yours.
        //TODO: change tag
        VolleyLog.setTag("Net-Zopnote");

        imageLoader = new ImageLoader(requestQueue, new BitmapLruCache());
    }

    public static synchronized VolleyManager getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyManager(context);
        }
        return instance;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            Network network = new BasicNetwork(new HurlStack());
            Cache cache = new DiskBasedCache(new File(context.getCacheDir(), CACHE_FOLDER_NETWORK), 10 * 1024 * 1024); // 10 MB cap
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void debugCache(String key) {
        if (DEBUG) Log.d(LOG_TAG, "key: " + key);
        Cache.Entry entry = getRequestQueue().getCache().get(key);
        if (entry != null) {
            if (DEBUG) Log.d(LOG_TAG, "expired? " + entry.isExpired());
        } else {
            if (DEBUG) Log.d(LOG_TAG, "cache not found");
        }
    }

}

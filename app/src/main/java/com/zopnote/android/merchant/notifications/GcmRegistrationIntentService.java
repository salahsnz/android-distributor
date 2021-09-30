package com.zopnote.android.merchant.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.Installation;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Nizam on 11-07-2015.
 */
public class GcmRegistrationIntentService extends IntentService {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "GcmRegIntentService";

    public GcmRegistrationIntentService() {
        // worker thread name for debugging
        super("GcmRegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            final String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (DEBUG) Log.d(LOG_TAG, "token=" + token);
            if (DEBUG) Log.d(LOG_TAG, "uid=" + Installation.getUid(this));
            // init volley
            VolleyManager.getInstance(getApplicationContext());
            // register token on server
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(AppConstants.ATTR_UID, Installation.getUid(this));
            jsonObj.put(AppConstants.ATTR_GCM_TOKEN, token);

            final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_GCM_REGISTER);
            authenticator.setBody(jsonObj.toString());

            JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST, authenticator.getUri(), jsonObj, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    if (DEBUG) Log.d(LOG_TAG, "onResponse");
                    System.out.println("GCM SEND RESPONS"  + "yes");
                    // no-op
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (DEBUG) Log.d(LOG_TAG, "error = " + error.getClass().getName());
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return authenticator.getVolleyHttpHeaders();
                }
            };

            volleyRequest.setShouldCache(false);
            volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 3, 3f)); // 10s timeout, 3 retry, 3f backoff multiplier
            VolleyManager.getInstance(getApplicationContext()).addToRequestQueue(volleyRequest);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

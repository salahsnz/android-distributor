package com.zopnote.android.merchant.notifications.inbox;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Notification;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zopnote.android.merchant.AppConstants.ENDPOINT_DELETE_NOTIFICATION;
import static com.zopnote.android.merchant.BuildConfig.DEBUG;

/**
 * Created by salah on 30/01/2020.
 */

public class InboxViewModel extends AndroidViewModel {

    private String LOG_TAG = InboxViewModel.class.getSimpleName();
    private Repository repository;

    public LiveData<Merchant> merchant;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteNotificationApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> deleteNotificationApiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> deleteNotificationApiCallSuccess = new MutableLiveData<>();

    public String apiCallErrorMessage;
    public List<Notification> notifications;

    public InboxViewModel(Application context, Repository repository) {
        super(context);
        this.repository = repository;

    }

    public void init() {
        if (merchant != null) {
            return;
        }

        merchant = repository.getMerchant();
    }


    public void getNotification() {

        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_NOTIFICATION);

        authenticator.addParameter(Param.MERCHANT_ID, merchant.getValue().getId());
        JsonArrayRequest volleyRequest = new JsonArrayRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        System.out.println("Response : " +response.toString());
                        apiCallRunning.postValue(false);

                        saveNotification(response);

                        apiCallSuccess.postValue(true);
                        apiCallError.postValue(false);
                        networkError.postValue(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                networkError.postValue(false);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                System.out.println("Response : " +error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        apiCallRunning.setValue(true);
        apiCallError.setValue(false);
        apiCallSuccess.setValue(false);
        networkError.setValue(false);
    }


    private void saveNotification(JSONArray response) {
        notifications = new ArrayList<>();
        if (response.length() == 0){
            return;
        }
        for (int i = 0; i < response.length(); i++) {
            try{
                Notification notification = new Notification();

                JSONObject notificationObject = response.getJSONObject(i);

                if( ! notificationObject.isNull("created")){
                    notification.setCreated(notificationObject.getLong("created"));
                }

                if( ! notificationObject.isNull("description")){
                    notification.setDescription(notificationObject.getString("description"));
                }

                if( ! notificationObject.isNull("notificationID")){
                    notification.setNotificationID((notificationObject.getString("notificationID")));
                }

                if( ! notificationObject.isNull("destinationID")){
                    notification.setDestinationID(notificationObject.getString("destinationID"));
                }

                if( ! notificationObject.isNull("title")){
                    notification.setTitle(notificationObject.getString("title"));
                }

                if( notificationObject.has("action") &&! notificationObject.isNull("action")){
                    notification.setAction(notificationObject.getString("action"));
                }



                notifications.add(notification);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        Collections.sort(notifications, new Comparator<Notification>() {

            @Override
            public int compare(Notification lhs, Notification rhs) {
                return new Date(rhs.getCreated()).compareTo( new Date(lhs.getCreated()));

            }
        });

    }


    public void deleteNotification(String notificationID,boolean clearAll) {
        JSONObject jsonObject = getDeleteNotificationJsonRequest(notificationID,clearAll);
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        // "http://18.139.71.131:8080/ZopnoteWeb/app/v1/deleteNotifications"
        final Authenticator authenticator = new Authenticator(ENDPOINT_DELETE_NOTIFICATION);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        deleteNotificationApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                deleteNotificationApiCallSuccess.postValue(true);
                            } else {
                                deleteNotificationApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            deleteNotificationApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                deleteNotificationApiRunning.postValue(false);
                deleteNotificationApiCallError.postValue(true);
                System.out.println(error);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", "76380346");
                params.put("Content-Type", "application/json");
                return params;
                //return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        deleteNotificationApiRunning.setValue(true);
        deleteNotificationApiCallError.setValue(false);
        deleteNotificationApiCallSuccess.setValue(false);
    }

    private JSONObject getDeleteNotificationJsonRequest(String notificationID,boolean clearAll) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            if (clearAll)
                jsonObject.put("clearAll", true);
            else {
                jsonObject.put("clearAll", false);
                jsonObject.put("notificationID",notificationID );
            }

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

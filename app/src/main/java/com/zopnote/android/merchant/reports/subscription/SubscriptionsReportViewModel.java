package com.zopnote.android.merchant.reports.subscription;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.data.model.Collection;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Pause;
import com.zopnote.android.merchant.data.model.StatusEnum;
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
import java.util.List;
import java.util.Map;

public class SubscriptionsReportViewModel extends AndroidViewModel {
    private static final boolean DEBUG = false;
    private String LOG_TAG = "SubscriptionsReport";

    private Repository repository;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> canSavePdf = new MutableLiveData<>();
    public MutableLiveData<String> reportPdfUri = new MutableLiveData<>();
    public String apiCallErrorMessage;
    public List reportItems;
    public String filterProduct;
    public String filterRoute;
    public boolean isFilter = false;
    public LiveData<Merchant> merchant;
    public String merchantId;
    public MutableLiveData<Boolean> dateChanged = new MutableLiveData<>();

    public SubscriptionsReportViewModel(@NonNull Application application, Repository repository) {
        super(application);
        this.repository = repository;

    }

    public void init() {
        if(merchant != null){
            return;
        }
        reportItems = new ArrayList<>();
        merchant = repository.getMerchant();

    }

    public void getSubscriptionReport() {
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_SUBSCRIPTIONS_REPORT);

        authenticator.addParameter(Param.MERCHANT_ID, merchantId);

        JsonArrayRequest volleyRequest = new JsonArrayRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());

                        apiCallRunning.postValue(false);

                        saveSubscriptionsReport(response);

                        apiCallSuccess.postValue(true);
                        canSavePdf.postValue(true);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
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
    }

    private void saveSubscriptionsReport(JSONArray response) {
        try {
            reportItems.clear();
            String previousRoute = "";

            for (int i = 0; i < response.length(); i++) {
                JSONObject reportItemObject = (JSONObject) response.get(i);

                String currentRoute = reportItemObject.getString("route");
                if (isFilter)
                    currentRoute = currentRoute+ " - " +filterProduct;
                if( ! currentRoute.equalsIgnoreCase(previousRoute)){
                    addHeader(currentRoute);
                    previousRoute = currentRoute;
                }

                SubscriptionReportItem reportItem = new SubscriptionReportItem();
                reportItem.setId(reportItemObject.getString("id"));
                reportItem.setDoorNumber(reportItemObject.getString("doorNumber"));
                reportItem.setRoute(reportItemObject.getString("route"));

                if( ! reportItemObject.isNull("addressLine1")){
                    reportItem.setAddressLine1(reportItemObject.getString("addressLine1"));
                }

                if( ! reportItemObject.isNull("addressLine2")){
                    reportItem.setAddressLine2(reportItemObject.getString("addressLine2"));
                }

                if( ! reportItemObject.isNull("subscriptions")){
                    JSONArray subscriptionsJsonArray = reportItemObject.getJSONArray("subscriptions");

                    reportItem.setSubscriptions(getSubscriptions(subscriptionsJsonArray));
                }
                if (isFilter) {
                    if (!reportItem.getSubscriptions().isEmpty())
                        reportItems.add(reportItem);
                }else
                    reportItems.add(reportItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addHeader(String currentRoute) {
        RouteHeader routeHeader = new RouteHeader();
        routeHeader.setName(currentRoute);
        reportItems.add(routeHeader);
    }

    private List<SubscriptionInfo> getSubscriptions(JSONArray subscriptionsJsonArray) {
        List<SubscriptionInfo> subscriptions = new ArrayList<>();

        for (int j = 0; j <subscriptionsJsonArray.length(); j++){
            try{
                JSONObject subscriptionJsonObject = subscriptionsJsonArray.getJSONObject(j);

                String status = subscriptionJsonObject.getString("subscriptionStatus");
                if(status.equalsIgnoreCase(StatusEnum.INACTIVE.name())){
                    //ignore inactive subscriptions
                    continue;
                }
                String productName = subscriptionJsonObject.getString("productName");

                if(isFilter && !productName.equalsIgnoreCase(filterProduct)){
                    //filter paper wise
                    continue;
                }

                SubscriptionInfo subscriptionInfo = new SubscriptionInfo();
                subscriptionInfo.setSubscriptionStatus(StatusEnum.valueOf(status));

                subscriptionInfo.setId(subscriptionJsonObject.getString("id"));

                subscriptionInfo.setProductId(subscriptionJsonObject.getString("productId"));
                subscriptionInfo.setProductName(productName);

                if( ! subscriptionJsonObject.isNull("startDate")){
                    //start date should always be present
                    long startDateLong = subscriptionJsonObject.getLong("startDate");
                    subscriptionInfo.setStartDate(new Date(startDateLong));
                }

                if( ! subscriptionJsonObject.isNull("endDate")){
                    //optional
                    long endDateLong = subscriptionJsonObject.getLong("endDate");
                    subscriptionInfo.setEndDate(new Date(endDateLong));
                }

                if( ! subscriptionJsonObject.isNull("pauseList")){
                    List<Pause> pauseList = getPauseList(subscriptionJsonObject.getJSONArray("pauseList"));

                    Collections.sort(pauseList, new Comparator<Pause>() {

                        @Override
                        public int compare(Pause lhs, Pause rhs) {
                            return lhs.getPauseStartDate().compareTo(rhs.getPauseStartDate());

                        }
                    });
                    subscriptionInfo.setPauseList(pauseList);
                }

                subscriptions.add(subscriptionInfo);
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        return subscriptions;
    }

    private List<Pause> getPauseList(JSONArray pauseList) {
        List<Pause> pauses = new ArrayList<>();
        try {
            for (int i= 0; i< pauseList.length(); i++){
                JSONObject pauseObject = pauseList.getJSONObject(i);

                String status = pauseObject.getString("pauseStatus");
                if(status.equalsIgnoreCase(StatusEnum.INACTIVE.name())){
                    continue;
                }

                Pause pause = new Pause();
                pause.setPauseStatus(StatusEnum.valueOf(status));

                pause.setId(pauseObject.getString("id"));

                pause.setPauseStartDate(new Date(pauseObject.getLong("pauseStartDate")));

                if( ! pauseObject.isNull("pauseEndDate")){
                    pause.setPauseEndDate(new Date(pauseObject.getLong("pauseEndDate")));
                }

                pauses.add(pause);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }


        return pauses;
    }
}

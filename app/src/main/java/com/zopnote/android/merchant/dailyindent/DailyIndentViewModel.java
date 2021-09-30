package com.zopnote.android.merchant.dailyindent;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.data.model.DailyIndent;
import com.zopnote.android.merchant.data.model.DailyIndentSubscription;
import com.zopnote.android.merchant.data.model.IndentUpdate;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DailyIndentViewModel extends AndroidViewModel {
    private static final boolean DEBUG = false;
    private String LOG_TAG = "IndentViewModel";

    private Repository repository;

    public LiveData<Merchant> merchant;
    public String merchantId;
    public List<DailyIndent> indentReport;
    public List<DailyIndentSubscription> offeredProductList;
    public Map<String,Integer> offeredProductQtyMap;
    public Map<String,DailyIndentSubscription> subscriptionMap;
    public Map<String,List<DailyIndent>> routeWiseMap;
    public String route;
    public Calendar calender;
    public DailyIndent itemUpdated;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();

    public MutableLiveData<Boolean> updateQuantityApiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateQuantityApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateQuantityApiCallError = new MutableLiveData<>();
    public boolean isEdited = false;

    public MutableLiveData<Boolean> dateChanged = new MutableLiveData<>();

    public MutableLiveData<Boolean> canSavePdf = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public MutableLiveData<Boolean> quantityUpdated = new MutableLiveData<>();




    public DailyIndentViewModel(@NonNull Application context, Repository repository) {
        super(context);
        this.repository = repository;
    }

    public void init() {
        if (merchant != null) {
            return;
        }
        calender = FormatUtil.getLocalCalenderNoTime();
        Calendar c = calender;
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        calender.set(year, month, day, 0 , 0 , 0);
        calender.set(Calendar.MILLISECOND, 0);

        merchant = repository.getMerchant();
        indentReport = new ArrayList();
        offeredProductList = new ArrayList();
        offeredProductQtyMap = new HashMap<>();

        apiCallRunning.setValue(false);
        apiCallError.setValue(false);
        apiCallSuccess.setValue(false);
        networkError.setValue(false);
    }

    public void getIndentReport() {

        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_GET_DIST_INDENT);
        authenticator.addParameter(Param.MERCHANT_ID, merchantId);

        authenticator.addParameter("indentDate", String.valueOf(calender.getTime().getTime()));
        System.out.println("DATETRACE "+String.valueOf(calender.getTime().getTime()));
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        System.out.println("Response viewbill" + response.toString());

                        apiCallRunning.postValue(false);

                        saveIndentReport(response);


                        apiCallSuccess.postValue(true);
                        canSavePdf.postValue(true);
                        apiCallError.postValue(false);
                        networkError.postValue(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                networkError.postValue(false);
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
        networkError.setValue(false);
    }


    private void saveIndentReport(JSONObject response) {
        try {
            indentReport.clear();
            offeredProductList.clear();
            //if (!(offeredProductList.size() >0)) {
                JSONArray merchant = response.getJSONArray("merchant");
                 subscriptionMap = new HashMap<>();
                for (int i = 0; i < merchant.length(); i++) {
                    JSONObject offeredProdItemObject = (JSONObject) merchant.get(i);
                    DailyIndentSubscription subscription = new DailyIndentSubscription();
                    String prodId = offeredProdItemObject.getString("id");
                    subscription.setProductId(prodId);
                    String sName = offeredProdItemObject.getString("sName");
                    subscription.setProductShortCode(sName);
                    subscription.setSubscriptionQuantity(-1);
                    offeredProductList.add(subscription);
                    offeredProductQtyMap.put(sName,0);
                    subscriptionMap.put(prodId,subscription);
                }

                Collections.sort(offeredProductList, new Comparator<DailyIndentSubscription>() {
                    @Override
                    public int compare(DailyIndentSubscription s1, DailyIndentSubscription s2) {
                        return s1.getProductShortCode().compareToIgnoreCase(s2.getProductShortCode());
                    }
                });
           // }
            JSONArray customers = response.getJSONArray("customers");
            for (int i = 0; i < customers.length(); i++) {
                JSONObject reportItemObject = (JSONObject) customers.get(i);

                DailyIndent dailyIndent = new DailyIndent();

                dailyIndent.setCustomerId(reportItemObject.getString("customerId"));

                dailyIndent.setCustomerName(reportItemObject.getString("customerName"));

                dailyIndent.setRoute(reportItemObject.getString("route"));

                dailyIndent.setIndentDate(reportItemObject.getLong("indentDate"));

                dailyIndent.setMerchantId(reportItemObject.getString("merchantId"));

                JSONArray dailySubscription = reportItemObject.getJSONArray("subscriptions");

                Map<String, DailyIndentSubscription> customerSubsMap = new HashMap<>(subscriptionMap);
                for (int k = 0; k < dailySubscription.length(); k++) {
                    DailyIndentSubscription subscription = new DailyIndentSubscription();
                    JSONObject dailySubscriptionObject = (JSONObject) dailySubscription.get(k);

                    String prodId = dailySubscriptionObject.getString("productId");
                    subscription.setProductId(prodId);
                    String shortCode = dailySubscriptionObject.getString("shortCode");
                    subscription.setProductShortCode(shortCode);
                    int qty = dailySubscriptionObject.getInt("subscriptionQuantity");
                    subscription.setSubscriptionQuantity(qty);

                    System.out.println(prodId);
                    offeredProductQtyMap.put(shortCode, offeredProductQtyMap.get(shortCode) +qty);
                    customerSubsMap.put(prodId,subscription);
                }
                List<DailyIndentSubscription> additionalList = new ArrayList<>(customerSubsMap.values());

                Collections.sort(additionalList, new Comparator<DailyIndentSubscription>() {
                    @Override
                    public int compare(DailyIndentSubscription s1, DailyIndentSubscription s2) {
                        return s1.getProductShortCode().compareToIgnoreCase(s2.getProductShortCode());
                    }
                });

                dailyIndent.setSubscriptions( additionalList);

                indentReport.add(dailyIndent);
                Collections.sort(indentReport);
                filterRouteWise(indentReport);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void filterRouteWise(List<DailyIndent> indentReport) {
        routeWiseMap = new HashMap<>();

        for (DailyIndent item : indentReport) {

            if (!routeWiseMap.containsKey(item.getRoute())) {
                route=item.getRoute();
                routeWiseMap.put(item.getRoute(), new ArrayList<DailyIndent>());
            }
            routeWiseMap.get(item.getRoute()).add(item);
        }

    }
    public void updateQuantity() {
        JSONObject jsonObject = getUpdateInvoiceJsonRequest();
        System.out.println("RESPONSE " + jsonObject.toString());
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        System.out.println(jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_UPDATE_DIST_INDENT);
        authenticator.setBody(jsonObject.toString());
        System.out.println(authenticator.toString());
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        System.out.println("RESPON DIST IND " + response.toString());
                        updateQuantityApiCallRunning.postValue(false);
                        isEdited =false;
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                updateQuantityApiCallSuccess.postValue(true);
                            } else {
                                updateQuantityApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            updateQuantityApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateQuantityApiCallRunning.postValue(false);
                updateQuantityApiCallError.postValue(true);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", "76380346");
                params.put("Content-Type", "application/json");
                return params;
                // return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        updateQuantityApiCallRunning.setValue(true);
        updateQuantityApiCallError.setValue(false);
        updateQuantityApiCallSuccess.setValue(false);
    }


    /*  public void updateQuantity() {
          JSONObject jsonObject = getUpdateInvoiceJsonRequest();
          if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
          System.out.println(jsonObject.toString());
          //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/updateDistributorIndent"
         //
          final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_UPDATE_DIST_INDENT);
          authenticator.setBody(jsonObject.toString());

          JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                  authenticator.getUri(),
                  jsonObject,
                  new Response.Listener<JSONObject>() {
                      @Override
                      public void onResponse(JSONObject response) {
                          if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                          updateQuantityApiCallRunning.postValue(false);
                          try {
                              String status = response.getString("status");
                              if (status.equalsIgnoreCase("success")) {
                                  updateQuantityApiCallSuccess.postValue(true);
                              } else {
                                  updateQuantityApiCallError.postValue(true);
                                  apiCallErrorMessage = response.getString("errorMessage");
                              }
                          } catch (JSONException e) {
                              Crashlytics.logException(e);
                              updateQuantityApiCallError.postValue(true);
                              apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                          }
                      }
                  }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                  updateQuantityApiCallRunning.postValue(false);
                  updateQuantityApiCallError.postValue(true);
                  apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
              }
          }) {
              @Override
              public Map<String, String> getHeaders() throws AuthFailureError {
                 *//* Map<String, String> params = new HashMap<String, String>();
                params.put("key", "76380346");
                params.put("Content-Type", "application/json");
                return params;*//*
                return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        updateQuantityApiCallRunning.setValue(true);
        updateQuantityApiCallError.setValue(false);
        updateQuantityApiCallSuccess.setValue(false);
    }
*/
    private JSONObject getUpdateInvoiceJsonRequest() {
        try {
            JSONArray jsonArrayUpdate = new JSONArray();

            long date = calender.getTime().getTime();
           // date = date-12*60*60*1000;
           // 1630261800000
           // 1630218600000
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("customerId", itemUpdated.getCustomerId());
                jsonObject.put("indentDate", String.valueOf(date));
                System.out.println("DATETRACE ::: + " +String.valueOf(date));
                jsonObject.put("merchantId", merchantId);

                List<DailyIndentSubscription> map = itemUpdated.getSubscriptions();

                JSONArray jsonArraySubscriptions = new JSONArray();
                for (int j = 0; j < map.size(); j++) {
                    JSONObject jsonObjSubscriptionsQuantity = new JSONObject();
                    if (map.get(j).getSubscriptionQuantity() != -1) {
                        jsonObjSubscriptionsQuantity.put("productId", map.get(j).getProductId());
                        jsonObjSubscriptionsQuantity.put("subscriptionQuantity", map.get(j).getSubscriptionQuantity());
                        jsonObjSubscriptionsQuantity.put("shortCode", map.get(j).getProductShortCode());
                        jsonArraySubscriptions.put(jsonObjSubscriptionsQuantity);
                    }
                }

                jsonObject.put("subscriptions", jsonArraySubscriptions);

                jsonArrayUpdate.put(jsonObject);


            JSONObject obj = new JSONObject();
            obj.put("data", jsonArrayUpdate.toString());

            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

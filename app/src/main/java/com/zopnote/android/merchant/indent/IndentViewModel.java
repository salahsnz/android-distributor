package com.zopnote.android.merchant.indent;

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
import com.zopnote.android.merchant.data.model.DailySubscription;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class IndentViewModel extends AndroidViewModel {
    private static final boolean DEBUG = false;
    private String LOG_TAG = "IndentViewModel";

    private Repository repository;

    public LiveData<Merchant> merchant;
    public String merchantId;
    public List<DailySubscription> indentReport;
    public Calendar purchaseCalender;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();
    public MutableLiveData<Boolean> dateChanged = new MutableLiveData<>();

    public MutableLiveData<Boolean> canSavePdf = new MutableLiveData<>();
    public MutableLiveData<String> reportPdfUri = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public MutableLiveData<Boolean> indentTypeChanged = new MutableLiveData<>();
    public String indentType;

    public IndentViewModel(@NonNull Application context, Repository repository) {
        super(context);
        this.repository = repository;
    }

    public void init(){
        if(merchant != null){
            return;
        }
        purchaseCalender = FormatUtil.getLocalCalenderNoTime();
        merchant = repository.getMerchant();
        indentReport = new ArrayList();

        apiCallRunning.setValue(false);
        apiCallError.setValue(false);
        apiCallSuccess.setValue(false);
        networkError.setValue(false);
    }

    public void getIndentReport() {
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_INDENT_REPORT);

        authenticator.addParameter(Param.MERCHANT_ID, merchantId);
        authenticator.addParameter(Param.DATE, String.valueOf(purchaseCalender.getTime().getTime()));

        JsonArrayRequest volleyRequest = new JsonArrayRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());

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

    private void saveIndentReport(JSONArray response) {
        try {
            indentReport.clear();

            for (int i = 0; i < response.length(); i++) {
                JSONObject reportItemObject = (JSONObject) response.get(i);

                DailySubscription dailySubscription = new DailySubscription();

                dailySubscription.setName(reportItemObject.getString("name"));

                dailySubscription.setRoute(reportItemObject.getString("route"));

                dailySubscription.setAddressLine2(reportItemObject.getString("addressLine2"));

                dailySubscription.setType(reportItemObject.getString("type"));

                dailySubscription.setActiveCount(reportItemObject.getInt("activeCount"));

                if( ! reportItemObject.isNull("pauseCount")){
                    dailySubscription.setPauseCount(reportItemObject.getInt("pauseCount"));
                }/*else{
                    //magazines will not have pause
                    dailySubscription.setPauseCount(0);
                }*/
                System.out.println("Route  "  + dailySubscription.getRoute());
                System.out.println("addressLine2  "  + dailySubscription.getAddressLine2());
                dailySubscription.setProcureCount(dailySubscription.getActiveCount() - dailySubscription.getPauseCount());

                indentReport.add(dailySubscription);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

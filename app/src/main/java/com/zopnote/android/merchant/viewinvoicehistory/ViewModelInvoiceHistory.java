package com.zopnote.android.merchant.viewinvoicehistory;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import java.util.List;

public class ViewModelInvoiceHistory extends AndroidViewModel {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "ViewModelInvoiceHistory";

    private Context context;
    private Repository repository;


    public LiveData<List<Invoice>> invoices;
    public LiveData<Merchant> merchant;
    public String customerId;
    public String merchantName;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();

    public String apiCallErrorMessage;

    public ViewModelInvoiceHistory(Application context, Repository repository) {
        super(context);
        this.context = context;
        this.repository = repository;
    }

    public void init(String customerId) {

        merchant = repository.getMerchant();
        invoices = repository.getInvoices(customerId);

    }


}
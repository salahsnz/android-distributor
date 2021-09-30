package com.zopnote.android.merchant.managesubscription.viewcustomization;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.data.repository.Repository;

public class ViewCustomizationViewModel extends AndroidViewModel {
    private static String LOG_TAG = ViewCustomizationViewModel.class.getSimpleName();
    private static boolean DEBUG = false;

    private final Repository repository;
    public LiveData<Merchant> merchant;
    public LiveData<Subscription> subscription;
    public String customerId;

    public ViewCustomizationViewModel(@NonNull Application application, Repository repository) {
        super(application);
        this.repository = repository;
    }

    public void init(String subscriptionId, String customerId){
        if (subscription != null) {
            return;
        }

        merchant = repository.getMerchant();
        subscription = repository.getSubscription(subscriptionId);
        this.customerId = customerId;
    }
}

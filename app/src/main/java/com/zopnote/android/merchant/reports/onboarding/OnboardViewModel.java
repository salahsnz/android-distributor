package com.zopnote.android.merchant.reports.onboarding;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnboardViewModel extends AndroidViewModel {
    private Repository repository;

    public LiveData<Merchant> merchant;
    public LiveData<List<Customer>> customers;

    public OnboardViewModel(@NonNull Application application, Repository repository) {
        super(application);
        this.repository = repository;
    }

    public void init(){

        if(customers != null){
            return;
        }

        customers = repository.getCustomers();
        merchant = repository.getMerchant();
    }
}

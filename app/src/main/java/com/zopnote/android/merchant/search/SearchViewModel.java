package com.zopnote.android.merchant.search;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private Repository repository;
    public LiveData<Merchant> merchant;
    public LiveData<List<Customer>> customers;
    public List<Customer> customerArrayList;
    public MutableLiveData<Boolean> customersLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> databaseSynced = new MutableLiveData<>();

    public SearchViewModel(Application context, Repository repository) {
        super(context);
        this.repository = repository;
        customersLoading.setValue(false);
        databaseSynced.setValue(false);
        customerArrayList = new ArrayList<>();
    }

    public void init() {
        if (customers != null) {
            return;
        }

        customersLoading.setValue(true);
        customers = repository.getCustomers();

    }
}

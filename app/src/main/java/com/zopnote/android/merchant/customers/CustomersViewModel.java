package com.zopnote.android.merchant.customers;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;

import java.util.List;

/**
 * Created by nmohideen on 03/02/18.
 */

public class CustomersViewModel extends AndroidViewModel {

    private Repository repository;

    public LiveData<Merchant> merchant;
    public LiveData<List<Customer>> customers;

    public CustomersViewModel(Application context, Repository repository) {
        super(context);
        this.repository = repository;

    }

    public void init() {
        if (customers != null) {
            return;
        }

        customers = repository.getCustomers();
        merchant = repository.getMerchant();
    }

    public LiveData<List<Customer>> getCustomers(String route){
        return repository.getCustomers(route);
    }
}
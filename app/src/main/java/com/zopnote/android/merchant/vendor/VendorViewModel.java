package com.zopnote.android.merchant.vendor;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;

import java.util.List;

public class VendorViewModel extends AndroidViewModel {
    private Repository repository;
    public LiveData<List<Merchant>> merchants;

    public VendorViewModel(@NonNull Application context,  Repository repository) {
        super(context);
        this.repository = repository;
    }

    public void init(){
        if(merchants != null){
            return;
        }
        merchants = repository.getMerchants();
    }
}

package com.zopnote.android.merchant.products.addproduct;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.zopnote.android.merchant.data.model.GenericProduct;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.data.repository.Repository;

import java.util.List;

public class AddProductViewModel extends AndroidViewModel {
    private Context context;
    private Repository repository;
   /* public LiveData<List<Product>> newspapers;
    public LiveData<List<Product>> magazines;*/
    public LiveData<List<GenericProduct>> allNewspapers;
    public LiveData<List<GenericProduct>> allMagazines;
    public LiveData<Merchant> merchant;

    public AddProductViewModel(Application context, Repository repository) {
        super(context);
        this.context = context;
        this.repository = repository;
    }

    public void init() {
        if (merchant != null) {
            return;
        }
        merchant = repository.getMerchant();

       // newspapers = repository.getNewsPapers();
       // magazines = repository.getMagazines();

       // allNewspapers = repository.getAllNewsPapers();
       // allMagazines = repository.getAllMagazines();
    }


    public LiveData<List<Product>> getOfferedProduct(String productType){
        return repository.getOfferedProduct(productType);
    }
    public LiveData<List<GenericProduct>> getAllProduct(String productType){
        return repository.getAllProduct(productType);
    }
}

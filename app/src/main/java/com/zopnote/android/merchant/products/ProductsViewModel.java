package com.zopnote.android.merchant.products;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.data.repository.Repository;

import java.util.List;

public class ProductsViewModel extends AndroidViewModel {
    private Context context;
    private Repository repository;
  /*  public LiveData<List<Product>> newspapers;
    public LiveData<List<Product>> magazines;*/

    public LiveData<Merchant> merchant;
    public ProductsViewModel(Application context, Repository repository) {
        super(context);
        this.context = context;
        this.repository = repository;
    }

    public void init() {
        if (merchant != null) {
            return;
        }

        /*newspapers = repository.getNewsPapers();
        magazines = repository.getMagazines();*/
        merchant = repository.getMerchant();

    }

    public LiveData<List<Product>> getOfferedProduct(String productType){
        return repository.getOfferedProduct(productType);
    }
}

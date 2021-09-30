package com.zopnote.android.merchant.products;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.ProductsActBinding;
import com.zopnote.android.merchant.products.addproduct.AddProductActivity;
import com.zopnote.android.merchant.util.Extras;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private ProductsActBinding binding;
    private ProductsViewModel viewmodel;
    private List<String> tabs;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.products_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();

        setupTabs();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupTabs() {
        tabs = new ArrayList<>(10);
        /*tabs.add(new Tab(getResources().getString(R.string.newspapers_label), "newspapers"));
        tabs.add(new Tab(getResources().getString(R.string.magazines_label), "magazines"));*/

        final ProductsPagerAdapter productsPagerAdapter = new ProductsPagerAdapter(getSupportFragmentManager(), tabs);

        viewPager = binding.viewpager;
        viewPager.setAdapter(productsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                setScreenName();
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                List<String> productList = merchant.getProductList();

                for (int i= 0; i<productList.size(); i++){
                    String productName = productList.get(i);
                    if( !tabs.contains(productName)){
                        tabs.add(productName);
                        productsPagerAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }

    private void setScreenName() {
        int currentItem = binding.viewpager.getCurrentItem();
        String currentTab = tabs.get(currentItem);
        String screenName = String.format("Products - %s", currentTab);

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, screenName, "ProductsActivity");
    }

    public static ProductsViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        ProductsViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(ProductsViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.VIEW_PRODUCTS, "ProductsActivity");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.add_product_menu_item:
                Intent intent = new Intent(this, AddProductActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class Tab {
        public String title;
        public String type;

        public Tab(String title, String type) {
            this.title = title;
            this.type = type;
        }
    }
}

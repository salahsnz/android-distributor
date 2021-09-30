package com.zopnote.android.merchant.merchantsetup;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.ShopSetupFragBinding;
import com.zopnote.android.merchant.home.HomeActivity;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.util.VolleyManager;

import java.io.IOException;

public class ShopSetupFragment extends Fragment {

    private ShopSetupFragBinding ShopSetupFragBinding;
    //private ShopSetup ShopSetupFragBinding;
    private ShopSetupViewModel viewmodel;
    private RadioButton radioButton;
    private ImageLoader imageLoader;
    private Bitmap photo;

    public ShopSetupFragment() {
        // Required empty public constructor
    }

    public static ShopSetupFragment newInstance() {
        ShopSetupFragment fragment = new ShopSetupFragment();
        Log.d("CSD","ShopSetupFragment");

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ShopSetupFragBinding = ShopSetupFragBinding.inflate(inflater, container, false);
        Log.d("CSD","ON CREATE VIEW");

        return ShopSetupFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();
        viewmodel = ShopSetupActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchant.removeObserver(this);

                imageLoader = VolleyManager.getInstance(getActivity()).getImageLoader();
                displayServiceImages();
                getProfileData();
            }
        });

        ShopSetupFragBinding.submitShopSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.enforceNetworkConnection(getContext())) {
                    getReports();
                }
            }
        });

        ShopSetupFragBinding.skipShopSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.enforceNetworkConnection(getContext())) {
                    startActivity(new Intent(getContext(), ProductSetupActivity.class));
                }
            }
        });

        ShopSetupFragBinding.closeShopSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getResources()!= null)
                    getActivity().finish();

                startActivity(new Intent(getContext(), HomeActivity.class));
            }
        });

        ShopSetupFragBinding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  getReports();
            }
        });

        ShopSetupFragBinding.radioGroupServices.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(radioGroup.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(getContext(), "Please select a Service", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) ShopSetupFragBinding.radioGroupServices.findViewById(selectedId);
                    int idx = radioGroup.indexOfChild(radioButton);

                    RadioButton r = (RadioButton) radioGroup.getChildAt(idx);
                    String selectedtext = null;
                    selectedtext = radioButton.getTransitionName();
                    viewmodel.serviceType=selectedtext;

                    Log.d("CSD","---"+selectedtext);
                }
            }
        });

        ShopSetupFragBinding.radioGroupPricing.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(radioGroup.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(getContext(), "Please select Pricing ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) ShopSetupFragBinding.radioGroupPricing.findViewById(selectedId);
                    int idx = radioGroup.indexOfChild(radioButton);

                    RadioButton r = (RadioButton) radioGroup.getChildAt(idx);
                    String selectedtext = null;
                    selectedtext = radioButton.getText().toString();

                    viewmodel.merchantBilling=selectedtext;

                    Log.d("CSD","---"+selectedtext);
                }
            }
        });

        ShopSetupFragBinding.radioGroupPaidType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(radioGroup.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(getContext(), "Please select Paid Type ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) ShopSetupFragBinding.radioGroupPaidType.findViewById(selectedId);
                    int idx = radioGroup.indexOfChild(radioButton);

                    RadioButton r = (RadioButton) radioGroup.getChildAt(idx);
                    String selectedtext = null;
                    selectedtext = radioButton.getText().toString();

                    if (selectedtext.equals("Pre-Paid"))
                        viewmodel.merchantBillingType="No";
                    else
                        viewmodel.merchantBillingType="Yes";

                    Log.d("CSD","---"+selectedtext);
                }
            }
        });

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    setStatusLoading();
                    viewmodel.apiCallRunning.setValue(false);
                }
            }
        });

        viewmodel.apiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    ShopSetupFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
                    setStatusNetworkError();
                    viewmodel.apiCallError.setValue(false);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    if(viewmodel.merchantModel != null){
                        setData();
                        setStatusReady();
                    }else{
                        setStatusEmpty();
                    }
                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });

        viewmodel.addShopSetupApiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    setStatusLoading();
                    viewmodel.addShopSetupApiCallRunning.setValue(false);
                }
            }
        });

        viewmodel.addShopSetupApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.addShopSetupApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            getActivity().getResources().getString(R.string.shop_setup_added_success_message),
                            Toast.LENGTH_LONG);

                    Intent intent = new Intent(getContext(), ProductSetupActivity.class);
                    intent.putExtra(Extras.SERVICE_TYPE, viewmodel.serviceType);
                    getContext().startActivity(intent);
                }
            }
        });
    }

    private void displayServiceImages()
    {
        String newsPaper="https://s3-ap-southeast-1.amazonaws.com/com.zopnote.content/images/newspaper-service.png";
        String grocery="https://s3-ap-southeast-1.amazonaws.com/com.zopnote.content/images/kirana-service.png";
        String laundry="https://s3-ap-southeast-1.amazonaws.com/com.zopnote.content/images/dhobi-service.png";

        showNewspaper(newsPaper);
        showGrocery(grocery);
        showLaundry(laundry);
    }

    private void showNewspaper(String img)
    {
        photo=decodeFile(img);
        imageLoader.get(img, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CSD","ERROR");
            }
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    photo=response.getBitmap();
                    ShopSetupFragBinding.imgNewspaper.setImageBitmap(photo);
                }
            }
        });
    }

    private void showGrocery(String img)
    {
        photo=decodeFile(img);

        imageLoader.get(img, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CSD","ERROR");
            }
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    photo=response.getBitmap();
                    ShopSetupFragBinding.imgGrocery.setImageBitmap(photo);
                }
            }
        });
    }

    private void showLaundry(String img)
    {
        photo=decodeFile(img);

        imageLoader.get(img, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CSD","ERROR");
            }
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    photo=response.getBitmap();
                    ShopSetupFragBinding.imgLaundry.setImageBitmap(photo);
                }
            }
        });
    }

    private void getProfileData()
    {
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.getProfileData();
        }
        else{
            viewmodel.networkError.postValue(true);
            ShopSetupFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    private void setData() {
        ShopSetupFragBinding.merchantName.setText(viewmodel.merchantModel.getOwnerName());
        ShopSetupFragBinding.merchantBusinessName.setText(viewmodel.merchantModel.getName());

        if (viewmodel.profileImageAlreadyExists) {
            String profilePicUrl = viewmodel.merchantModel.getProfilePicUrl();
            retrieveProfilePic(profilePicUrl);
        }

        if (viewmodel.product.getName()!=null) { //First Time this will be null
            if (viewmodel.product.getName().equalsIgnoreCase(getResources().getString(R.string.grocery_label)))  //OnDemand
                ShopSetupFragBinding.radioButtonGrocery.setChecked(true);
            else if (viewmodel.product.getName().equalsIgnoreCase(getResources().getString(R.string.newspaper_label))) //Subscription
                ShopSetupFragBinding.radioButtonNewsPaper.setChecked(true);
            else if (viewmodel.product.getName().equalsIgnoreCase(getResources().getString(R.string.laundry_label))) //Laundry
                ShopSetupFragBinding.radioButtonDhobi.setChecked(true);
            else if (viewmodel.product.getName().equalsIgnoreCase(getResources().getString(R.string.distributor_label))) // Distributor
                ShopSetupFragBinding.radioButtonDistri.setChecked(true);
        }
    }

    public void retrieveProfilePic(String profilePicUrl) {

        if (NetworkUtil.isNetworkAvailable(getActivity())) {

            imageLoader.get(profilePicUrl, new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("CSD","ERROR"+error.toString());
                }
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        photo=response.getBitmap();
                        ShopSetupFragBinding.profilePic.setImageBitmap(photo);
                        Log.d("CSD","SUCCESS "+response.getBitmap().getWidth());
                    }
                }
            });
            photo = decodeFile(profilePicUrl);
        }else{
            viewmodel.networkError.postValue(true);
            ShopSetupFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    public Bitmap decodeFile(String filePath) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, o);

        Log.d("CSD","FILE PATH "+filePath);
// The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

// Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        Log.d("CSD",String.valueOf(width_tmp)+"---"+String.valueOf(height_tmp));
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

// Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap image = BitmapFactory.decodeFile(filePath, o2);

        ExifInterface exif;
        try
        {
            exif = new ExifInterface(filePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int rotate = 0;
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            if (rotate != 0) {
                int w = image.getWidth();
                int h = image.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap & convert to ARGB_8888, required by tess
                image = Bitmap.createBitmap(image, 0, 0, w, h, mtx, false);

            }
        } catch (IOException e) {
            return null;
        }
        return image.copy(Bitmap.Config.ARGB_8888, true);
    }

    public void getReports() {

        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.submitService();
           // viewmodel.getRouteInfo();
            //viewmodel.getMerchantRoutes();
        }else{
            viewmodel.networkError.postValue(true);
            ShopSetupFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }


    private void setStatusNetworkError () {
       // ShopSetupFragBinding.contentView.setVisibility(View.GONE);
      //  ShopSetupFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        ShopSetupFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        ShopSetupFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading () {
      //  ShopSetupFragBinding.contentView.setVisibility(View.VISIBLE);
      //  ShopSetupFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        ShopSetupFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        ShopSetupFragBinding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmpty () {
       // ShopSetupFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
        ShopSetupFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        ShopSetupFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady () {
       // ShopSetupFragBinding.contentView.setVisibility(View.VISIBLE);
        ShopSetupFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        ShopSetupFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

}
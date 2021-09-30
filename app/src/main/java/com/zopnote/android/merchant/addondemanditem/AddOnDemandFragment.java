package com.zopnote.android.merchant.addondemanditem;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.AddOndemandItemFragBinding;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by salah on 24/12/19.
 */

public class AddOnDemandFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int CAMERA_REQUEST = 107;
    private AddOndemandItemFragBinding binding;
    private AddOnDemandViewModel viewmodel;
    private Bitmap photo;
    private ProgressDialog progressDialog;
    private Double totalAmount;
    public AddOnDemandFragment() {
        // Requires empty public constructor
    }

    public static AddOnDemandFragment newInstance() {
        AddOnDemandFragment fragment = new AddOnDemandFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AddOndemandItemFragBinding.inflate(inflater, container, false);


        
        return binding.getRoot();
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = AddOnDemandActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                // required to fetch data

                getReports();
               viewmodel.merchant.removeObserver(this);
            }
        });



        viewmodel.customer.observe(this, new Observer<Customer>() {
            @Override
            public void onChanged(@Nullable Customer customer) {


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
                    binding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
                    setStatusNetworkError();
                    viewmodel.apiCallError.setValue(false);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    if(viewmodel.onDemandProduct != null){
                        addProductView();
                        addConvenienceCharge();
                        setStatusReady();
                    }else{
                        setStatusEmpty();
                    }

                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });

        binding.submitOnDemandItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.enforceConnection(AddOnDemandFragment.this.getActivity()))
                    if (totalAmount != 0) {
                        if (isValidConvenienceCharge(binding.convenienceCharge))
                            viewmodel.convenienceCharges = Integer.parseInt(binding.convenienceCharge.getText().toString());

                        if (binding.saveWithPaymentReq.isChecked())
                            viewmodel.addOndemandItem("pay");
                        else
                            viewmodel.addOndemandItem("save");

                    }else {
                        Toast.makeText(getActivity(),getString(R.string.invoice_amount_error_message),Toast.LENGTH_LONG).show();
                    }

            }
        });

        binding.submitWithPaymentLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.logEvent(Event.NAV_ACTION_ADD_ONDEMAND_ITEM);
                if (Utils.enforceConnection(AddOnDemandFragment.this.getActivity()))
                    if (totalAmount != 0) {
                        if (isValidConvenienceCharge(binding.convenienceCharge))
                            viewmodel.convenienceCharges = Integer.parseInt(binding.convenienceCharge.getText().toString());
                        viewmodel.addOndemandItem("pay");
                    }else {
                        Toast.makeText(getActivity(),getString(R.string.invoice_amount_error_message),Toast.LENGTH_LONG).show();
                    }
            }
        });

        binding.captureBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndCallCamera();
            }
        });
        binding.ivBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        binding.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewmodel.billCaptured =false;
                viewmodel.encodedString = null;
                binding.ivBill.setImageBitmap(null);
                binding.viewBillRelative.setVisibility(View.GONE);
            }
        });

        setupApiCallObservers();
    }

    private void openDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddOnDemandFragment.this.getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.bill_image_preview_frag, null);

        final ImageView billImg = view.findViewById(R.id.ivBillCopyImg);
        billImg.setVisibility(View.VISIBLE);
        billImg.setImageBitmap(photo);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();
        view.findViewById(R.id.cancelPreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }
    public void getReports() {

        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.getOnDemandItems();
        }else{
            viewmodel.networkError.postValue(true);
            binding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    private void addConvenienceCharge()
    {
        final EditText ccAmount = binding.convenienceCharge;

        ccAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    Double amount = 0.00;
                    if (isValidItemAmount(ccAmount))
                        amount = Double.valueOf(ccAmount.getText().toString());

                    viewmodel.productItemMap.put("Convenience Charge",amount);

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    setTotalPrice();
                }
            });

    }

    private void addProductView() {
        binding.productItemContainer.removeAllViews();
        binding.productItemContainer.removeAllViews();
        viewmodel.productItemMap = new HashMap<>();

        setTotalPrice();
        for (String prod : viewmodel.onDemandProduct){
            View view = LayoutInflater.from(AddOnDemandFragment.this.getActivity()).inflate(R.layout.custom_product_items, null);
            final TextView tvItems = view.findViewById(R.id.name);
            tvItems.setText(prod);
            final EditText etAmount =  view.findViewById(R.id.perItemPrice);
            //final EditText ccAmount = view.findViewById(R.id.convenienceCharge);

            etAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    Double amount = 0.00;
                    if (isValidItemAmount(etAmount))
                        amount = Double.valueOf(etAmount.getText().toString());

                    viewmodel.productItemMap.put(tvItems.getText().toString(),amount);

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    setTotalPrice();
                }
            });



            binding.productItemContainer.addView(view);
        }

    }

    private void setTotalPrice() {
        totalAmount = 0.00;

        for (Double price : viewmodel.productItemMap.values()){
            totalAmount = totalAmount + price;
        }
        String formattedIssuePrice = FormatUtil.getRupeePrefixedAmount(getContext(), totalAmount, FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        binding.totalItemPrice.setText(formattedIssuePrice);
    }

    private boolean isValidItemAmount(EditText amountEditText){
        if( ! amountEditText.getText().toString().trim().isEmpty()){
            try {
                Double.parseDouble(amountEditText.getText().toString());
                return true;
            }catch (Exception ex){
               // amountEditText.setError(getString(R.string.invoice_amount_error_message));
                return false;
            }
        }else{
           // amountEditText.setError(getString(R.string.invoice_amount_error_message));
            return false;
        }
    }

    private boolean isValidConvenienceCharge(EditText amountEditText){
        if( ! amountEditText.getText().toString().trim().isEmpty()){
            try {
                Integer.parseInt(amountEditText.getText().toString());
                return true;
            }catch (Exception ex){
                return false;
            }
        }else{
            return false;
        }
    }

    private void setupApiCallObservers() {
        viewmodel.addOndemandApiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(AddOnDemandFragment.this.getActivity());
                    progressDialog.setMessage(AddOnDemandFragment.this.getActivity().getResources().getString(R.string.update_invoice_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.addOndemandApiCallRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.addOndemandApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(AddOnDemandFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.addOndemandApiCallError.setValue(false);
                }
            }
        });

        viewmodel.addOndemandApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {

                    Utils.showSuccessToast(AddOnDemandFragment.this.getActivity(),
                            AddOnDemandFragment.this.getActivity().getResources().getString(R.string.update_invoice_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.addOndemandApiCallSuccess.setValue(false);
                    AddOnDemandFragment.this.getActivity().finish();
                }
            }
        });

    }



    private void checkPermissionAndCallCamera() {
            if (ActivityCompat.checkSelfPermission(AddOnDemandFragment.this.getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
               openCamera();
            } else {
                ActivityCompat.requestPermissions(AddOnDemandFragment.this.getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(AddOnDemandFragment.this.getActivity(),R.string.permissions_camera_permission_settings_instruction, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void openCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.myfileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }

    }
    String currentPhotoPath;

    private File createImageFile() throws IOException {
        String imageFileName = "BillImage";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {


            binding.viewBillRelative.setVisibility(View.VISIBLE);
            viewmodel.billCaptured = true;



            photo = decodeFile(currentPhotoPath);
            binding.ivBill.setImageBitmap(photo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            byte[] byte_arr = out.toByteArray();

            viewmodel.encodedString = Base64.encodeToString(byte_arr, Base64.NO_WRAP);



        }
    }

    public Bitmap decodeFile(String filePath) {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

// The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

// Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
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

    private void setStatusNetworkError() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmpty() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady() {
        binding.contentView.setVisibility(View.VISIBLE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }
}

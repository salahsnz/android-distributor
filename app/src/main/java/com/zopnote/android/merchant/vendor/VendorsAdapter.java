package com.zopnote.android.merchant.vendor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.remote.FirestoreDataSource;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.databinding.VendorItemBinding;
import com.zopnote.android.merchant.home.HomeActivity;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.ObjectsUtil;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.util.VolleyManager;

import java.util.List;

public class VendorsAdapter extends BaseRecyclerAdapter <Merchant, VendorsAdapter.VendorViewHolder>{
    private ImageLoader imageLoader;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    public VendorsAdapter(Context context) {
        super(context);
        imageLoader = VolleyManager.getInstance(context).getImageLoader();
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<Merchant> newItems) {
        Merchant oldItem = getItems().get(oldItemPosition);
        Merchant newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<Merchant> newItems) {
        Merchant oldItem = getItems().get(oldItemPosition);
        Merchant newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(newItem.getId(), oldItem.getId()) &&
                ObjectsUtil.equals(newItem.getName(), oldItem.getName()) &&
                ObjectsUtil.equals(newItem.getOwnerName(), oldItem.getOwnerName()) &&
                ObjectsUtil.equals(newItem.getProfilePicUrl(), oldItem.getProfilePicUrl());
    }

    @Override
    public VendorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VendorItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.vendor_item,
                        parent, false);
        VendorViewHolder vh = new VendorViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(VendorViewHolder holder, int position) {
        final Merchant merchant = getItems().get(position);
        holder.itemBinding.ownerName.setText(merchant.getOwnerName());
        holder.itemBinding.name.setText(merchant.getName());
        holder.itemBinding.contactNumber.setText(merchant.getContactNumber());

        holder.itemBinding.profilePic.setErrorImageResId(R.drawable.ic_broken_image_black_24dp);
        holder.itemBinding.profilePic.setDefaultImageResId(R.drawable.ic_person_black_24dp);
        holder.itemBinding.profilePic.setImageUrl(merchant.getProfilePicUrl(), imageLoader); //null url accepted

        holder.itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchVendor(merchant);
            }
        });

        holder.itemBinding.contactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndCall(merchant.getContactNumber());
            }
        });
        holder.itemBinding.messageWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickWhatsApp(merchant.getOwnerName(),merchant.getContactNumber());
            }
        });
    }

    private void switchVendor(Merchant merchant) {
        Prefs.putString(AppConstants.PREF_VENDOR_UID, merchant.getId());

        clearCachedData();

        Toast.makeText(getContext(), R.string.vendor_switch_success,Toast.LENGTH_SHORT).show();
    }

    private void clearCachedData() {
        Repository.destroyInstance();
        FirestoreDataSource.destroyInstance();
        ViewModelFactory.destroyInstance();

        clearHomeActivity();
    }

    private void clearHomeActivity() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(intent);
    }

    private void checkPermissionAndCall(String contactNumber) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            callCustomer(contactNumber);
        } else {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }

    }
    public void onClickWhatsApp(String vendorName, String contactNumber) {

        try {
            String text = "Hi " + vendorName + ", ";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + contactNumber + "&text=" + text));
            getContext().startActivity(intent);


        }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getContext(),"it may be you dont have whats app",Toast.LENGTH_LONG).show();

            }

    }

    @SuppressLint("MissingPermission")
    private void callCustomer(String contactNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + contactNumber));
        getContext().startActivity(intent);
    }

    public class VendorViewHolder extends RecyclerView.ViewHolder {
        final VendorItemBinding itemBinding;

        public VendorViewHolder(VendorItemBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }
    }
}

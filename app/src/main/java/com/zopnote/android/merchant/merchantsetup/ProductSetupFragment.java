package com.zopnote.android.merchant.merchantsetup;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.ProductSetupFragBinding;
import com.zopnote.android.merchant.home.HomeActivity;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.util.VolleyManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ProductSetupFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int CAMERA_REQUEST = 107;
    public static final int RESULT_GALLERY = 1;
    private ProductSetupFragBinding ProductSetupFragBinding;
    private ProductSetupViewModel viewmodel;
    private ImageLoader imageLoader;
    private Bitmap photo;

    public ProductSetupFragment() {
        // Required empty public constructor
    }

    public static ProductSetupFragment newInstance() {
        ProductSetupFragment fragment = new ProductSetupFragment();
        Log.d("CSD","ProductSetupFragment");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ProductSetupFragBinding = ProductSetupFragBinding.inflate(inflater, container, false);

        return ProductSetupFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();

        viewmodel = ProductSetupActivity.obtainViewModel(getActivity());
        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantId = merchant.getId();

                imageLoader = VolleyManager.getInstance(getActivity()).getImageLoader();
                getProfileData();
                ProductSetupFragBinding.productType.setText(viewmodel.productType);

                viewmodel.merchant.removeObserver(this);
            }
        });


        ProductSetupFragBinding.addProductSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAllFields()) {
                    if (NetworkUtil.enforceNetworkConnection(getContext())) {
                        //viewmodel.route= String.valueOf(ProductSetupFragBinding.editTXTaddRoute.getText());
                        getReports();
                        //viewmodel.addCustomer();
                    }
                }
            }
        });

        ProductSetupFragBinding.closeProductSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getResources()!= null)
                    getActivity().finish();

                startActivity(new Intent(getContext(), HomeActivity.class));
            }
        });

        ProductSetupFragBinding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  getReports();
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
                    ProductSetupFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
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

        viewmodel.addProductSetupApiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    setStatusLoading();
                    viewmodel.addProductSetupApiCallRunning.setValue(false);
                }
            }
        });

        viewmodel.addProductSetupApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.addProductSetupApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            getActivity().getResources().getString(R.string.add_product_success_message),
                            Toast.LENGTH_LONG);
                    clearFields();
                    viewmodel.addProductSetupApiCallSuccess.setValue(false);
                    setStatusEmpty();
                    //startActivity(new Intent(getContext(), MerchantKYCActivity.class));
                }
            }
        });

        ProductSetupFragBinding.takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndCallCamera();
            }
        });

        ProductSetupFragBinding.pickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openGallery();
                checkPermissionAndOpenGallery();
            }
        });

        ProductSetupFragBinding.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewmodel.profileImageCaptured =false;
                viewmodel.encodedString = "";

                ProductSetupFragBinding.ivBill.setImageBitmap(null);
                ProductSetupFragBinding.deleteImg.setVisibility(View.GONE);
                //ProductSetupFragBinding.viewBillRelative.setVisibility(View.GONE);
            }
        });

        ProductSetupFragBinding.ivBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    private void clearFields()
    {
        Log.d("CSD","CLEAR FIELDS");
        ProductSetupFragBinding.productName.setText("");
        ProductSetupFragBinding.productPrice.setText("");

        viewmodel.profileImageCaptured =false;
        viewmodel.encodedString = "";

        ProductSetupFragBinding.ivBill.setImageBitmap(null);
        ProductSetupFragBinding.deleteImg.setVisibility(View.GONE);
    }

    private void checkPermissionAndCallCamera() {
        if (ActivityCompat.checkSelfPermission(ProductSetupFragment.this.getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(ProductSetupFragment.this.getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private void checkPermissionAndOpenGallery() {
        if (ActivityCompat.checkSelfPermission(ProductSetupFragment.this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            ActivityCompat.requestPermissions(ProductSetupFragment.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RESULT_GALLERY);
        }
    }

    private void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        //galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), RESULT_GALLERY);
        startActivityForResult(galleryIntent , RESULT_GALLERY );
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
        String imageFileName = "ProfileImage";
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
        switch (requestCode) {
            case CAMERA_REQUEST:
                takePicture(requestCode,resultCode, data);
                break;
            case RESULT_GALLERY:
                imageSelect(requestCode,resultCode, data);
                break;
        }
    }

    public void takePicture(int requestCode, int resultCode, Intent data)
    {
        Log.d("CSD","INSIDE TAKE PIC");
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            ProductSetupFragBinding.viewBillRelative.setVisibility(View.VISIBLE);
            ProductSetupFragBinding.deleteImg.setVisibility(View.VISIBLE);
            viewmodel.profileImageCaptured = true;

            //  Uri photoURI=data.getData();
            // performCrop(photoURI);

            Log.d("CSD","CAMERA: "+currentPhotoPath);
            photo = decodeFile(currentPhotoPath);
            ProductSetupFragBinding.ivBill.setImageBitmap(photo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            byte[] byte_arr = out.toByteArray();

            viewmodel.encodedString = Base64.encodeToString(byte_arr, Base64.NO_WRAP);
        }
    }

    public void imageSelect(int requestCode, int resultCode, Intent data)
    {
        Log.d("CSD","INSIDE GALLERY PIC");
        if (requestCode == RESULT_GALLERY && resultCode == Activity.RESULT_OK) {
            viewmodel.profileImageCaptured = true;
            ProductSetupFragBinding.viewBillRelative.setVisibility(View.VISIBLE);
            ProductSetupFragBinding.deleteImg.setVisibility(View.VISIBLE);
            try {
                final Uri imageUri = data.getData();

                //final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                //final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                String path= getRealPathFromURI(imageUri);
                Log.d("CSD",">>>>> "+path);

                photo = decodeFile(path);
                ProductSetupFragBinding.ivBill.setImageBitmap(photo);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                byte[] byte_arr = out.toByteArray();

                viewmodel.encodedString = Base64.encodeToString(byte_arr, Base64.NO_WRAP);
            } catch (Exception exception) {
                Log.d("CSD",exception.toString());
                //Toast.makeText(PostImage.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor =getContext().getContentResolver().query( contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductSetupFragment.this.getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.bill_image_preview_frag, null);   //CHECK

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

    private boolean validateAllFields() {
        if(validate(ProductSetupFragBinding.productName) && validate(ProductSetupFragBinding.productPrice)){
            return true;
        }
        return false;
    }

    public boolean validate(EditText editText) {
        if(editText.equals(ProductSetupFragBinding.productName)){
            if (ProductSetupFragBinding.productName.getText().toString().trim().length() > 2) {
                viewmodel.productName=ProductSetupFragBinding.productName.getText().toString();
                return true;
            } else {
                ProductSetupFragBinding.productName.setError(getResources().getString(R.string.name_error_message));
                ProductSetupFragBinding.productName.requestFocus();
                return false;
            }
        }

        if(editText.equals(ProductSetupFragBinding.productPrice)){
            if (ProductSetupFragBinding.productPrice.getText().toString().trim().length() > 0) {
                viewmodel.productPrice=ProductSetupFragBinding.productPrice.getText().toString();
                return true;
            } else {
                ProductSetupFragBinding.productPrice.setError(getResources().getString(R.string.product_price_error_message));
                ProductSetupFragBinding.productPrice.requestFocus();
                return false;
            }
        }
        return false;
    }

    private void setData() {
        ProductSetupFragBinding.merchantName.setText(viewmodel.merchantModel.getOwnerName());
        ProductSetupFragBinding.merchantBusinessName.setText(viewmodel.merchantModel.getName());

        if (viewmodel.profileImageAlreadyExists) {
            String profilePicUrl = viewmodel.merchantModel.getProfilePicUrl();
            retrieveProfilePic(profilePicUrl);
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
                        ProductSetupFragBinding.profilePic.setImageBitmap(photo);
                        Log.d("CSD","SUCCESS "+response.getBitmap().getWidth());
                    }
                }
            });
            photo = decodeFile(profilePicUrl);
        }else{
            viewmodel.networkError.postValue(true);
            ProductSetupFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
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

    private void getProfileData()
    {
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.getProfileData();
        }
        else{
            viewmodel.networkError.postValue(true);
            ProductSetupFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }


    public void getReports() {
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.addProduct();
        }else{
            viewmodel.networkError.postValue(true);
            ProductSetupFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    private void setStatusNetworkError () {
       // ProductSetupFragBinding.contentView.setVisibility(View.GONE);
      //  ProductSetupFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        ProductSetupFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        ProductSetupFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading () {
      //  ProductSetupFragBinding.contentView.setVisibility(View.VISIBLE);
      //  ProductSetupFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        ProductSetupFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        ProductSetupFragBinding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmpty () {
       // ProductSetupFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
        ProductSetupFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        ProductSetupFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady () {
       // ProductSetupFragBinding.contentView.setVisibility(View.VISIBLE);
        ProductSetupFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        ProductSetupFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

}
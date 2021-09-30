package com.zopnote.android.merchant.merchantsetup;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.ActivityNotFoundException;
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
import com.zopnote.android.merchant.databinding.MerchantProfileFragBinding;
import com.zopnote.android.merchant.home.HomeActivity;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.util.VolleyManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MerchantProfileFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int CAMERA_REQUEST = 107;
    private MerchantProfileFragBinding merchantProfileFragBinding;
    private Bitmap photo;
    private MerchantProfileViewModel viewmodel;
    public static final int RESULT_GALLERY = 1;
    private ImageLoader imageLoader;
    final int PIC_CROP = 2;

    public MerchantProfileFragment() {
        // Required empty public constructor
    }

    public static MerchantProfileFragment newInstance() {
        MerchantProfileFragment fragment = new MerchantProfileFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        merchantProfileFragBinding = MerchantProfileFragBinding.inflate(inflater, container, false);

        return merchantProfileFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();
        String StepTitle1;
        StepTitle1=getResources().getString(R.string.merchant_setup_profile_step_profile);

        merchantProfileFragBinding.stepTitle.setText(StepTitle1);

        viewmodel = MerchantProfileActivity.obtainViewModel(getActivity());
        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {

                imageLoader = VolleyManager.getInstance(getActivity()).getImageLoader();

                getProfileData();
                viewmodel.merchant.removeObserver(this);
            }
        });

        merchantProfileFragBinding.submitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAllFields()) {
                    if (NetworkUtil.enforceNetworkConnection(getContext())) {
                       getReports();
                        //startActivity(new Intent(getContext(), MerchantKYCActivity.class));
                    }
                }
            }
        });

        merchantProfileFragBinding.submitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAllFields()) {
                    if (NetworkUtil.enforceNetworkConnection(getContext())) {
                        getReports();
                    }
                }
            }
        });

        merchantProfileFragBinding.skipProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.enforceNetworkConnection(getContext())) {
                    startActivity(new Intent(getContext(), MerchantKYCActivity.class));
                }
            }
        });


        merchantProfileFragBinding.closeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getResources()!= null)
                    getActivity().finish();

                startActivity(new Intent(getContext(), HomeActivity.class));
            }
        });

        merchantProfileFragBinding.takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndCallCamera();
            }
        });

        merchantProfileFragBinding.pickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openGallery();
                checkPermissionAndOpenGallery();
            }
        });

        merchantProfileFragBinding.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewmodel.profileImageCaptured =false;
                viewmodel.encodedString = "";

                merchantProfileFragBinding.ivBill.setImageBitmap(null);
                merchantProfileFragBinding.deleteImg.setVisibility(View.GONE);
                //merchantProfileFragBinding.viewBillRelative.setVisibility(View.GONE);
            }
        });

        merchantProfileFragBinding.ivBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
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
                    merchantProfileFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
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

        viewmodel.addMerchantApiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    setStatusLoading();
                    viewmodel.addMerchantApiCallRunning.setValue(false);
                }
            }
        });

        viewmodel.addMerchantApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.addMerchantApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            getActivity().getResources().getString(R.string.merchant_updated_success_message),
                            Toast.LENGTH_LONG);
                    //Prefs.putBoolean(AppConstants.PREFS_ACCEPT_MERCHANT_AGREMENT, true);
                    //getActivity().finish();
                    startActivity(new Intent(getContext(), MerchantKYCActivity.class));
                }
            }
        });

    }
    private void setData() {
        merchantProfileFragBinding.merchantName.setText(viewmodel.merchantModel.getOwnerName());
        merchantProfileFragBinding.merchantBusinessName.setText(viewmodel.merchantModel.getName());

        if (viewmodel.profileImageAlreadyExists) {
            String profilePicUrl = viewmodel.merchantModel.getProfilePicUrl();
            retrieveProfilePic(profilePicUrl);
        }
    }

    private void checkPermissionAndCallCamera() {
        if (ActivityCompat.checkSelfPermission(MerchantProfileFragment.this.getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(MerchantProfileFragment.this.getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private void checkPermissionAndOpenGallery() {
        if (ActivityCompat.checkSelfPermission(MerchantProfileFragment.this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            ActivityCompat.requestPermissions(MerchantProfileFragment.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RESULT_GALLERY);
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
            merchantProfileFragBinding.viewBillRelative.setVisibility(View.VISIBLE);
            merchantProfileFragBinding.deleteImg.setVisibility(View.VISIBLE);
            viewmodel.profileImageCaptured = true;

          //  Uri photoURI=data.getData();
           // performCrop(photoURI);

            Log.d("CSD","CAMERA: "+currentPhotoPath);
            photo = decodeFile(currentPhotoPath);
            merchantProfileFragBinding.ivBill.setImageBitmap(photo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            byte[] byte_arr = out.toByteArray();

            viewmodel.encodedString = Base64.encodeToString(byte_arr, Base64.NO_WRAP);
        }
    }


    private void performCrop(Uri photoURI)
    {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(photoURI, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);

        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            System.out.println(anfe.toString());
        }
    }

    public void imageSelect(int requestCode, int resultCode, Intent data)
    {
        Log.d("CSD","INSIDE GALLERY PIC");
        if (requestCode == RESULT_GALLERY && resultCode == Activity.RESULT_OK) {
            viewmodel.profileImageCaptured = true;
            merchantProfileFragBinding.viewBillRelative.setVisibility(View.VISIBLE);
            merchantProfileFragBinding.deleteImg.setVisibility(View.VISIBLE);
            try {
                final Uri imageUri = data.getData();

                //final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                //final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

              String path= getRealPathFromURI(imageUri);
              Log.d("CSD",">>>>> "+path);

              photo = decodeFile(path);
              merchantProfileFragBinding.ivBill.setImageBitmap(photo);

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

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MerchantProfileFragment.this.getActivity());

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

    public void getReports() {
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.submitProfile();
        }else{
            viewmodel.networkError.postValue(true);
            merchantProfileFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    public void retrieveProfilePic(String profilePicUrl) {

        if (NetworkUtil.isNetworkAvailable(getActivity())) {

            //Note: Using billImg causing wierd issue, - image not seems to load, working for fragbinding see below
            //LayoutInflater inflater = LayoutInflater.from(getActivity());
            //final View view1 = inflater.inflate(R.layout.merchant_profile_frag, null);
            // final NetworkImageView billImg = view1.findViewById(R.id.iv_bill);

            if ((profilePicUrl.length() == 0) || (profilePicUrl.isEmpty()) || (profilePicUrl.endsWith("zopnote_logo.png"))) {
                merchantProfileFragBinding.deleteImg.setVisibility(View.GONE);
              //  merchantProfileFragBinding.ivBill.setDefaultImageResId(R.drawable.ic_person_black_48dp);
            }
            else {
                merchantProfileFragBinding.deleteImg.setVisibility(View.VISIBLE);
            }
                imageLoader.get(profilePicUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("CSD","ERROR"+error.toString());
                    }
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            merchantProfileFragBinding.ivBill.setImageBitmap(response.getBitmap());
                            photo=response.getBitmap();
                            Log.d("CSD","SUCCESS "+response.getBitmap().getWidth());
                        }
                    }
                });
                photo = decodeFile(profilePicUrl);
           // }
        }else{
            viewmodel.networkError.postValue(true);
            merchantProfileFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }


    private boolean validateAllFields() {
        if(validate(merchantProfileFragBinding.merchantName) && validate(merchantProfileFragBinding.merchantBusinessName)){
            return true;
        }
        return false;
    }

    public boolean validate(EditText editText) {
        if(editText.equals(merchantProfileFragBinding.merchantName)){
            if (merchantProfileFragBinding.merchantName.getText().toString().trim().length() > 2) {
                    viewmodel.merchantName=merchantProfileFragBinding.merchantName.getText().toString();
                    return true;
            } else {
                merchantProfileFragBinding.merchantName.setError(getResources().getString(R.string.name_error_message));
                merchantProfileFragBinding.merchantName.requestFocus();
                return false;
            }
        }

        if(editText.equals(merchantProfileFragBinding.merchantBusinessName)){
            if (merchantProfileFragBinding.merchantBusinessName.getText().toString().trim().length() > 0) {
                viewmodel.merchantBusinessName=merchantProfileFragBinding.merchantBusinessName.getText().toString();
                return true;
            } else {
                merchantProfileFragBinding.merchantBusinessName.setError(getResources().getString(R.string.business_name_error_message));
                merchantProfileFragBinding.merchantBusinessName.requestFocus();
                return false;
            }
        }

        return false;
    }
    private void getProfileData()
    {
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.getProfileData();
        }
        else{
            viewmodel.networkError.postValue(true);
            merchantProfileFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
    }
    }

    private void setStatusNetworkError () {
       // merchantProfileFragBinding.contentView.setVisibility(View.GONE);
      //  merchantProfileFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        merchantProfileFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        merchantProfileFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading () {
      //  MerchantProfileFragBinding.contentView.setVisibility(View.VISIBLE);
       // MerchantProfileFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        merchantProfileFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        merchantProfileFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusEmpty () {
       // MerchantProfileFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
        merchantProfileFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        merchantProfileFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady () {
       // MerchantProfileFragBinding.contentView.setVisibility(View.VISIBLE);
      //  MerchantProfileFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        merchantProfileFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        merchantProfileFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }
}
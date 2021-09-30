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
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.agreement.AgreementActivity;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.MerchantKycFragBinding;
import com.zopnote.android.merchant.home.HomeActivity;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.util.VolleyManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MerchantKYCFragment extends Fragment {

    private MerchantKycFragBinding merchantKYCFragBinding;
    private MerchantKYCViewModel viewmodel;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int CAMERA_REQUEST = 107;
    private Bitmap photo;
    public static final int RESULT_GALLERY = 1;
    private ImageLoader imageLoader;
    final int PIC_CROP = 2;

    public MerchantKYCFragment() {
        // Required empty public constructor
    }

    public static MerchantKYCFragment newInstance() {
        MerchantKYCFragment fragment = new MerchantKYCFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // String stepTitle;
      //  stepTitle = getResources().getString(R.string.merchant_setup_profile_step_profile);
     //   merchantKYCFragBinding.stepTitle.setText(stepTitle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        merchantKYCFragBinding = merchantKYCFragBinding.inflate(inflater, container, false);

        return merchantKYCFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("CSD","KYC ON ACTIVITY CREATED");

        String stepTitle1, agreement_sign_off;
        stepTitle1=getResources().getString(R.string.merchant_setup_kyc_step_profile);
        agreement_sign_off=getResources().getString(R.string.merchant_setup_kyc_agreement_sign_off);

        merchantKYCFragBinding.stepTitle.setText(stepTitle1);
        merchantKYCFragBinding.checkedTextAgreement.setText(agreement_sign_off);

        setStatusLoading();

        viewmodel = MerchantKYCActivity.obtainViewModel(getActivity());
        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                //viewmodel.merchantId = merchant.getId();

                imageLoader = VolleyManager.getInstance(getActivity()).getImageLoader();

                retrieveKYCDetails();
                viewmodel.merchant.removeObserver(this);
            }
        });

        merchantKYCFragBinding.submitKYC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAllFields()) {
                    if (NetworkUtil.enforceNetworkConnection(getContext())) {
                        getReports();
                    }
                }
            }
        });

         merchantKYCFragBinding.closeKYC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getResources()!= null)
                    getActivity().finish();

                startActivity(new Intent(getContext(), HomeActivity.class));
            }
        });


        merchantKYCFragBinding.skipKYC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.enforceNetworkConnection(getContext())) {
                    startActivity(new Intent(getContext(), MerchantBankInfoActivity.class));
                }
            }
        });

        merchantKYCFragBinding.takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndCallCamera();
            }
        });

        merchantKYCFragBinding.pickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openGallery();
                checkPermissionAndOpenGallery();
            }
        });

        merchantKYCFragBinding.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewmodel.proofImageCaptured =false;
                viewmodel.encodedString = "";

                merchantKYCFragBinding.ivBill.setImageBitmap(null);

                // merchantProfileFragBinding.ivBill.setDefaultImageResId(R.drawable.ic_person_black_48dp);
                merchantKYCFragBinding.deleteImg.setVisibility(View.GONE);
                //merchantProfileFragBinding.viewBillRelative.setVisibility(View.GONE);
            }
        });

        merchantKYCFragBinding.ivBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        merchantKYCFragBinding.checkedTextAgreement.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final CheckedTextView simpleCheckedTextView = (CheckedTextView) view.findViewById(R.id.checkedText_agreement);

            if (simpleCheckedTextView.isChecked()) {
                viewmodel.merchantSigned="NO";
                Log.d("CSD","UN CHECKED");

                merchantKYCFragBinding.submitKYC.setTextColor(getResources().getColor(R.color.text_secondary));
                merchantKYCFragBinding.submitKYC.setBackgroundColor(getResources().getColor(R.color.gray_bg));
                merchantKYCFragBinding.submitKYC.setEnabled(false);

                simpleCheckedTextView.setChecked(false);
            }
            else
            {
                Log.d("CSD","CHECKED");
                viewmodel.merchantSigned="YES";

                merchantKYCFragBinding.submitKYC.setTextAppearance(getContext(), R.style.NormalButton);
                merchantKYCFragBinding.submitKYC.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                merchantKYCFragBinding.submitKYC.setEnabled(true);

                simpleCheckedTextView.setChecked(true);
            }
        }
    });

    merchantKYCFragBinding.agreementView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (NetworkUtil.enforceNetworkConnection(getContext())) {
                        //startActivity(new Intent(getContext(), AgreementActivity.class));
                        Intent intent = new Intent(getContext(), AgreementActivity.class);
                        intent.putExtra(Extras.TITLE, "Agreement");
                        intent.putExtra(Extras.URL, AppConstants.AGREEMENT_URL);
                        startActivity(intent);
                    }
                }
    });

        merchantKYCFragBinding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
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
                    merchantKYCFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
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

        viewmodel.addMerchantApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            getActivity().getResources().getString(R.string.kyc_updated_success_message),
                            Toast.LENGTH_LONG);
                    startActivity(new Intent(getContext(), MerchantBankInfoActivity.class));
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
    }

    private void setData() {
        //Default size of skip button and Close button in xml varies
        merchantKYCFragBinding.skipKYC.setTextAppearance(getContext(), R.style.NormalButton);
        merchantKYCFragBinding.skipKYC.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        merchantKYCFragBinding.closeKYC.setTextAppearance(getContext(), R.style.NormalButton);
        merchantKYCFragBinding.closeKYC.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        
        merchantKYCFragBinding.panNo.setText(viewmodel.merchantModel.getPAN());
        merchantKYCFragBinding.aadharNo.setText(viewmodel.merchantModel.getAadhar());

        if (viewmodel.merchantModel.getAgreementSigned().equals("YES")) {
            merchantKYCFragBinding.checkedTextAgreement.setChecked(true);
            viewmodel.merchantSigned="YES";

            merchantKYCFragBinding.submitKYC.setTextAppearance(getContext(), R.style.NormalButton);
            merchantKYCFragBinding.submitKYC.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            merchantKYCFragBinding.submitKYC.setEnabled(true);
        }
        else {
            merchantKYCFragBinding.checkedTextAgreement.setChecked(false);
            viewmodel.merchantSigned="NO";

            merchantKYCFragBinding.submitKYC.setTextColor(getResources().getColor(R.color.text_secondary));
            merchantKYCFragBinding.submitKYC.setBackgroundColor(getResources().getColor(R.color.gray_bg));
            merchantKYCFragBinding.submitKYC.setEnabled(false);
        }

       Log.d("CSD",">>>> "+viewmodel.merchantModel.getProofPicUrl());
        if (viewmodel.proofImageAlreadyExists) {
            String proofPicUrl = viewmodel.merchantModel.getProofPicUrl();
            retrieveProofPic(proofPicUrl);
        }
    }

    private void checkPermissionAndCallCamera() {
        if (ActivityCompat.checkSelfPermission(MerchantKYCFragment.this.getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(MerchantKYCFragment.this.getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private void checkPermissionAndOpenGallery() {
        if (ActivityCompat.checkSelfPermission(MerchantKYCFragment.this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            ActivityCompat.requestPermissions(MerchantKYCFragment.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RESULT_GALLERY);
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
            merchantKYCFragBinding.viewBillRelative.setVisibility(View.VISIBLE);
          //  merchantKYCFragBinding.deleteImg.setVisibility(View.VISIBLE);
            viewmodel.proofImageCaptured = true;

            //  Uri photoURI=data.getData();
            // performCrop(photoURI);

            Log.d("CSD","CAMERA: "+currentPhotoPath);
            photo = decodeFile(currentPhotoPath);
            merchantKYCFragBinding.ivBill.setImageBitmap(photo);
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
            viewmodel.proofImageCaptured = true;
            merchantKYCFragBinding.viewBillRelative.setVisibility(View.VISIBLE);
         //   merchantKYCFragBinding.deleteImg.setVisibility(View.VISIBLE);
            try {
                final Uri imageUri = data.getData();

                //final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                //final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                String path= getRealPathFromURI(imageUri);
                Log.d("CSD",">>>>> "+path);

                photo = decodeFile(path);
                merchantKYCFragBinding.ivBill.setImageBitmap(photo);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(MerchantKYCFragment.this.getActivity());

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
            viewmodel.submitKYC();
        }else{
            viewmodel.networkError.postValue(true);
            merchantKYCFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }


    public void retrieveProofPic(String proofPicUrl) {
        Log.d("CSD","------"+proofPicUrl);

        if (NetworkUtil.isNetworkAvailable(getActivity())) {

            //Note: Using billImg causing wierd issue, - image not seems to load, working for fragbinding see below
            //LayoutInflater inflater = LayoutInflater.from(getActivity());
            //final View view1 = inflater.inflate(R.layout.merchant_profile_frag, null);
            // final NetworkImageView billImg = view1.findViewById(R.id.iv_bill);

            if ((proofPicUrl.length() == 0) || (proofPicUrl.isEmpty()) || (proofPicUrl.equals(""))) {
                merchantKYCFragBinding.deleteImg.setVisibility(View.GONE);
                //  merchantProfileFragBinding.ivBill.setDefaultImageResId(R.drawable.ic_person_black_48dp);
            }
            else {
                merchantKYCFragBinding.deleteImg.setVisibility(View.GONE);

                imageLoader.get(proofPicUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("CSD","ERROR");
                    }
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            photo=response.getBitmap();
                            merchantKYCFragBinding.ivBill.setImageBitmap(photo);
                            Log.d("CSD","SUCCESS "+response.getBitmap().getWidth());
                        }
                    }
                });

                photo = decodeFile(proofPicUrl);
            }
        }else{
            viewmodel.networkError.postValue(true);
            merchantKYCFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    public void retrieveKYCDetails() {

        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.getKYCDetails();
        }else{
            viewmodel.networkError.postValue(true);
            merchantKYCFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    private boolean validateAllFields() {
        if(validate(merchantKYCFragBinding.panNo) && validate(merchantKYCFragBinding.aadharNo)){
            return true;
        }
        return false;
    }

    public boolean validate(EditText editText) {
        if(editText.equals(merchantKYCFragBinding.panNo)){
            if (merchantKYCFragBinding.panNo.getText().toString().trim().length() == 10) {
                viewmodel.merchantPANNo=merchantKYCFragBinding.panNo.getText().toString();
                return true;
            } else {
                merchantKYCFragBinding.panNo.setError(getResources().getString(R.string.panNo_error_message));
                merchantKYCFragBinding.panNo.requestFocus();
                return false;
            }
        }

        if(editText.equals(merchantKYCFragBinding.aadharNo)){
            if (merchantKYCFragBinding.aadharNo.getText().toString().trim().length() == 12) {
                viewmodel.merchantAadharNo=merchantKYCFragBinding.aadharNo.getText().toString();
                return true;
            } else {
                merchantKYCFragBinding.aadharNo.setError(getResources().getString(R.string.aadharNo_error_message));
                merchantKYCFragBinding.aadharNo.requestFocus();
                return false;
            }
        }
        return false;
    }

    private void setStatusNetworkError () {
       // merchantKYCFragBinding.contentView.setVisibility(View.GONE);
      //  merchantKYCFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        merchantKYCFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        merchantKYCFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading () {
      //  MerchantKYCFragBinding.contentView.setVisibility(View.VISIBLE);
      //  MerchantKYCFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        merchantKYCFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        merchantKYCFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusEmpty () {
       // MerchantKYCFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
        merchantKYCFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        merchantKYCFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady () {
       // MerchantKYCFragBinding.contentView.setVisibility(View.VISIBLE);
        merchantKYCFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        merchantKYCFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

}
package com.zopnote.android.merchant.editcustomer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.addcustomer.ProfileFragment;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.CustomSpinnerViewBinding;
import com.zopnote.android.merchant.databinding.EditCustomerActBinding;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.NothingSelectedSpinnerAdapter;
import com.zopnote.android.merchant.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EditCustomerActivity extends AppCompatActivity {

    private static String LOG_TAG = EditCustomerActivity.class.getSimpleName();
    private static boolean DEBUG = false;

    private EditCustomerActBinding binding;
    private EditCustomerViewModel viewmodel;
    private ProgressDialog progressDialog;

    private String customerId;

    private boolean customerLoaded = false;
    private boolean addressLine1FieldsLoaded = false;
    private boolean addressLine2Loaded = false;
    private boolean routeLoaded = false;
    private static String NOTHING_SELECTED = "None";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.edit_customer_act);

        setupToolbar();

        setupArgs();

        viewmodel = obtainViewModel(this);
        viewmodel.init(customerId);

        setupView();

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(EditCustomerActivity.this);
                    progressDialog.setMessage(EditCustomerActivity.this.getResources().getString(R.string.update_customer_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });

        viewmodel.apiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(EditCustomerActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(EditCustomerActivity.this,
                            EditCustomerActivity.this.getResources().getString(R.string.update_customer_success_message),
                            Toast.LENGTH_LONG);
                    EditCustomerActivity.this.finish();
                }
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                System.out.println("Merchant id: " + merchant.getId());
            }
        });

        binding.fieldsInclude.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveProfileFields();
                if(validate()){
                    if (NetworkUtil.enforceNetworkConnection(EditCustomerActivity.this)) {
                        viewmodel.updateProfile();
                    }
                }
            }
        });

        binding.fieldsInclude.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.fieldsInclude.pickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    openContact();
                } else {
                    ActivityCompat.requestPermissions(EditCustomerActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                }



            }
        });
    }

    private void openContact(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        if (reqCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri contact = data.getData();
            ContentResolver cr = getContentResolver();

            Cursor c = managedQuery(contact, null, null, null, null);
            //      c.moveToFirst();


            while(c.moveToNext()){
                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);

                    while(pCur.moveToNext()){
                        String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        if (phone.length()>10)
                            viewmodel.mobileNumber=phone.substring(3);
                        else
                            viewmodel.mobileNumber=phone;

                        viewmodel.name=name;

                    }
                    // pCur.close();
                }

            }
            // c.close();


        }



    }

    private void saveProfileFields() {
        if( binding.fieldsInclude.mobileNumber.getText().toString().isEmpty()){
            viewmodel.mobileNumber = viewmodel.placeHolderMobileNumber;
        }else{
            viewmodel.mobileNumber = binding.fieldsInclude.mobileNumber.getText().toString();
        }
        viewmodel.email = binding.fieldsInclude.email.getText().toString();

        viewmodel.name = binding.fieldsInclude.name.getText().toString();
        viewmodel.doorNumber = binding.fieldsInclude.doorNumber.getText().toString();
    }

    private void setupView() {

        viewmodel.customer.observe(this, new Observer<Customer>() {
            @Override
            public void onChanged(@Nullable Customer customer) {

                if( !customerLoaded ){
                    viewmodel.mobileNumber = getDisplayMobileNumber(customer);
                    viewmodel.placeHolderMobileNumber = viewmodel.mobileNumber;

                    if(Utils.hasValidMobileNumber(viewmodel.mobileNumber)){
                        binding.fieldsInclude.mobileNumber.setText(viewmodel.mobileNumber);
                    }else{
                        //don't show placeholder mobile number
                    }

                    viewmodel.email = customer.getEmail();
                    binding.fieldsInclude.email.setText(viewmodel.email);

                    viewmodel.name = getName(customer);
                    binding.fieldsInclude.name.setText(viewmodel.name);

                    viewmodel.doorNumber = customer.getDoorNumber();
                    binding.fieldsInclude.doorNumber.setText(viewmodel.doorNumber);

                    if(customer.getAddressLine1() != null && customer.getAddressLine1().trim().length() >0){
                        prepareNameSelectedValueMap(customer.getAddressLine1());
                    }

                    viewmodel.addressLine2 = customer.getAddressLine2();

                    viewmodel.route = customer.getRoute();

                    customerLoaded = true;

                    clearFocus();
                }
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {

                if( ! routeLoaded){
                    prepareRoutesSpinner(merchant.getRoutes());
                }

                if( ! addressLine2Loaded){
                    prepareAddressLine2Spinner(merchant.getAreaList());
                }

                if( ! addressLine1FieldsLoaded){
                    binding.fieldsInclude.addressLine1SpinnerContainer.removeAllViews();
                    prepareAddressLine1Spinners(merchant.getAddressFieldsConfig());
                }
            }
        });
    }

    private void prepareNameSelectedValueMap(String addressLine1) {
        try {
            JSONObject addressLine1Object = new JSONObject(addressLine1);
            Iterator<?> keys = addressLine1Object.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if ( addressLine1Object.get(key) instanceof String ) {
                    viewmodel.addressLine1NameSelectedValueMap.put(key, (String) addressLine1Object.get(key));
                }
            }
        } catch (JSONException e) {
            //for legacy data: if not JSONArray then it's a String
            viewmodel.addressLine1NameSelectedValueMap.put("floor", addressLine1);
            e.printStackTrace();
        }
    }

    private void prepareAddressLine1Spinners(String addressFieldsConfig) {
        try {
            JSONObject address1Fields = new JSONObject(addressFieldsConfig);
            JSONArray address1FieldsArray = address1Fields.getJSONArray("address1Fields");
            for (int i = 0; i< address1FieldsArray.length(); i++){
                JSONObject addressField  = (JSONObject) address1FieldsArray.get(i);

                String name = addressField.getString("name");
                String label = addressField.getString("label");
                viewmodel.addressLine1NameLabelMap.put(name, label);

                List<String> values = new ArrayList<>();
                JSONArray valuesArray = addressField.getJSONArray("values");
                for (int j=0; j<valuesArray.length(); j++){
                    values.add(valuesArray.getString(j));
                }

                if (values.size() > 0) {
                    values.add(NOTHING_SELECTED);
                }

                addSpinner(name, label, values);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addressLine1FieldsLoaded = true;
    }

    private void addSpinner(String name, String label, List<String> values) {
        View spinnerView = getSpinner(name, label, values);
        binding.fieldsInclude.addressLine1SpinnerContainer.addView(spinnerView);
    }

    private View getSpinner(final String name, String label, List<String> values) {
        CustomSpinnerViewBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.custom_spinner_view, null, false);

        binding.label.setText(label);

        Spinner spinner = new Spinner(this,Spinner.MODE_DROPDOWN);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, values);
        spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                spinnerArrayAdapter,
                R.layout.spinner_row_nothing_selected,
                this));

        if(viewmodel.addressLine1NameSelectedValueMap.size() > 0) {
            String previouslySelectedValue = viewmodel.addressLine1NameSelectedValueMap.get(name);
            if(previouslySelectedValue != null){
                int index = spinnerArrayAdapter.getPosition(previouslySelectedValue);
                if (index >= 0) {
                    spinner.setSelection(index + 1);
                }
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = (String) parent.getItemAtPosition(position);
                if(selectedText != null) {
                    if (selectedText.equalsIgnoreCase(NOTHING_SELECTED)) {
                        if (viewmodel.addressLine1NameSelectedValueMap.containsKey(name)) {
                            //remove existing value from map
                            viewmodel.addressLine1NameSelectedValueMap.remove(name);
                        }
                    } else {
                        viewmodel.addressLine1NameSelectedValueMap.put(name, parent.getItemAtPosition(position).toString());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerContainer.addView(spinner);
        return binding.getRoot();
    }

    private void prepareRoutesSpinner(List<String> merchantRoutes) {

        final ArrayList<String> routeSuggestions = new ArrayList<>();

        for (String route: merchantRoutes) {
            if( ! routeSuggestions.contains(route)){
                routeSuggestions.add(route);
            }
        }

        ArrayAdapter<String> routeSuggestionsArrayAdapter = new ArrayAdapter<String>(EditCustomerActivity.this,android.R.layout.simple_spinner_dropdown_item, routeSuggestions);
        routeSuggestionsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.fieldsInclude.route.setAdapter(new NothingSelectedSpinnerAdapter(
                routeSuggestionsArrayAdapter,
                R.layout.spinner_row_nothing_selected,
                EditCustomerActivity.this));

        if(viewmodel.route != null){
            int index = routeSuggestionsArrayAdapter.getPosition(viewmodel.route);
            if(index >= 0){
                binding.fieldsInclude.route.setSelection(index+1);
            }
        }

        binding.fieldsInclude.route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = (String) parent.getItemAtPosition(position);
                if(selectedText != null) {
                    viewmodel.route = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        routeLoaded = true;
    }

    private void prepareAddressLine2Spinner(List<String> merchantAddressLine2) {

        final ArrayList<String> addressLine2Suggestion = new ArrayList<>();

        for (String addressLine2: merchantAddressLine2) {
            if( ! addressLine2Suggestion.contains(addressLine2)){
                addressLine2Suggestion.add(addressLine2);
            }
        }

        ArrayAdapter<String> addressLine2SuggestionsArrayAdapter = new ArrayAdapter<String>(EditCustomerActivity.this,android.R.layout.simple_spinner_dropdown_item, addressLine2Suggestion);
        addressLine2SuggestionsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.fieldsInclude.addressLine2.setAdapter(new NothingSelectedSpinnerAdapter(
                addressLine2SuggestionsArrayAdapter,
                R.layout.spinner_row_nothing_selected,
                EditCustomerActivity.this));

        if(viewmodel.addressLine2 != null){
            int index = addressLine2SuggestionsArrayAdapter.getPosition(viewmodel.addressLine2);
            if(index >= 0){
                binding.fieldsInclude.addressLine2.setSelection(index+1);
            }
        }
        binding.fieldsInclude.addressLine2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = (String) parent.getItemAtPosition(position);
                if(selectedText != null){
                    viewmodel.addressLine2 = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addressLine2Loaded = true;
    }

    private void clearFocus() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setupArgs() {
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
    }

    public static EditCustomerViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        EditCustomerViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(EditCustomerViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.fieldsInclude.mobileNumber.setText(viewmodel.mobileNumber);

        binding.fieldsInclude.name.setText(viewmodel.name);
        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.ADD_CUSTOMER, "EditCustomerActivity");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private String getDisplayMobileNumber(Customer customer) {
        return customer.getMobileNumber().replaceAll("^\\+91", "");
    }

    private String getName(Customer customer) {
        StringBuilder stringBuilder = new StringBuilder();
        if (customer.getFirstName() != null && customer.getFirstName().trim().length() > 0) {
            stringBuilder.append(customer.getFirstName().trim());
        }
        if (customer.getLastName() != null && customer.getLastName().trim().length() > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(customer.getLastName().trim());
        }
        return stringBuilder.toString();
    }

    public boolean validate() {
        return areProfileEditTextFieldsValid() && areProfileSpinnerValuesValid();
    }

    private boolean areProfileEditTextFieldsValid() {
        if(validateProfileField(binding.fieldsInclude.mobileNumber) && validateProfileField(binding.fieldsInclude.name) && validateProfileField(binding.fieldsInclude.doorNumber)
                && validateProfileField(binding.fieldsInclude.email)){
            return true;
        }
        return false;
    }

    public boolean validateProfileField(EditText editText) {
        if(editText.equals(binding.fieldsInclude.mobileNumber)){
            String mobile = binding.fieldsInclude.mobileNumber.getText().toString();
            if(mobile.trim().length() == 0 && viewmodel.placeHolderMobileNumber.matches("^[1]\\d{9}$")){
                //place holder mobile number used, ignore empty field
                return true;
            }else if(mobile.matches("^[6789]\\d{9}$")){
                return true;
            } else {
                binding.fieldsInclude.mobileNumber.setError(getResources().getString(R.string.mobile_number_error_message));
                binding.fieldsInclude.mobileNumber.requestFocus();
                return false;
            }
        }

        if(editText.equals(binding.fieldsInclude.email)){
            if(binding.fieldsInclude.email.getText().toString().isEmpty() || isValidEmailAddress(binding.fieldsInclude.email.getText().toString())){
                //allow empty email address
                return true;
            } else {
                binding.fieldsInclude.email.setError(getResources().getString(R.string.email_address_error_message));
                binding.fieldsInclude.email.requestFocus();
                return false;
            }
        }

        if(editText.equals(binding.fieldsInclude.name)){
            if (binding.fieldsInclude.name.getText().toString().isEmpty() || binding.fieldsInclude.name.getText().toString().trim().length() > 2) {
                //allow empty first name
                return true;
            } else {
                binding.fieldsInclude.name.setError(getResources().getString(R.string.name_error_message));
                binding.fieldsInclude.name.requestFocus();
                return false;
            }
        }

        if(editText.equals(binding.fieldsInclude.doorNumber)){
            if (binding.fieldsInclude.doorNumber.getText().toString().trim().length() > 0) {
                return true;
            } else {
                binding.fieldsInclude.doorNumber.setError(getResources().getString(R.string.door_number_error_message));
                binding.fieldsInclude.doorNumber.requestFocus();
                return false;
            }
        }
        return false;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private boolean areProfileSpinnerValuesValid() {
        if (validateProfileField(binding.fieldsInclude.addressLine2) && validateProfileField(binding.fieldsInclude.route)) {
            return true;
        }
        return false;
    }

    private boolean validateProfileField(Spinner spinner) {
        if( spinner.getSelectedItem() != null){
            return true;
        }else{
            if(spinner.equals(binding.fieldsInclude.addressLine2)){
                ((TextView)spinner.getSelectedView()).setError(getResources().getString(R.string.addressline2_error_message));

            }else if(spinner.equals(binding.fieldsInclude.route)){
                ((TextView)spinner.getSelectedView()).setError(getResources().getString(R.string.route_error_message));
            }
            return false;
        }
    }
}

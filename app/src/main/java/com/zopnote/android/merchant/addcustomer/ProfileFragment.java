package com.zopnote.android.merchant.addcustomer;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.CustomSpinnerViewBinding;
import com.zopnote.android.merchant.databinding.ProfileFragBinding;
import com.zopnote.android.merchant.util.MobileNumberEditText;
import com.zopnote.android.merchant.util.NothingSelectedSpinnerAdapter;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.util.Validatable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by nmohideen on 26/12/17.
 */

public class ProfileFragment extends Fragment implements Validatable {

    private ProfileFragBinding binding;
    private AddCustomerViewModel viewmodel;
    private boolean addressLine2Loaded = false;
    private boolean routeLoaded = false;
    private boolean addressLine1FieldsLoaded = false;
    private static String NOTHING_SELECTED = "None";
    private ArrayAdapter<String> routeSuggestionsArrayAdapter;
    private ArrayAdapter<String> addressLine2SuggestionsArrayAdapter;

    private MobileNumberEditText mMobile;
    private TextInputEditText mName;

    public ProfileFragment() {
        // Requires empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ProfileFragBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = AddCustomerActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {

                if( ! routeLoaded){
                    prepareRoutesSpinner(merchant.getRoutes());
                }

                if( ! addressLine2Loaded){
                    prepareAddressLine2Spinner(merchant.getAreaList());
                }

                if( !addressLine1FieldsLoaded){
                    binding.addressLine1SpinnerContainer.removeAllViews();
                    prepareAddressLine1Spinners(merchant.getAddressFieldsConfig());
                }
            }
        });

        View pasteRouteButton = getActivity().findViewById(R.id.pasteRouteButton);
        pasteRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restorePreviousRouteAndAddressLine1();
            }
        });


      binding.pickContact.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                  openContact();
              } else {
                  ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 1);
              }



          }
      });

}
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openContact();
                } else {
                    Toast.makeText(getActivity(),R.string.permissions_read_contact_permission_settings_instruction, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void openContact(){
        Intent intent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        if (reqCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri contact = data.getData();
            ContentResolver cr = ProfileFragment.this.getActivity().getContentResolver();

            Cursor c = ProfileFragment.this.getActivity().managedQuery(contact, null, null, null, null);
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
    public String phoneNumberWithOutCountryCode(String phoneNumberWithCountryCode) {
        Pattern complie = Pattern.compile(" ");
        String[] phonenUmber = complie.split(phoneNumberWithCountryCode);
        return phonenUmber[1];
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
        binding.addressLine1SpinnerContainer.addView(spinnerView);
    }

    private View getSpinner(final String name, String label, List<String> values) {
        CustomSpinnerViewBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.custom_spinner_view, null, false);

        binding.label.setText(label);

        Spinner spinner = new Spinner(getActivity(), Spinner.MODE_DROPDOWN);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, values);
        spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                spinnerArrayAdapter,
                R.layout.spinner_row_nothing_selected,
                getContext()));

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

        routeSuggestionsArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, routeSuggestions);
        routeSuggestionsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.route.setAdapter(new NothingSelectedSpinnerAdapter(
                routeSuggestionsArrayAdapter,
                R.layout.spinner_row_nothing_selected,
                getContext()));

        if(viewmodel.route != null){
            int index = routeSuggestionsArrayAdapter.getPosition(viewmodel.route);
            if(index >= 0){
                binding.route.setSelection(index+1);
            }
        }

        binding.route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        addressLine2SuggestionsArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, addressLine2Suggestion);
        addressLine2SuggestionsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.addressLine2.setAdapter(new NothingSelectedSpinnerAdapter(
                addressLine2SuggestionsArrayAdapter,
                R.layout.spinner_row_nothing_selected,
                getContext()));

        if(viewmodel.addressLine2 != null){
            int index = addressLine2SuggestionsArrayAdapter.getPosition(viewmodel.addressLine2);
            if(index >= 0){
                binding.addressLine2.setSelection(index+1);
            }
        }
        binding.addressLine2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    @Override
    public void onPause() {
        super.onPause();
        viewmodel.mobileNumber = binding.mobileNumber.getText().toString();
        viewmodel.name = binding.name.getText().toString();
        viewmodel.doorNumber = binding.doorNumber.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mobileNumber.setText(viewmodel.mobileNumber);

        binding.name.setText(viewmodel.name);
        binding.doorNumber.setText(viewmodel.doorNumber);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public boolean validate() {
        return validateAllFields();
    }

    private void restorePreviousRouteAndAddressLine1(){
        String previousRoute = Prefs.getString(AppConstants.PREFS_LAST_USED_ROUTE, null);
        String previousAddressLine2 = Prefs.getString(AppConstants.PREFS_LAST_USED_ADDRESS_LINE2, null);

        if(previousRoute == null || previousAddressLine2 == null){
            Toast.makeText(getContext(), R.string.apartment_and_route_values_not_found_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        if(previousRoute != null && ! previousRoute.isEmpty()){
            if(routeSuggestionsArrayAdapter != null){
                int index = routeSuggestionsArrayAdapter.getPosition(previousRoute);
                if(index >= 0){
                    binding.route.setSelection(index+1);
                }
            }
        }

        if(previousAddressLine2 != null && ! previousAddressLine2.isEmpty()){
            if(addressLine2SuggestionsArrayAdapter != null){
                int index = addressLine2SuggestionsArrayAdapter.getPosition(previousAddressLine2);
                if(index >= 0){
                    binding.addressLine2.setSelection(index+1);
                }
            }
        }

        Toast.makeText(getContext(), R.string.apartment_and_route_values_restored_toast, Toast.LENGTH_SHORT).show();
    }

    private boolean validateAllFields() {
        if(validate(binding.mobileNumber) && validate(binding.doorNumber) && validate(binding.addressLine2) && validate(binding.route)){
            return true;
        }
        return false;
    }

    public boolean validate(EditText editText) {
        if(editText.equals(binding.mobileNumber)){
            if(binding.mobileNumber.getText().toString().isEmpty()){
                return true;
            }else if(binding.mobileNumber.getText().toString().matches("^[6789]\\d{9}$")) {
                return true;
            } else {
                binding.mobileNumber.setError(getResources().getString(R.string.mobile_number_error_message));
                binding.mobileNumber.requestFocus();
                return false;
            }
        }

        if(editText.equals(binding.name)){
            if (binding.name.getText().toString().trim().length() > 2) {
                return true;
            } else {
                binding.name.setError(getResources().getString(R.string.name_error_message));
                binding.name.requestFocus();
                return false;
            }
        }

        if(editText.equals(binding.doorNumber)){
            if (binding.doorNumber.getText().toString().trim().length() > 0) {
                return true;
            } else {
                binding.doorNumber.setError(getResources().getString(R.string.door_number_error_message));
                binding.doorNumber.requestFocus();
                return false;
            }
        }

        return false;
    }
    public boolean validate(Spinner spinner) {
        if( spinner.getSelectedItem() != null){
            return true;
        }else{
            if(spinner.equals(binding.addressLine2)){
                ((TextView)spinner.getSelectedView()).setError(getResources().getString(R.string.addressline2_error_message));

            }else if(spinner.equals(binding.route)){
                ((TextView)spinner.getSelectedView()).setError(getResources().getString(R.string.route_error_message));
            }
            spinner.requestFocus();

            return false;
        }
    }
}

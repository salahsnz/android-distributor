package com.zopnote.android.merchant.dailyindent;


import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.DailyIndent;
import com.zopnote.android.merchant.data.model.DailyIndentSubscription;
import com.zopnote.android.merchant.data.model.IndentUpdate;
import com.zopnote.android.merchant.databinding.DailyIndentFragBinding;
import com.zopnote.android.merchant.reports.subscription.RouteHeader;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.NothingSelectedSpinnerAdapter;
import com.zopnote.android.merchant.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyIndentFragment extends Fragment {
    private DailyIndentViewModel viewmodel;
    private DailyIndentFragBinding indentUpdateFragment;
    private DailyIndentAdapter adapter;
    private ArrayAdapter<String> routeSuggestionsArrayAdapter;
    private boolean routeLoaded = false;
    private ProgressDialog progressDialog;
    private String LOG_TAG = "IndentFragment";

    private String route;

    public DailyIndentFragment() {
        // Required empty public constructor
    }

    public static DailyIndentFragment newInstance() {
        DailyIndentFragment fragment = new DailyIndentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        indentUpdateFragment = DailyIndentFragBinding.inflate(inflater, container, false);
        indentUpdateFragment.recyclerView.setHasFixedSize(true);


        // hide all
        indentUpdateFragment.contentView.setVisibility(View.GONE);
        indentUpdateFragment.loadingView.getRoot().setVisibility(View.GONE);
        indentUpdateFragment.networkErrorView.getRoot().setVisibility(View.GONE);

        return indentUpdateFragment.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();

        viewmodel = DailyIndentActivity.obtainViewModel(getActivity());
        route = getArguments().getString(Extras.ROUTE);

        viewmodel.quantityUpdated.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean changed) {
                if (changed) {
                    // indentUpdateFragment.recyclerView.removeAllViewsInLayout();
                    // adapter.notifyDataSetChanged();
                    viewmodel.updateQuantity();
                }
            }
        });

        indentUpdateFragment.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DailyIndentActivity) getActivity()).getReports();
            }
        });
        indentUpdateFragment.saveIndentUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateIndentQuantity();
            }
        });

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    setStatusLoading();
                }
            }
        });

        viewmodel.apiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    indentUpdateFragment.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
                    setStatusNetworkError();
                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    setData();
                }
            }
        });

        viewmodel.networkError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    indentUpdateFragment.networkErrorView.networkErrorText.setText(R.string.no_network_error);
                    setStatusNetworkError();
                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });


        viewmodel.updateQuantityApiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(DailyIndentFragment.this.getActivity());
                    progressDialog.setMessage(DailyIndentFragment.this.getResources().getString(R.string.update_quantity_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.updateQuantityApiCallRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.updateQuantityApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(DailyIndentFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.updateQuantityApiCallError.setValue(false);
                }
            }
        });

        viewmodel.updateQuantityApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {

                if (success) {
                    Utils.showSuccessToast(DailyIndentFragment.this.getActivity(),
                            DailyIndentFragment.this.getActivity().getResources().getString(R.string.update_invoice_success_message),
                            Toast.LENGTH_LONG);
                    ((DailyIndentActivity) getActivity()).getReports();
                    viewmodel.updateQuantityApiCallSuccess.setValue(false);
                }
            }
        });
        setupDatePicker();
    }

    public void updateIndentQuantity() {
        if (NetworkUtil.enforceNetworkConnection(DailyIndentFragment.this.getActivity())) {
            viewmodel.updateQuantity();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

       /* if( viewmodel.apiCallError.getValue() || viewmodel.networkError.getValue()){
            //do nothing
        }else{
            viewmodel.networkError.setValue(false);
            setData();
        }*/
    }

    private void setData() {

        setAdapter();

        setHeaderProd();

        if( ! routeLoaded){
            prepareRoutesSpinner(new ArrayList<>(viewmodel.routeWiseMap.keySet()));
        }

        List<DailyIndent> list = new ArrayList<>(viewmodel.routeWiseMap.get(viewmodel.route));

        indentUpdateFragment.totalVendorCount.setText(String.valueOf(list.size()));

        if (list.size()>0){
            adapter.setItems(list);
        }else {
           Utils.showFailureToast(getActivity(),"No vendors found",Toast.LENGTH_LONG);
        }


        setStatusReady();

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
        indentUpdateFragment.route.setAdapter(new NothingSelectedSpinnerAdapter(
                routeSuggestionsArrayAdapter,
                R.layout.spinner_row_nothing_selected,
                getContext()));

        if(viewmodel.route != null){
            int index = routeSuggestionsArrayAdapter.getPosition(viewmodel.route);
            if(index >= 0){
                indentUpdateFragment.route.setSelection(index+1);
            }
        }

        indentUpdateFragment.route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = (String) parent.getItemAtPosition(position);
                if(selectedText != null) {
                    viewmodel.route = parent.getItemAtPosition(position).toString();
                    setData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
         routeLoaded =true;

    }



    private void setAdapter() {
        adapter = new DailyIndentAdapter(getActivity());
        adapter.setViewModel(DailyIndentActivity.obtainViewModel(this.getActivity()));

        RecyclerView.ItemDecoration dividerDecoration2 = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        indentUpdateFragment.recyclerView.addItemDecoration(dividerDecoration2);
        indentUpdateFragment.recyclerView.setNestedScrollingEnabled(false);
        indentUpdateFragment.recyclerView.setAdapter(adapter);
    }


    private void setHeaderProd() {
        indentUpdateFragment.linearHeaderContent.removeAllViews();
        LinearLayout parent1 = new LinearLayout(getContext());

        parent1.setLayoutParams(new LinearLayout.LayoutParams(240, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent1.setOrientation(LinearLayout.HORIZONTAL);
        TextView name = new TextView(getContext());
        name.setText("VENDORS");
        name.setTextAppearance(getContext(), R.style.FontMedium);
        name.setTypeface(null, Typeface.BOLD);
        name.setTextColor(getResources().getColor(R.color.text_primary));

        parent1.addView(name);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent1.getLayoutParams();
        params.setMargins(14, 0, 14, 0);
        parent1.setLayoutParams(params);
        indentUpdateFragment.linearHeaderContent.addView(parent1);
        for (int i = 0; i < viewmodel.offeredProductList.size(); i++) {
            LinearLayout parent2 = new LinearLayout(getContext());
            parent2.setLayoutParams(new LinearLayout.LayoutParams(90, ViewGroup.LayoutParams.WRAP_CONTENT));
            parent2.setOrientation(LinearLayout.HORIZONTAL);
            TextView shortCode = new TextView(getContext());
            shortCode.setText(viewmodel.offeredProductList.get(i).getProductShortCode() + " (" + viewmodel.offeredProductQtyMap.get(viewmodel.offeredProductList.get(i).getProductShortCode()) + ") ");
            shortCode.setTextAppearance(getContext(), R.style.FontMedium);
            shortCode.setTypeface(null, Typeface.BOLD);
            shortCode.setTextColor(getResources().getColor(R.color.text_primary));
            parent2.addView(shortCode);

            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) parent2.getLayoutParams();
            params1.setMargins(14, 0, 14, 0);
            parent2.setLayoutParams(params1);
            indentUpdateFragment.linearHeaderContent.addView(parent2);
        }

    }


    private List<IndentUpdate> getIndentForRoute(String route) {
        List indentList = new ArrayList();

        if (!viewmodel.indentReport.isEmpty()) {


        }
        return indentList;
    }


    private void setupDatePicker() {

        setDate();

        viewmodel.dateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if (dateChanged) {
                    setDate();
                }
            }
        });

        indentUpdateFragment.datePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewmodel.isEdited) {
                    openWarningDialog();
                } else {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                }

            }
        });
    }

    private void openWarningDialog() {
        String message = "You have not saved for the day " + FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY, viewmodel.calender.getTime());
        String positiveButtonText = getActivity().getResources().getString(R.string.save_and_continue_label);
        String negativeButtonText = getActivity().getResources().getString(R.string.cancel_and_continue_label);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (NetworkUtil.enforceNetworkConnection(getContext())) {
                            viewmodel.updateQuantity();
                        }

                        viewmodel.isEdited = false;
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        viewmodel.isEdited = false;
                    }
                })
                .create();
        alertDialog.show();
    }

    private void setDate() {
        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY, viewmodel.calender.getTime());
        indentUpdateFragment.showDatePicker.setText(date);
    }


    private void addHeader(String currentAddressLine2, List summaryList) {
        RouteHeader routeHeader = new RouteHeader();
        routeHeader.setName(currentAddressLine2);
        summaryList.add(routeHeader);
    }

    private void setStatusNetworkError() {
        indentUpdateFragment.contentView.setVisibility(View.GONE);
        indentUpdateFragment.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        indentUpdateFragment.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading() {
        indentUpdateFragment.contentView.setVisibility(View.GONE);
        indentUpdateFragment.networkErrorView.getRoot().setVisibility(View.GONE);
        indentUpdateFragment.loadingView.getRoot().setVisibility(View.VISIBLE);
    }


    private void setStatusReady() {
        indentUpdateFragment.contentView.setVisibility(View.VISIBLE);
        indentUpdateFragment.networkErrorView.getRoot().setVisibility(View.GONE);
        indentUpdateFragment.loadingView.getRoot().setVisibility(View.GONE);
    }
}

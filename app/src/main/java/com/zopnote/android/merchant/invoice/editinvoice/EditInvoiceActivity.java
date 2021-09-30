package com.zopnote.android.merchant.invoice.editinvoice;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.InvoiceItem;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.EditInvoiceActBinding;
import com.zopnote.android.merchant.invoice.InvoiceUtil;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.util.VolleyManager;

import java.util.Date;

public class EditInvoiceActivity extends AppCompatActivity {
    private EditInvoiceViewModel viewmodel;
    private EditInvoiceActBinding binding;
    private ProgressDialog progressDialog;
    private String customerId;
    private String invoiceId;
    private double totalDue;
    private boolean invoiceLoaded;
    private ImageLoader imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.edit_invoice_act);

        setupArgs();

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init(customerId, invoiceId);

        setupView();

        setupApiCallObservers();
    }

    private void setupView() {

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
            }
        });

        viewmodel.invoice.observe(this, new Observer<Invoice>() {
            @Override
            public void onChanged(@Nullable Invoice invoice) {
                if( ! invoiceLoaded){
                    binding.invoiceItemsContainer.removeAllViews();

                    viewmodel.sortedInvoiceItems = InvoiceUtil.getSortedInvoiceItems(invoice);

                    addInvoiceItemViews();

                    invoiceLoaded = true;
                }

            }
        });

        binding.addInvoiceItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddInvoiceItemDialog();
            }
        });

        binding.saveInvoiceChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtil.enforceNetworkConnection(EditInvoiceActivity.this)){
                    viewmodel.updateInvoice();
                }
            }
        });
    }

    private void addInvoiceItemViews() {

        totalDue = 0d;

        for(int i=0; i< viewmodel.sortedInvoiceItems.size(); i++){

             InvoiceItem invoiceItem = viewmodel.sortedInvoiceItems.get(i);

            final View invoiceItemView = LayoutInflater.from(this)
                    .inflate(R.layout.edit_invoice_invoice_item, null);
            ((TextView) invoiceItemView.findViewById(R.id.invoiceItemName)).setText(invoiceItem.getItem());


            if (invoiceItem.getBillImg() != null){
                SpannableString content = new SpannableString(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(invoiceItem.getAmount()));
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                ((TextView) invoiceItemView.findViewById(R.id.invoiceItemAmount)).setText(content);

                invoiceItemView.findViewById(R.id.invoiceItemAmount).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openDialog(invoiceItemView.getTag());
                    }
                });
            }else {
                ((TextView) invoiceItemView.findViewById(R.id.invoiceItemAmount)).setText(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(invoiceItem.getAmount()));
            }
            ((ImageView) invoiceItemView.findViewById(R.id.viewBilCopy)).setVisibility(View.GONE);
            invoiceItemView.setTag(i);

            totalDue = totalDue + invoiceItem.getAmount();

            invoiceItemView.findViewById(R.id.editInvoiceItemButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditInvoiceItemDialog(invoiceItemView.getTag());
                }
            });

            invoiceItemView.findViewById(R.id.deleteInvoiceItemButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInvoiceItemDeleteConfirmationDialog(invoiceItemView.getTag());
                }
            });


            binding.invoiceItemsContainer.addView(invoiceItemView);
        }

        setTotalDue();
    }

    private void openDialog(Object tag) {
        final int invoiceItemIndex = (int) tag;
        final InvoiceItem invoiceItem = viewmodel.sortedInvoiceItems.get(invoiceItemIndex);

        imageLoader = VolleyManager.getInstance(this).getImageLoader();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.bill_image_preview_frag, null);

        final NetworkImageView billImg = view.findViewById(R.id.ivBillCopy);



        billImg.setImageUrl(invoiceItem.getBillImg(),imageLoader);

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




    private void setTotalDue() {
        binding.totalDueAmount.setText(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(totalDue));
    }

    private void showEditInvoiceItemDialog(Object tag) {
        int invoiceItemIndex = (int) tag;
        final InvoiceItem invoiceItem = viewmodel.sortedInvoiceItems.get(invoiceItemIndex);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.add_edit_invoice_item_dialog, null);

        final EditText itemNameEditText = view.findViewById(R.id.itemName);
        itemNameEditText.setText(invoiceItem.getItem());

        final EditText itemAmountEditText = view.findViewById(R.id.itemAmount);
        itemAmountEditText.setText(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(invoiceItem.getAmount()));

        builder.setView(view);

        builder.setMessage(R.string.edit_invoice_item_message)
                .setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Override the handler
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update invoice item

                if(isValidItemName(itemNameEditText) && isValidItemAmount(itemAmountEditText)){

                    String invoiceItemName = itemNameEditText.getText().toString();
                    invoiceItem.setItem("Dist-"+invoiceItemName);

                    String editedAmount = itemAmountEditText.getText().toString();
                    Double amount = Double.parseDouble(editedAmount);
                    invoiceItem.setAmount(amount);
                    invoiceItem.setBillImg(invoiceItem.getBillImg());
                    invoiceItem.setDate(new Date().getTime());
                    updateInvoiceItemsView();
                    dialog.dismiss();
                }
            }
        });
    }

    private void updateInvoiceItemsView() {
        binding.invoiceItemsContainer.removeAllViews();
        addInvoiceItemViews();
    }

    private void showInvoiceItemDeleteConfirmationDialog(Object tag) {
        final int invoiceItemIndex = (int) tag;
        final InvoiceItem invoiceItem = viewmodel.sortedInvoiceItems.get(invoiceItemIndex);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = getResources().getString(R.string.invoice_item_delete_warning_message, invoiceItem.getItem(),
                FormatUtil.getRupeePrefixedAmount(EditInvoiceActivity.this,
                        invoiceItem.getAmount(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));

        builder.setMessage(message)
                .setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Override the handler
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteInvoiceItem(invoiceItemIndex);
                dialog.dismiss();
            }
        });
    }

    private void deleteInvoiceItem(int invoiceItemIndex) {
        viewmodel.sortedInvoiceItems.remove(invoiceItemIndex);
        binding.invoiceItemsContainer.removeAllViews();
        addInvoiceItemViews();
    }

    private void showAddInvoiceItemDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.add_edit_invoice_item_dialog, null);

        final EditText itemNameEditText = view.findViewById(R.id.itemName);
        final EditText itemAmountEditText = view.findViewById(R.id.itemAmount);

        builder.setView(view);

        builder.setMessage(R.string.add_invoice_item_message)
                .setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Override the handler
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isValidItemName(itemNameEditText) && isValidItemAmount(itemAmountEditText)){

                    String invoiceItemName = itemNameEditText.getText().toString();
                    String invoiceItemAmount = itemAmountEditText.getText().toString();

                    Double amount = Double.parseDouble(invoiceItemAmount);

                    InvoiceItem invoiceItem = new InvoiceItem();
                    invoiceItem.setItem("Dist-"+invoiceItemName);
                    invoiceItem.setAmount(amount);
                    invoiceItem.setDate(new Date().getTime());
                    viewmodel.sortedInvoiceItems.add(invoiceItem);

                    updateInvoiceItemsView();
                    dialog.dismiss();

                }
            }
        });
    }

    private boolean isValidItemName(EditText nameEditText) {
        if( ! nameEditText.getText().toString().trim().isEmpty()){
            return true;
        }else{
            nameEditText.setError(getString(R.string.invoice_item_name_error_message));
            return false;
        }
    }

    private boolean isValidItemAmount(EditText amountEditText){
        if( ! amountEditText.getText().toString().trim().isEmpty()){
            try {
                Double.parseDouble(amountEditText.getText().toString());
                return true;
            }catch (Exception ex){
                amountEditText.setError(getString(R.string.invoice_amount_error_message));
                return false;
            }
        }else{
            amountEditText.setError(getString(R.string.invoice_amount_error_message));
            return false;
        }
    }

    private void setupArgs() {
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
        invoiceId = getIntent().getStringExtra(Extras.INVOICE_ID);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupApiCallObservers() {
        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(EditInvoiceActivity.this);
                    progressDialog.setMessage(EditInvoiceActivity.this.getResources().getString(R.string.update_invoice_api_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.apiCallRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.apiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(EditInvoiceActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallError.setValue(false);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(EditInvoiceActivity.this,
                            EditInvoiceActivity.this.getResources().getString(R.string.update_invoice_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallSuccess.setValue(false);

                    EditInvoiceActivity.this.finish();
                }
            }
        });
    }

    public static EditInvoiceViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        EditInvoiceViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(EditInvoiceViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.EDIT_INVOICE, "EditInvoiceActivity");

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
}

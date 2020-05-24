package com.example.Financial.fragment;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Financial.R;
import com.example.Financial.SwipeController.SwipeController;
import com.example.Financial.SwipeController.SwipeControllerActions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class exchange extends Fragment {
    private String TAG = this.getClass().getSimpleName();
    private int mId = -1;
    private dataAdapter mDataAdapter = null;

    private RecyclerView mRecyclerView;
    private TabLayout mTabLayout;
    private FloatingActionButton mFabPlus;
    private FloatingActionButton mFabSync;
    private String mMethod = "CASH";

    private Button mButtonDate = null;
    private Button mButtonExpiryDate = null;
    private Spinner mSpinnerAccount = null;
    private Spinner mSpinnerCurrency = null;
    private EditText mEditTextCurrencyOthers = null;
    private EditText mEditTextRate = null;
    private EditText mEditTextAmount = null;
    private EditText mEditTextInterest = null;
    private TextView mTextViewCurrentRate = null;
    private TextView mTextViewCurrentAmount = null;

    // virtual data
    Map<String, List<infoExchange>> mMapMethod = new HashMap<String, List<infoExchange>>();
    List<infoExchange> infoExchangeTime;
    List<infoExchange> infoExchangeDamand;
    List<infoExchange> infoExchangeCash;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exchange, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.getArguments() != null) {
            this.mId = this.getArguments().getInt("ID");
        }

        if(getView() != null){
            // get View
            this.mRecyclerView = getView().findViewById(R.id.recyclerView);
            this.mTabLayout = getView().findViewById(R.id.tabLayout);
            this.mFabPlus = getView().findViewById(R.id.fab_plus_exchange);
            this.mFabSync = getView().findViewById(R.id.fab_sync_exchange);

            // set listener
            this.mFabPlus.setOnClickListener(this.clickListener_plus);
            this.mFabSync.setOnClickListener(this.clickListener_sync);
            this.mTabLayout.setOnTabSelectedListener(this.selectedListener_tab);

            initDataInfo();
            setRecyclerView(this.mMapMethod.get(getString(R.string.cash)));
        }
    }

    private void initDataInfo(){
        this.infoExchangeCash = new ArrayList<infoExchange>();
        this.infoExchangeDamand = new ArrayList<infoExchange>();
        this.infoExchangeTime = new ArrayList<infoExchange>();
        this.mMapMethod.put(getString(R.string.cash), this.infoExchangeCash);
        this.mMapMethod.put(getString(R.string.spot_demand_deposits), this.infoExchangeDamand);
        this.mMapMethod.put(getString(R.string.spot_time_deposits), this.infoExchangeTime);

        // TODO : load SQL data
        infoExchange infoExchange1 = new infoExchange("2020-05-17", "", "Account1", "Currency1", "0.1", "", "100", getString(R.string.cash));
        this.infoExchangeCash.add(infoExchange1);
        infoExchange infoExchange2 = new infoExchange("2020-05-19", "", "Account1", "Currency1", "0.1", "", "100", getString(R.string.spot_demand_deposits));
        infoExchange infoExchange3 = new infoExchange("2020-05-19", "", "Account1", "Currency2", "0.1", "", "100", getString(R.string.spot_demand_deposits));
        this.infoExchangeDamand.add(infoExchange2);
        this.infoExchangeDamand.add(infoExchange3);

        // switch CASH page
        this.mTabLayout.selectTab(this.mTabLayout.getTabAt(0));
    }

    private View.OnClickListener clickListener_plus = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            initDialog();
            buildPlusDialog();
        }
    };

    private View.OnClickListener clickListener_sync = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            initDialog();
            buildSyncDialog();
            // TODO : dialog with sync
            //  1. Spot Demand <-> Spot Time
            //  2. Cash <-> Spot Demand
        }
    };

    private Spinner.OnItemSelectedListener selectedListener_Currency = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(adapterView.getSelectedItem().toString().equals("Others"))
                mEditTextCurrencyOthers.setEnabled(true);
            else
                mEditTextCurrencyOthers.setEnabled(false);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private TabLayout.OnTabSelectedListener selectedListener_tab = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mMethod = tab.getText().toString();
            setRecyclerView(mMapMethod.get(mMethod));
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    View.OnClickListener clickListener_date = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View view) {
            final Button btn = (Button)view;

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
            datePickerDialog.setOnCancelListener(new DatePickerDialog.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    btn.setText("");
                }
            });
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    int month = i1+1;
                    btn.setText(i + "-" + month + "-" + i2);
                }
            });
            datePickerDialog.show();
        }
    };

    View.OnClickListener clickListener_expiryDate = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View view) {
            final Button btn = (Button)view;

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
            datePickerDialog.setOnCancelListener(new DatePickerDialog.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    btn.setText("");
                }
            });
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    int month = i1+1;
                    btn.setText(i + "-" + month + "-" + i2);
                }
            });
            datePickerDialog.show();
        }
    };

    class clickListener_Sync implements View.OnClickListener {
        private final AlertDialog mAlertDialog;
        public boolean result = true;

        clickListener_Sync(AlertDialog alertDialog){
            this.mAlertDialog = alertDialog;
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: ");

            if( mButtonDate.getText() == null || mButtonDate.getText().toString().isEmpty() ||
                mButtonExpiryDate.getText() == null || mButtonExpiryDate.getText().toString().isEmpty() ||
                mEditTextInterest.getText() == null || mEditTextInterest.getText().toString().isEmpty() ||
                mEditTextAmount.getText() == null || mEditTextAmount.getText().toString().isEmpty())
            {
                Toast.makeText(getContext(), "Please key in full info.", Toast.LENGTH_LONG).show();
                this.result = false;
                return;
            }

            // TODO : add & remote SQL data
            infoExchange infoExchangeTime = new infoExchange();
            infoExchangeTime.mMethod = mMethod;
            infoExchangeTime.mDate = mButtonDate.getText().toString();
            infoExchangeTime.mExpiryDate = mButtonExpiryDate.getText().toString();
            infoExchangeTime.mAccount = mSpinnerAccount.getSelectedItem().toString();
            infoExchangeTime.mCurrency = mSpinnerCurrency.getSelectedItem().toString();
            infoExchangeTime.mRate = mTextViewCurrentRate.getText().toString();
            infoExchangeTime.mInterest = mEditTextInterest.getText().toString();
            infoExchangeTime.mAmount = mEditTextAmount.getText().toString();

            infoExchange infoExchangeDemand = infoExchangeTime;
            infoExchangeDemand.mInterest = "";
            mMapMethod.get(getString(R.string.spot_time_deposits)).add(infoExchangeTime);
            mMapMethod.get(getString(R.string.spot_demand_deposits)).add(infoExchangeDemand);

            //
            setRecyclerView(mMapMethod.get(getString(R.string.spot_demand_deposits)));
        }
    }

    class clickListener_Sync_and_Exit implements View.OnClickListener {
        private final AlertDialog mAlertDialog;

        clickListener_Sync_and_Exit(AlertDialog alertDialog){
            this.mAlertDialog = alertDialog;
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: ");
            clickListener_Sync clickListenerSync = new clickListener_Sync(mAlertDialog);
            clickListenerSync.onClick(view);

            // close dialog
            if(clickListenerSync.result)
                this.mAlertDialog.dismiss();
        }
    }

    class clickListener_Check implements View.OnClickListener {
        private final AlertDialog mAlertDialog;
        public boolean result = true;

        clickListener_Check(AlertDialog alertDialog){
            this.mAlertDialog = alertDialog;
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: ");
            if( mButtonDate.getText() == null || mButtonDate.getText().toString().isEmpty() ||
                (mSpinnerCurrency.getSelectedItem().toString().equals("Others") && (mEditTextCurrencyOthers.getText() == null || mEditTextCurrencyOthers.getText().toString().isEmpty()) ) ||
                mEditTextRate.getText() == null || mEditTextRate.getText().toString().isEmpty() ||
                mEditTextAmount.getText() == null || mEditTextAmount.getText().toString().isEmpty() )
            {
                Toast.makeText(getContext(), "Please key in full info.", Toast.LENGTH_LONG).show();
                this.result = false;
                return;
            }

            // TODO : add data to SQL
            infoExchange infoExchange = new infoExchange();
            infoExchange.mMethod = mMethod;
            infoExchange.mDate = mButtonDate.getText().toString();
            infoExchange.mExpiryDate = mButtonExpiryDate.getText().toString();
            infoExchange.mAccount = mSpinnerAccount.getSelectedItem().toString();
            infoExchange.mCurrency = mSpinnerCurrency.getSelectedItem().toString();
            infoExchange.mRate = mEditTextRate.getText().toString();
            infoExchange.mAmount = mEditTextAmount.getText().toString();
            if(infoExchange.mCurrency == "Others")
                infoExchange.mCurrency = mEditTextCurrencyOthers.getText().toString();

            //
            mMapMethod.get(mMethod).add(infoExchange);
            mDataAdapter.refresh();
        }
    }

    class clickListener_Check_and_Exit implements View.OnClickListener {
        private final AlertDialog mAlertDialog;

        clickListener_Check_and_Exit(AlertDialog alertDialog){
            this.mAlertDialog = alertDialog;
        }

        @Override
        public void onClick(View view) {
            clickListener_Check clickListenerCheck = new clickListener_Check(mAlertDialog);
            clickListenerCheck.onClick(view);

            // close dialog
            if(clickListenerCheck.result)
                this.mAlertDialog.dismiss();
        }
    }

    class clickListener_Clear implements View.OnClickListener {
        private final AlertDialog mAlertDialog;

        clickListener_Clear(AlertDialog alertDialog){
            this.mAlertDialog = alertDialog;
        }

        @Override
        public void onClick(View view) {
            initDialog();
            buildPlusDialog();
            mAlertDialog.dismiss();
        }
    }

    private void setRecyclerView(List<infoExchange> infoExchangeList){
        // set adapter
        mDataAdapter = new dataAdapter(this.getContext(), infoExchangeList);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mRecyclerView.setAdapter(mDataAdapter);

        // set recyclerview event
        final SwipeController swipeController = new SwipeController(new SwipeControllerActions(){
            @Override
            public void onLeftClicked(int position) {
                Log.d(TAG, "onLeftClicked: ");

                // set this listview to edit
                String date = mDataAdapter.mList.get(position).mDate;
                String expiryDate = mDataAdapter.mList.get(position).mExpiryDate;
                String account = mDataAdapter.mList.get(position).mAccount;
                String currency = mDataAdapter.mList.get(position).mCurrency;
                String rate = mDataAdapter.mList.get(position).mRate;
                String amount = mDataAdapter.mList.get(position).mAmount;

                // set views in dialog
                initDialog();
                buildPlusDialog();
                mButtonDate.setText(date);
                mButtonExpiryDate.setText(expiryDate);
                mSpinnerAccount.setSelection(0);
                for(int i=0; i<mSpinnerAccount.getCount(); i++){
                    if(account.equals(mSpinnerAccount.getItemAtPosition(i)))
                        mSpinnerAccount.setSelection(i);
                }
                mSpinnerCurrency.setSelection(0);
                for(int i=0; i<mSpinnerCurrency.getCount(); i++){
                    if(currency.equals(mSpinnerCurrency.getItemAtPosition(i)))
                        mSpinnerCurrency.setSelection(i);
                }
                mEditTextRate.setText(rate);
                mEditTextAmount.setText(amount);

                // remove this listview
                onRightClicked(position);
            }

            @Override
            public void onRightClicked(int position) {
                Log.d(TAG, "onRightClicked: ");

                // TODO : remove from SQL
                mDataAdapter.mList.remove(position);
                mDataAdapter.refresh();
            }
        }, "EDIT", "REMOVE");

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(this.mRecyclerView);

        this.mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    private void initDialog() {
        this.mButtonDate = new Button(getContext());
        this.mButtonExpiryDate = new Button(getContext());
        this.mSpinnerAccount = new Spinner(getContext());
        this.mSpinnerCurrency = new Spinner(getContext());
        this.mEditTextCurrencyOthers = new EditText(getContext());
        this.mEditTextRate = new EditText(getContext());
        this.mEditTextAmount = new EditText(getContext());
        this.mEditTextInterest = new EditText(getContext());
        this.mTextViewCurrentRate = new TextView(getContext());
        this.mTextViewCurrentAmount = new TextView(getContext());
    }

    private void buildSyncDialog(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // Date
        LinearLayout subLayoutDate = new LinearLayout(getContext());
        subLayoutDate.setGravity(Gravity.CENTER_HORIZONTAL);
        subLayoutDate.setOrientation(LinearLayout.HORIZONTAL);
        subLayoutDate.setLayoutParams(layoutParams);
        this.mButtonDate.setOnClickListener(clickListener_date);
        this.mButtonDate.setHint("Date ...");
        TextView tv = new TextView(getContext());
        tv.setText(" - ");
        this.mButtonExpiryDate.setOnClickListener(clickListener_expiryDate);
        this.mButtonExpiryDate.setHint("Expiry date ...");
        subLayoutDate.addView(mButtonDate);
        subLayoutDate.addView(tv);
        subLayoutDate.addView(mButtonExpiryDate);

        // Account
        // TODO : load sql date to spinner items
        String[] arraySpinner = new String[]{"Account1", "Account2", "Account3"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.spinner, arraySpinner);
        this.mSpinnerAccount.setAdapter(arrayAdapter);
        this.mSpinnerAccount.setSelection(0);

        // Currency
        LinearLayout subLayoutCurrency = new LinearLayout(getContext());
        subLayoutCurrency.setLayoutParams(layoutParams);
        subLayoutCurrency.setOrientation(LinearLayout.HORIZONTAL);
        // TODO : load sql date to spinner items
        this.mSpinnerCurrency.setOnItemSelectedListener(selectedListener_Currency);
        arraySpinner = new String[]{"Currency1", "Currency2", "Currency3", "Others"};
        arrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.spinner, arraySpinner);
        this.mSpinnerCurrency.setAdapter(arrayAdapter);
        this.mSpinnerCurrency.setSelection(0);
        this.mTextViewCurrentRate.setText("NULL");
        subLayoutCurrency.addView(this.mSpinnerCurrency);
        subLayoutCurrency.addView(this.mTextViewCurrentRate);

        // Interest
        this.mEditTextInterest.setKeyListener(DigitsKeyListener.getInstance(false, true));
        this.mEditTextInterest.setHint("Interest");

        // Amount
        LinearLayout subLayoutAmount = new LinearLayout(getContext());
        subLayoutAmount.setLayoutParams(layoutParams);
        subLayoutAmount.setOrientation(LinearLayout.HORIZONTAL);
        this.mEditTextAmount.setKeyListener(DigitsKeyListener.getInstance(false, true));
        this.mEditTextAmount.setHint("Amount ...");
        this.mTextViewCurrentAmount.setText("0");
        subLayoutAmount.addView(this.mEditTextAmount);
        subLayoutAmount.addView(this.mTextViewCurrentAmount);

        // layout
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(subLayoutDate);
        linearLayout.addView(this.mSpinnerAccount);
        linearLayout.addView(subLayoutCurrency);
        linearLayout.addView(this.mEditTextInterest);
        linearLayout.addView(subLayoutAmount);

        // dialog
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(linearLayout)
                .setTitle(this.mMethod)
                .setCancelable(true)
                .setPositiveButton("Sync", null)
                .setNegativeButton("Sync & Exit", null)
                .setNeutralButton("Clear", null)
                .create();
        alertDialog.show();

        Button btnSync = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnSync.setOnClickListener(new clickListener_Sync(alertDialog));

        Button btnSyncAndExit = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btnSyncAndExit.setOnClickListener(new clickListener_Sync_and_Exit(alertDialog));

        Button btnClear = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        btnClear.setOnClickListener(new clickListener_Clear(alertDialog));
    }

    private void buildPlusDialog(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // Date
        LinearLayout subLayoutDate = new LinearLayout(getContext());
        subLayoutDate.setGravity(Gravity.CENTER_HORIZONTAL);
        subLayoutDate.setOrientation(LinearLayout.HORIZONTAL);
        subLayoutDate.setLayoutParams(layoutParams);
        this.mButtonDate.setOnClickListener(clickListener_date);
        this.mButtonDate.setHint("Date ...");
        TextView tv = new TextView(getContext());
        tv.setText(" - ");
        this.mButtonExpiryDate.setOnClickListener(clickListener_expiryDate);
        this.mButtonExpiryDate.setHint("Expiry date ...");
        subLayoutDate.addView(mButtonDate);
        if(mMethod == getString(R.string.spot_time_deposits)){
            subLayoutDate.addView(tv);
            subLayoutDate.addView(mButtonExpiryDate);
        }
        else{
            this.mButtonDate.setLayoutParams(layoutParams);
        }

        // Account
        // TODO : load sql date to spinner items
        String[] arraySpinner = new String[]{"Account1", "Account2", "Account3"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.spinner, arraySpinner);
        this.mSpinnerAccount.setAdapter(arrayAdapter);
        this.mSpinnerAccount.setSelection(0);

        // Currency
        LinearLayout subLayoutCurrency = new LinearLayout(getContext());
        subLayoutCurrency.setLayoutParams(layoutParams);
        subLayoutCurrency.setOrientation(LinearLayout.HORIZONTAL);
        this.mSpinnerCurrency.setOnItemSelectedListener(selectedListener_Currency);
        // TODO : load sql date to spinner items
        arraySpinner = new String[]{"Currency1", "Currency2", "Currency3", "Others"};
        arrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.spinner, arraySpinner);
        this.mSpinnerCurrency.setAdapter(arrayAdapter);
        this.mSpinnerCurrency.setSelection(0);
        //
        this.mEditTextCurrencyOthers.setLayoutParams(layoutParams);
        this.mEditTextCurrencyOthers.setEnabled(false);
        this.mEditTextCurrencyOthers.setHint("Others ...");
        subLayoutCurrency.addView(this.mSpinnerCurrency);
        subLayoutCurrency.addView(this.mEditTextCurrencyOthers);

        // Rate
        LinearLayout subLayoutRate = new LinearLayout(getContext());
        subLayoutRate.setLayoutParams(layoutParams);
        subLayoutRate.setOrientation(LinearLayout.HORIZONTAL);
        this.mEditTextRate.setKeyListener(DigitsKeyListener.getInstance(false, true));
        this.mEditTextRate.setHint("Rate");
        // TODO : load sql data to set current rate, and dynamic compute with amount and rate
        this.mTextViewCurrentRate.setText("NULL");
        subLayoutRate.addView(this.mEditTextRate);
        subLayoutRate.addView(this.mTextViewCurrentRate);

        // Amount
        LinearLayout subLayoutAmount = new LinearLayout(getContext());
        subLayoutAmount.setLayoutParams(layoutParams);
        subLayoutAmount.setOrientation(LinearLayout.HORIZONTAL);
        this.mEditTextAmount.setKeyListener(DigitsKeyListener.getInstance(false, true));
        this.mEditTextAmount.setHint("Amount ...");
        this.mTextViewCurrentAmount.setText("0");
        subLayoutAmount.addView(this.mEditTextAmount);
        subLayoutAmount.addView(this.mTextViewCurrentAmount);

        // layout
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(subLayoutDate);
        linearLayout.addView(this.mSpinnerAccount);
        linearLayout.addView(subLayoutCurrency);
        linearLayout.addView(subLayoutRate);
        linearLayout.addView(subLayoutAmount);

        // dialog
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(linearLayout)
                .setTitle(this.mMethod)
                .setCancelable(true)
                .setPositiveButton("Check", null)
                .setNegativeButton("Check & Exit", null)
                .setNeutralButton("Clear", null)
                .create();
        alertDialog.show();

        Button btnCheck = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnCheck.setOnClickListener(new clickListener_Check(alertDialog));

        Button btnCheckAndExit = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btnCheckAndExit.setOnClickListener(new clickListener_Check_and_Exit(alertDialog));

        Button btnClear = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        btnClear.setOnClickListener(new clickListener_Clear(alertDialog));
    }

    // =================================================================

    private static class infoExchange{
        String mDate;
        String mAmount;
        String mCurrency;
        String mRate;
        String mAccount;
        String mExpiryDate;
        String mMethod;
        String mInterest;
        infoExchange(){}
        infoExchange(String date, String expiryDate, String account, String currency,
                     String rate, String interest, String amount, String method)
        {
            this.mDate = date;
            this.mAmount = amount;
            this.mCurrency = currency;
            this.mRate = rate;
            this.mInterest = interest;
            this.mAccount = account;
            this.mExpiryDate = expiryDate;
            this.mMethod = method;
        }
    }

    // =================================================================

    private static class dataAdapter extends RecyclerView.Adapter<dataAdapter.ViewHolder>{
        List<exchange.infoExchange> mList;
        private Context mContext;

        static class ViewHolder extends RecyclerView.ViewHolder{
            TextView tv_Date, tv_Period, tv_Account, tv_Currency, tv_Rate, tv_Amount;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.tv_Date = itemView.findViewById(R.id.textView_date);
                this.tv_Period = itemView.findViewById(R.id.textView_expiryDate);
                this.tv_Account = itemView.findViewById(R.id.textView_account);
                this.tv_Currency = itemView.findViewById(R.id.textView_currency);
                this.tv_Rate = itemView.findViewById(R.id.textView_rate);
                this.tv_Amount = itemView.findViewById(R.id.textView_amount);
            }
        }

        dataAdapter(Context context, List<infoExchange> list) {
            super();

            this.mList = list;
            this.mContext = context;
        }

        @NonNull
        @Override
        public dataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_exchange_plus, parent, false);
            return new dataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull dataAdapter.ViewHolder holder, int position) {
            infoExchange infoExchange = mList.get(position);
            holder.tv_Date.setText(infoExchange.mDate);
            holder.tv_Period.setText(infoExchange.mExpiryDate);
            holder.tv_Period.setVisibility( (infoExchange.mMethod==this.mContext.getString(R.string.spot_time_deposits)) ?
                                            View.VISIBLE : View.GONE );
            holder.tv_Account.setText(infoExchange.mAccount);
            holder.tv_Currency.setText(infoExchange.mCurrency);
            holder.tv_Rate.setText(infoExchange.mRate);
            holder.tv_Amount.setText(infoExchange.mAmount);
        }

        @Override
        public int getItemCount() {
            return this.mList.size();
        }

        void refresh(){
            this.notifyDataSetChanged();
        }
    }
}

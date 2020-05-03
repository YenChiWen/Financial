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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.Financial.R;
import com.example.Financial.SwipeController.SwipeController;
import com.example.Financial.SwipeController.SwipeControllerActions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class account_receivable_payable extends Fragment {
    private String TAG = this.getClass().getSimpleName();
    private int mId = -1;
    private List<infoReceivablePayable> mListInfoReceivablePayable = new ArrayList<>();
    private dataAdapter mDataAdapter = null;

    // view
    private FloatingActionButton mFabPlus = null;
    private RecyclerView mRecyclerView = null;
    private Spinner mSpinnerCurrency = null;
    private EditText mEditTextCurrency = null;
    private Button mButtonDateStart = null;
    private Button mButtonDateEnd = null;
    private EditText mEditTextRemark = null;
    private EditText mEditTextAmount = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_receivable_payable, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.getArguments() != null) {
            this.mId = this.getArguments().getInt("ID");
        }

        if(getView() != null){
            // get View
            this.mFabPlus = getView().findViewById(R.id.fab_plus_ARAP);
            this.mRecyclerView = getView().findViewById(R.id.recyclerview_ARAP);

            // set listener
            this.mFabPlus.setOnClickListener(this.clickListener_Plus);

            setRecyclerView();
        }
    }

    private void setRecyclerView(){
        // set adapter
        mDataAdapter = new dataAdapter(this.getContext(), this.mListInfoReceivablePayable);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mRecyclerView.setAdapter(mDataAdapter);

        // set recyclerview event
        final SwipeController swipeController = new SwipeController(new SwipeControllerActions(){
            @Override
            public void onLeftClicked(int position) {
                Log.d(TAG, "onLeftClicked: ");

                // set this listview to edit
                String dateStart = mDataAdapter.mList.get(position).mDateStart;
                String dateEnd = mDataAdapter.mList.get(position).mDateEnd;
                String currancy = mDataAdapter.mList.get(position).mCurrency;
                String amount = mDataAdapter.mList.get(position).mAmount;
                String remark = mDataAdapter.mList.get(position).mRemark;

                // set views in dialog
                initDialog();
                mSpinnerCurrency.setSelection(0);
                for(int i=0; i<mSpinnerCurrency.getCount(); i++){
                    if(currancy.equals(mSpinnerCurrency.getItemAtPosition(i)))
                        mSpinnerCurrency.setSelection(i);
                }
                mButtonDateStart.setText(dateStart);
                mButtonDateEnd.setText(dateEnd);
                mEditTextAmount.setText(amount);
                mEditTextRemark.setText(remark);

                // remove this listview
                onRightClicked(position);
                buildDialog();
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

    private void initDialog(){
        this.mButtonDateStart = new Button(getContext());
        this.mSpinnerCurrency = new Spinner(getContext());
        this.mEditTextCurrency = new EditText(getContext());
        this.mEditTextAmount = new EditText(getContext());
        this.mEditTextRemark = new EditText(getContext());
        this.mButtonDateEnd = new Button(getContext());
    }

    private void buildDialog(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // Date
        this.mButtonDateStart.setOnClickListener(clickListener_date);
        this.mButtonDateStart.setHint("Start Date ...");

        // Currency
        LinearLayout subLayoutCurrency = new LinearLayout(getContext());
        subLayoutCurrency.setLayoutParams(layoutParams);
        subLayoutCurrency.setOrientation(LinearLayout.HORIZONTAL);
        this.mSpinnerCurrency.setOnItemSelectedListener(selectedListener_Currency);
        // TODO : load sql date to spinner items
        String[] arraySpinner = new String[]{"Currency1", "Currency2", "Currency3", "Others"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.spinner, arraySpinner);
        this.mSpinnerCurrency.setAdapter(arrayAdapter);
        this.mSpinnerCurrency.setSelection(0);
        //
        this.mEditTextCurrency.setLayoutParams(layoutParams);
        this.mEditTextCurrency.setEnabled(false);
        this.mEditTextCurrency.setHint("Others ...");
        subLayoutCurrency.addView(this.mSpinnerCurrency);
        subLayoutCurrency.addView(this.mEditTextCurrency);

        // Amount
        this.mEditTextAmount.setKeyListener(DigitsKeyListener.getInstance(false, true));
        this.mEditTextAmount.setHint("Amount ...");

        // Remark
        this.mEditTextRemark.setHint("Remark ...");

        // Repayment date
        this.mButtonDateEnd.setOnClickListener(clickListener_date);
        this.mButtonDateEnd.setHint("End Date ...");

        // layout
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(this.mButtonDateStart);
        linearLayout.addView(subLayoutCurrency);
        linearLayout.addView(this.mEditTextAmount);
        linearLayout.addView(this.mEditTextRemark);
        linearLayout.addView(this.mButtonDateEnd);

        // dialog
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(linearLayout)
                .setTitle((mId==R.id.nav_account_receivable) ? "Add Accounts Receivable" : "Add Accounts Payable")
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

     View.OnClickListener clickListener_Plus = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            initDialog();
            buildDialog();
        }
    };

    class clickListener_Check implements View.OnClickListener {
        private final AlertDialog mAlertDialog;

        clickListener_Check(AlertDialog alertDialog){
            this.mAlertDialog = alertDialog;
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: " + mSpinnerCurrency.getSelectedItem().toString());
            if( mButtonDateStart.getText() == null || mButtonDateStart.getText().toString().isEmpty() ||
                    mEditTextAmount.getText() == null || mEditTextAmount.getText().toString().isEmpty() ||
                    mEditTextRemark.getText() == null || mEditTextRemark.getText().toString().isEmpty() ||
                    mSpinnerCurrency.getSelectedItem() == null || mSpinnerCurrency.getSelectedItem().toString().isEmpty() ||
                    ( mSpinnerCurrency.getSelectedItem().toString().equals("Others") && (mEditTextCurrency.getText() == null || mEditTextCurrency.getText().toString().isEmpty())))
            {
                Toast.makeText(getContext(), "Please key in full info.", Toast.LENGTH_LONG).show();
                return;
            }

            // TODO : add data to SQL
            infoReceivablePayable infoReceivablePayable = new infoReceivablePayable();
            infoReceivablePayable.mDateStart = mButtonDateStart.getText().toString();
            infoReceivablePayable.mDateEnd = mButtonDateEnd.getText().toString();
            infoReceivablePayable.mCurrency = mSpinnerCurrency.getSelectedItem().toString();
            infoReceivablePayable.mAmount = mEditTextAmount.getText().toString();
            infoReceivablePayable.mRemark = mEditTextRemark.getText().toString();
            if(infoReceivablePayable.mCurrency.equals("Others")){
                infoReceivablePayable.mCurrency = mEditTextCurrency.getText().toString();
            }

            //
            mListInfoReceivablePayable.add(infoReceivablePayable);
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
            buildDialog();
            mAlertDialog.dismiss();
        }
    }

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

    Spinner.OnItemSelectedListener selectedListener_Currency = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(adapterView.getSelectedItem().toString().equals("Others"))
                mEditTextCurrency.setEnabled(true);
            else
                mEditTextCurrency.setEnabled(false);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    // =================================================================

    private static class infoReceivablePayable{
        String mDateStart;
        String mDateEnd;
        String mRemark;
        String mAmount;
        String mCurrency;
        infoReceivablePayable(){}
    }

    // =================================================================

    private static class dataAdapter extends RecyclerView.Adapter<dataAdapter.ViewHolder>{
        List<infoReceivablePayable> mList;
        private Context mContext;

        static class ViewHolder extends RecyclerView.ViewHolder{
            TextView textviewDateStart,
                    textviewRemark,
                    textviewAmount,
                    textviewDateEnd,
                    textviewCurrency;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.textviewDateStart = itemView.findViewById(R.id.textView_dateStart);
                this.textviewAmount = itemView.findViewById(R.id.textView_amount);
                this.textviewCurrency = itemView.findViewById(R.id.textView_currency);
                this.textviewRemark = itemView.findViewById(R.id.textView_remark);
                this.textviewDateEnd = itemView.findViewById(R.id.textView_dateEnd);
            }
        }

        dataAdapter(Context context, List<infoReceivablePayable> list) {
            super();

            this.mList = list;
            this.mContext = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_receivable_payable, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            infoReceivablePayable infoReceivablePayable = mList.get(position);
            holder.textviewDateStart.setText(infoReceivablePayable.mDateStart);
            holder.textviewCurrency.setText(infoReceivablePayable.mCurrency);
            holder.textviewAmount.setText(infoReceivablePayable.mAmount);
            holder.textviewRemark.setText(infoReceivablePayable.mRemark);
            holder.textviewDateEnd.setText(infoReceivablePayable.mDateEnd);
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

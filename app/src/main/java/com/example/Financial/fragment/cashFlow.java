package com.example.Financial.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Financial.R;
import com.example.Financial.SwipeController.SwipeController;
import com.example.Financial.SwipeController.SwipeControllerActions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;


import java.util.ArrayList;
import java.util.List;

public class cashFlow extends Fragment {
    private String TAG = this.getClass().getSimpleName();
    private int mId = -1;
    private List<infoIncome> mListIncomeInfo = new ArrayList<>();
    private dataAdapter mDataAdapter = null;

    // View
    private RecyclerView mRecyclerView = null;
    private MaterialCalendarView mCalendarView = null;
    private FloatingActionButton mFabPlus = null;
    private EditText mEtRemark = null;
    private EditText mEtAmount = null;
    private Spinner mSpinnerItems = null;
    private EditText mEtOtherItems = null;
    private Spinner mSpinnerMethod = null;
    private EditText mEtOtherMethod = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cash_flow, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.getArguments() != null) {
            this.mId = this.getArguments().getInt("ID");
        }

        if(this.getView() != null){
            // get view
            this.mRecyclerView = getView().findViewById(R.id.recyclerview_cashFlow);
            this.mCalendarView = getView().findViewById(R.id.materialCalendarView);
            this.mFabPlus = getView().findViewById(R.id.fab_plus_cashFlow);

            // set listener
            this.mFabPlus.setOnClickListener(clickListener_Plus);
            this.setCalendarView();
            this.setRecyclerView();
        }
    }

    private void initDialog(){
        this.mEtAmount = new EditText(getContext());
        this.mEtRemark = new EditText(getContext());
        this.mSpinnerItems = new Spinner(getContext());
        this.mEtOtherItems = new EditText(getContext());
        this.mSpinnerMethod = new Spinner(getContext());
        this.mEtOtherMethod = new EditText(getContext());
    }

    private void buildDialog(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // item
        LinearLayout layoutItem = new LinearLayout(getContext());
        layoutItem.setOrientation(LinearLayout.HORIZONTAL);
        layoutItem.setLayoutParams(layoutParams);
        this.setSpinnerItems();
        this.mEtOtherItems.setEnabled(false);
        this.mEtOtherItems.setLayoutParams(layoutParams);
        layoutItem.addView(this.mSpinnerItems);
        layoutItem.addView(this.mEtOtherItems);
        // method
        LinearLayout layoutMethod = new LinearLayout(getContext());
        layoutMethod.setOrientation(LinearLayout.HORIZONTAL);
        layoutMethod.setLayoutParams(layoutParams);
        this.setSpinnerMethod();
        this.mEtOtherMethod.setEnabled(false);
        this.mEtOtherMethod.setLayoutParams(layoutParams);
        layoutMethod.addView(this.mSpinnerMethod);
        layoutMethod.addView(this.mEtOtherMethod);
        // remark
        this.mEtRemark.setHint("Remark ...");
        // amount
        this.mEtAmount.setKeyListener(DigitsKeyListener.getInstance(false, true));
        this.mEtAmount.setHint("Amount ...");
        // main layout
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(layoutItem);
        linearLayout.addView(layoutMethod);
        linearLayout.addView(this.mEtAmount);
        linearLayout.addView(this.mEtRemark);

        // dialog
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(linearLayout)
                .setTitle(mCalendarView.getSelectedDate().getDate().toString())
                .setCancelable(true)
                .setPositiveButton("Check", null)
                .setNegativeButton("Check & Exit", null)
                .setNeutralButton("Clear", null)
                .create();
        alertDialog.show();

        Button btnCheck = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnCheckAndExit = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button btnClear = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        btnCheck.setOnClickListener(new clickListener_Check(alertDialog));
        btnCheckAndExit.setOnClickListener(new clickListener_Check_and_Exit(alertDialog));
        btnClear.setOnClickListener(new clickListener_Clear(alertDialog));
    }

    class clickListener_Check implements View.OnClickListener {
        private final AlertDialog mAlertDialog;

        clickListener_Check(AlertDialog alertDialog){
            this.mAlertDialog = alertDialog;
        }

        @Override
        public void onClick(View view) {
            if (    mSpinnerItems.getSelectedItem() == null || mSpinnerItems.getSelectedItem().toString().isEmpty() ||
                    (mSpinnerItems.getSelectedItem().equals("Others") && (mEtOtherItems.getText() == null || mEtOtherItems.getText().toString().isEmpty())) ||
                    mSpinnerMethod.getSelectedItem() == null || mSpinnerMethod.getSelectedItem().toString().isEmpty() ||
                    (mSpinnerMethod.getSelectedItem().equals("Others") && (mEtOtherMethod.getText() == null || mEtOtherMethod.getText().toString().isEmpty())) ||
                    mEtAmount.getText() == null || mEtAmount.getText().toString().isEmpty() ||
                    mEtRemark.getText() == null || mEtRemark.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please key in full info.", Toast.LENGTH_LONG).show();
                return;
            }

            // TODO : add data to SQL
            infoIncome inComeInfo = new infoIncome();
            inComeInfo.mDate = mCalendarView.getSelectedDate().getDate().toString();
            inComeInfo.mItem = mSpinnerItems.getSelectedItem().toString();
            inComeInfo.mRemark = mEtRemark.getText().toString();
            inComeInfo.mMethod = mSpinnerMethod.getSelectedItem().toString();
            inComeInfo.mAmount = mEtAmount.getText().toString();
            if(inComeInfo.mItem.equals("Others"))
                inComeInfo.mItem = mEtOtherItems.getText().toString();
            if(inComeInfo.mMethod.equals("Others"))
                inComeInfo.mMethod = mEtOtherMethod.getText().toString();

            mListIncomeInfo.add(inComeInfo);
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
            clickListener_Check clickListenerCheck = new clickListener_Check(this.mAlertDialog);
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

    View.OnClickListener clickListener_Plus = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            initDialog();
            buildDialog();
        }
    };

    private OnDateLongClickListener longClickListener_Date = new OnDateLongClickListener() {
        @Override
        public void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {
            if( widget.getCalendarMode().equals(CalendarMode.MONTHS) )
                widget.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();
            else
                widget.state().edit().setCalendarDisplayMode(CalendarMode.MONTHS).commit();
        }
    };

    private OnDateSelectedListener dateSelectedListener_Date = new OnDateSelectedListener() {
        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            // set calendar view as one week only
            if(!mCalendarView.getCalendarMode().equals(CalendarMode.WEEKS))
                mCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();

            // TODO : show SQL data as listview
            mListIncomeInfo.clear();
            mDataAdapter.refresh();
        }
    };

    private Spinner.OnItemSelectedListener selectedListener_Item_Method = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(adapterView.getId() == R.id.spinner_items_cashFlow){
                if(adapterView.getSelectedItem().toString().equals("Others"))
                    mEtOtherItems.setEnabled(true);
                else
                    mEtOtherItems.setEnabled(false);
            }

            if(adapterView.getId() == R.id.spinner_method_cashFlow){
                if(adapterView.getSelectedItem().toString().equals("Others"))
                    mEtOtherMethod.setEnabled(true);
                else
                    mEtOtherMethod.setEnabled(false);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void setSpinnerMethod(){
        this.mSpinnerMethod.setId(R.id.spinner_method_cashFlow);
        // set listener
        this.mSpinnerMethod.setOnItemSelectedListener(selectedListener_Item_Method);

        // TODO : load sql date to spinner items
        String[] arraySpinner = new String[]{};
        if(this.mId == R.id.nav_income){
             arraySpinner = new String[]{"Income_method1", "Income_method2", "Income_method3", "Others"};
        }
        else if(this.mId == R.id.nav_expenses){
            arraySpinner = new String[]{"Expenses_method1", "Expenses_method2", "Expenses_method3", "Others"};
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.spinner, arraySpinner);
        this.mSpinnerMethod.setAdapter(arrayAdapter);
    }

    private void setSpinnerItems(){
        this.mSpinnerItems.setId(R.id.spinner_items_cashFlow);
        // set listener
        this.mSpinnerItems.setOnItemSelectedListener(selectedListener_Item_Method);

        // TODO : load sql date to spinner items
        String[] arraySpinner = new String[]{"test1", "test2", "test3", "Others"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.spinner, arraySpinner);
        this.mSpinnerItems.setAdapter(arrayAdapter);
    }

    private void setCalendarView(){
        // set listener
        this.mCalendarView.setOnDateChangedListener(dateSelectedListener_Date);
        this.mCalendarView.setOnDateLongClickListener(longClickListener_Date);

        // set current date
        this.mCalendarView.setSelectedDate(this.mCalendarView.getCurrentDate());

        // set calendar view as one week only
        this.mCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();
    }

    private void setRecyclerView(){
        // set adapter
        mDataAdapter = new dataAdapter(this.getContext(), this.mListIncomeInfo);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mRecyclerView.setAdapter(mDataAdapter);

        // set recyclerview event
        final SwipeController swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                Log.d(TAG, "onLeftClicked: ");

                // set this listview to edit
                String amount = mDataAdapter.mList.get(position).mAmount;
                String remark = mDataAdapter.mList.get(position).mRemark;
                String item = mDataAdapter.mList.get(position).mItem;
                String method = mDataAdapter.mList.get(position).mMethod;

                // set views in dialog
                initDialog();
                buildDialog();
                mSpinnerItems.setSelection(0);
                for(int i=0; i<mSpinnerItems.getCount(); i++){
                    if(item.equals(mSpinnerItems.getItemAtPosition(i)))
                        mSpinnerItems.setSelection(i);
                }
                mSpinnerMethod.setSelection(0);
                for(int i=0; i<mSpinnerMethod.getCount(); i++){
                    if(method.equals(mSpinnerMethod.getItemAtPosition(i)))
                        mSpinnerMethod.setSelection(i);
                }
                mEtRemark.setText(remark);
                mEtAmount.setText(amount);

                // remove this listview
                onRightClicked(position);
            }

            @Override
            public void onRightClicked(int position) {
                Log.d(TAG, "onRightClicked: ");

                // TODO : remove form SQL
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

    // =================================================================

    private static class infoIncome {
        String mDate;
        String mItem;
        String mRemark;
        String mMethod;
        String mAmount;
        infoIncome(){}
    }

    // =================================================================

    private static class dataAdapter extends RecyclerView.Adapter<dataAdapter.ViewHolder>{
        List<infoIncome> mList;
        private Context mContext;

        static class ViewHolder extends RecyclerView.ViewHolder{
            TextView textviewItem, textviewRemark, textviewAmount, textviewMethod;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.textviewItem = itemView.findViewById(R.id.textView_item);
                this.textviewAmount = itemView.findViewById(R.id.textView_amount);
                this.textviewRemark = itemView.findViewById(R.id.textView_remark);
                this.textviewMethod = itemView.findViewById(R.id.textView_method);
            }
        }

        dataAdapter(Context context, List<infoIncome> list) {
            super();

            this.mList = list;
            this.mContext = context;
        }

        @NonNull
        @Override
        public dataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_cash_flow, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull dataAdapter.ViewHolder holder, int position) {
            infoIncome inComeInfo = mList.get(position);
            holder.textviewItem.setText(inComeInfo.mItem);
            holder.textviewAmount.setText(inComeInfo.mAmount);
            holder.textviewRemark.setText(inComeInfo.mRemark);
            holder.textviewMethod.setText(inComeInfo.mMethod);
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

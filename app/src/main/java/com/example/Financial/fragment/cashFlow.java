package com.example.Financial.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Financial.R;
import com.example.Financial.SwipeController.SwipeController;
import com.example.Financial.SwipeController.SwipeControllerActions;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;


import java.util.ArrayList;
import java.util.List;

public class cashFlow extends Fragment {
    String TAG = this.getClass().getSimpleName();
    int mId = -1;
    List<inComeInfo> mListIncomeInfo = new ArrayList<>();
    dataAdapter mDataAdapter = null;

    // View
    RecyclerView mRecyclerView = null;
    MaterialCalendarView mCalendarView = null;
    Button mBtnCheck = null;
    Button mBtnClear = null;
    EditText mEtRemark = null;
    EditText mEtAmount = null;
    Spinner mSpinnerItems = null;
    Spinner mSpinnerMethod = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cash_flow, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mId = this.getArguments().getInt("ID");

        // get view
        this.mRecyclerView = getView().findViewById(R.id.recyclerview_cashFlow);
        this.mCalendarView = getView().findViewById(R.id.materialCalendarView);
        this.mBtnCheck = getView().findViewById(R.id.btn_check_cashFlow);
        this.mBtnClear = getView().findViewById(R.id.btn_clear_cashFlow);
        this.mEtAmount = getView().findViewById(R.id.editText_amount_cashFlow);
        this.mEtRemark = getView().findViewById(R.id.editText_remark_cashFlow);
        this.mSpinnerItems = getView().findViewById(R.id.spinner_items_cashFlow);
        this.mSpinnerMethod = getView().findViewById(R.id.spinner_method_cashFlow);

        // set listener
        this.mCalendarView.setOnDateChangedListener(dateSelectedListener);
        this.mBtnCheck.setOnClickListener(btnCheckListener);
        this.mBtnClear.setOnClickListener(btnClearListener);

        //
        this.setCalendarView();
        this.setRecyclerView();
        this.setSpinnerItems();
        this.setSpinnerMethod();
    }

    OnDateLongClickListener dateLongClickListener = new OnDateLongClickListener() {
        @Override
        public void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {
            if( widget.getCalendarMode().equals(CalendarMode.MONTHS) )
                widget.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();
            else
                widget.state().edit().setCalendarDisplayMode(CalendarMode.MONTHS).commit();
        }
    };

    OnDateSelectedListener dateSelectedListener = new OnDateSelectedListener() {
        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            // TODO : show SQL data as listview
            mListIncomeInfo.clear();
            mDataAdapter.refresh();
        }
    };

    final View.OnClickListener btnCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mSpinnerItems.getSelectedItem() == null || mSpinnerItems.getSelectedItem().toString().isEmpty() ||
                    mSpinnerMethod.getSelectedItem() == null || mSpinnerMethod.getSelectedItem().toString().isEmpty() ||
                    mEtAmount.getText() == null || mEtAmount.getText().toString().isEmpty() ||
                    mEtRemark.getText() == null || mEtRemark.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please key in full info.", Toast.LENGTH_LONG).show();
                return;
            }

            // TODO : add data to SQL
            inComeInfo inComeInfo = new inComeInfo();
            inComeInfo.mDate = mCalendarView.getSelectedDate().getDate().toString();
            inComeInfo.mItem = mSpinnerItems.getSelectedItem().toString();
            inComeInfo.mRemark = mEtRemark.getText().toString();
            inComeInfo.mMethod = mSpinnerMethod.getSelectedItem().toString();
            inComeInfo.mAmount = mEtAmount.getText().toString();
            mListIncomeInfo.add(inComeInfo);
            mDataAdapter.refresh();
        }
    };

    View.OnClickListener btnClearListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mEtAmount.getText().clear();
            mEtRemark.getText().clear();
            mSpinnerItems.setSelection(0);
        }
    };

    private void setSpinnerMethod(){
        // TODO : load sql date to spinner items
        String[] arraySpinner = new String[]{};
        if(this.mId == R.id.nav_income){
             arraySpinner = new String[]{"Income_method1", "Income_method2", "Income_method3"};
        }
        else if(this.mId == R.id.nav_expenses){
            arraySpinner = new String[]{"Expenses_method1", "Expenses_method2", "Expenses_method3"};
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.getContext(), R.layout.spinner, arraySpinner);
        this.mSpinnerMethod.setAdapter(arrayAdapter);
    }

    private void setSpinnerItems(){
        // TODO : load sql date to spinner items
        String[] arraySpinner = new String[]{"test1", "test2", "test3"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.getContext(), R.layout.spinner, arraySpinner);
        this.mSpinnerItems.setAdapter(arrayAdapter);
    }

    private void setCalendarView(){
        // set current date
        this.mCalendarView.setSelectedDate(this.mCalendarView.getCurrentDate());

        // set calendar view as one week only
        this.mCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();

        this.mCalendarView.setOnDateLongClickListener(dateLongClickListener);
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

                mSpinnerItems.setSelection(0);
                for(int i=0; i<mSpinnerItems.getCount(); i++) {
                    if (item.equals(mSpinnerItems.getItemAtPosition(i)))
                        mSpinnerItems.setSelection(i);
                }
                mSpinnerMethod.setSelection(0);
                for(int i=0; i<mSpinnerMethod.getCount(); i++) {
                    if (method.equals(mSpinnerMethod.getItemAtPosition(i)))
                        mSpinnerMethod.setSelection(i);
                }
                mEtRemark.setText(remark);
                mEtAmount.setText(amount);

                // remove this listview
                onRightClicked(position);

                // set focus
                mEtRemark.setFocusable(true);
                mEtRemark.requestFocus();
            }

            @Override
            public void onRightClicked(int position) {
                Log.d(TAG, "onRightClicked: ");

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

    private class inComeInfo{
        public String mDate;
        public String mItem;
        public String mRemark;
        public String mMethod;
        public String mAmount;


        public inComeInfo(){ }
    }

    // =================================================================

    private class dataAdapter extends RecyclerView.Adapter<dataAdapter.ViewHolder>{
        public List<inComeInfo> mList;
        private Context mContext;

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView textviewItem, textviewRemark, textviewAmount, textviewMethod;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.textviewItem = itemView.findViewById(R.id.textView_item);
                this.textviewAmount = itemView.findViewById(R.id.textView_amount);
                this.textviewRemark = itemView.findViewById(R.id.textView_remark);
                this.textviewMethod = itemView.findViewById(R.id.textView_method);
            }
        }

        public dataAdapter(Context context, List<inComeInfo> list) {
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
            inComeInfo inComeInfo = mList.get(position);
            holder.textviewItem.setText(inComeInfo.mItem);
            holder.textviewAmount.setText(inComeInfo.mAmount);
            holder.textviewRemark.setText(inComeInfo.mRemark);
            holder.textviewMethod.setText(inComeInfo.mMethod);
        }

        @Override
        public int getItemCount() {
            return this.mList.size();
        }

        public void refresh(){
            this.notifyDataSetChanged();
        }
    }
}

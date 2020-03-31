package com.example.Financial;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class income extends Fragment {
    String TAG = this.getClass().getSimpleName();
    final DateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    List<inComeInfo> mListIncomeInfo = new ArrayList<>();
    adapter mAdapter = null;

    // View
    ListView mListView = null;
    CalendarView mCalendarView = null;
    Button mBtnCheck = null;
    Button mBtnClear = null;
    EditText mEtRemark = null;
    EditText mEtAmount = null;
    Spinner mSpinnerItems = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get view
        this.mCalendarView = getView().findViewById(R.id.calendarView);
        this.mBtnCheck = getView().findViewById(R.id.btn_check_income);
        this.mBtnClear = getView().findViewById(R.id.btn_clear_income);
        this.mEtAmount = getView().findViewById(R.id.editText_amount_income);
        this.mEtRemark = getView().findViewById(R.id.editText_remark_income);
        this.mSpinnerItems = getView().findViewById(R.id.spinner_items_income);
        this.mListView = getView().findViewById(R.id.listview_income);

        // set listener
        this.mCalendarView.setOnDateChangeListener(dateChangeListener);
        this.mBtnCheck.setOnClickListener(btnCheckListener);
        this.mBtnClear.setOnClickListener(btnClearListener);

        // set spinner
        setSpinnerItems();

        // set list view
        this.mAdapter = new adapter(this.getContext(), this.mListIncomeInfo);
        this.mListView.setAdapter(this.mAdapter);
    }

    CalendarView.OnDateChangeListener dateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
            // TODO : show SQL data as listview

            mListIncomeInfo.clear();
        }
    };

    View.OnClickListener btnCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if( mSpinnerItems.getSelectedItem() == null || mSpinnerItems.getSelectedItem().toString().isEmpty() ||
                mEtAmount.getText() == null || mEtAmount.getText().toString().isEmpty() ||
                mEtRemark.getText() == null || mEtRemark.getText().toString().isEmpty() )
            {
                Toast.makeText(getContext(), "Please key in full info.", Toast.LENGTH_LONG).show();
                return;
            }

            // TODO : add data to SQL
            inComeInfo inComeInfo = new inComeInfo();
            inComeInfo.mDate = mDateFormat.format(mCalendarView.getDate());
            inComeInfo.mItem = mSpinnerItems.getSelectedItem().toString();
            inComeInfo.mAmount = mEtAmount.getText().toString();
            inComeInfo.mRemark = mEtRemark.getText().toString();
            mListIncomeInfo.add(inComeInfo);

            mAdapter.refresh(mListIncomeInfo);
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

    private void setSpinnerItems(){
        // TODO : load sql date to spinner items
        String[] arraySpinner = new String[]{"test1", "test2", "test3"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.getContext(), R.layout.spinner, arraySpinner);
        this.mSpinnerItems.setAdapter(arrayAdapter);
    }

    private void setCalendarView(){
        // TODO : set calendar view as one week only
        // https://github.com/Tibolte/AgendaCalendarView
    }

    // =================================================================

    private class inComeInfo{
        public String mDate;
        public String mItem;
        public String mAmount;
        public String mRemark;

        public inComeInfo(){ };
    }

    // =================================================================

    private class adapter extends BaseAdapter {
        // TODO remove recycle
        // http://hulkyang.blogspot.com/2019/01/recyclerview-itemtouchhelper.html

        private ViewHolder mViewHolder = null;
        private LayoutInflater mLayoutInflater = null;
        private Context mContext = null;
        List<inComeInfo> mList = new ArrayList<>();

        class ViewHolder{
            TextView textviewItem = null;
            TextView textviewAmount = null;
            TextView textviewRemark = null;
        }

        adapter(Context context, List<inComeInfo> list){
            this.mList = list;
            this.mContext = context;
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return this.mList.size();
        }

        @Override
        public Object getItem(int i) {
            return this.mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // create item
            mViewHolder = null;
            if(view == null){
                view = mLayoutInflater.inflate(R.layout.listview_income, null);
                mViewHolder = new ViewHolder();
                mViewHolder.textviewItem = view.findViewById(R.id.textView_item);
                mViewHolder.textviewRemark = view.findViewById(R.id.textView_remark);
                mViewHolder.textviewAmount = view.findViewById(R.id.textView_amount);
                view.setTag(mViewHolder);
            }
            else{
                mViewHolder = (ViewHolder) view.getTag();
            }

            // set item ui
            inComeInfo inComeInfo = (inComeInfo) getItem(i);
            mViewHolder.textviewItem.setText(inComeInfo.mItem);
            mViewHolder.textviewRemark.setText(inComeInfo.mRemark);
            mViewHolder.textviewAmount.setText(inComeInfo.mAmount);

            return view;
        }

        public void refresh(List<inComeInfo> list){
            notifyDataSetChanged();
        }
    }
}

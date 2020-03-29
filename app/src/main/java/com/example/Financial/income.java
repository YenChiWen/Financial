package com.example.Financial;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class income extends Fragment {
    String TAG = this.getClass().getSimpleName();
    Context mContext = null;

    // View
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

    public income(Context context){
        this. mContext = context;

        // get view
        this.mCalendarView = getView().findViewById(R.id.calendarView);
        this.mBtnCheck = getView().findViewById(R.id.btn_check_income);
        this.mBtnClear = getView().findViewById(R.id.btn_clear_income);
        this.mEtAmount = getView().findViewById(R.id.editText_amount_income);
        this.mEtRemark = getView().findViewById(R.id.editText_remark_income);
        this.mSpinnerItems = getView().findViewById(R.id.spinner_items_income);

        // set listener
        this.mCalendarView.setOnDateChangeListener(dateChangeListener);
        this.mBtnCheck.setOnClickListener(btnCheckListener);
        this.mBtnClear.setOnClickListener(btnClearListener);
    }

    CalendarView.OnDateChangeListener dateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
            // TODO : show SQL data as listview
        }
    };

    View.OnClickListener btnCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO : add data to SQL
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

    private void setCalendarView(){
        // TODO : set calendar view as one week only
        // https://github.com/Tibolte/AgendaCalendarView
    }






    private class adapter extends BaseAdapter {
        Date mDate = new Date();
        String mItem = "";
        String mRemark = "";
        int mAmount = 0;

        public void adapter(Date date, String item, int amount, String remark){
            this.mDate = date;
            this.mItem = item;
            this.mAmount = amount;
            this.mRemark = remark;
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }
}

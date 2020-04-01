package com.example.Financial;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;


import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class income extends Fragment {
    String TAG = this.getClass().getSimpleName();
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get view
        this.mCalendarView = getView().findViewById(R.id.materialCalendarView);
        this.mBtnCheck = getView().findViewById(R.id.btn_check_income);
        this.mBtnClear = getView().findViewById(R.id.btn_clear_income);
        this.mEtAmount = getView().findViewById(R.id.editText_amount_income);
        this.mEtRemark = getView().findViewById(R.id.editText_remark_income);
        this.mSpinnerItems = getView().findViewById(R.id.spinner_items_income);
        this.mRecyclerView = getView().findViewById(R.id.recyclerview_income);

        // set listener
        this.mCalendarView.setOnDateChangedListener(dateSelectedListener);
        this.mBtnCheck.setOnClickListener(btnCheckListener);
        this.mBtnClear.setOnClickListener(btnClearListener);

        // set list view
        mDataAdapter = new dataAdapter(this.getContext(), this.mListIncomeInfo);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mRecyclerView.setAdapter(mDataAdapter);

        //
        setSpinnerItems();
        setCalendarView();
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
                    mEtAmount.getText() == null || mEtAmount.getText().toString().isEmpty() ||
                    mEtRemark.getText() == null || mEtRemark.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please key in full info.", Toast.LENGTH_LONG).show();
                return;
            }

            // TODO : add data to SQL
            inComeInfo inComeInfo = new inComeInfo();
            inComeInfo.mDate = mCalendarView.getSelectedDate().getDate().toString();
            inComeInfo.mItem = mSpinnerItems.getSelectedItem().toString();
            inComeInfo.mAmount = mEtAmount.getText().toString();
            inComeInfo.mRemark = mEtRemark.getText().toString();
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

    // =================================================================

    private class inComeInfo{
        public String mDate;
        public String mItem;
        public String mAmount;
        public String mRemark;

        public inComeInfo(){ }
    }

    // =================================================================

    private class dataAdapter extends RecyclerView.Adapter<dataAdapter.ViewHolder>{
        // https://codeburst.io/android-swipe-menu-with-recyclerview-8f28a235ff28

        List<inComeInfo> mList;
        private Context mContext;

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView textviewItem, textviewAmount, textviewRemark;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.textviewItem = itemView.findViewById(R.id.textView_item);
                this.textviewAmount = itemView.findViewById(R.id.textView_amount);
                this.textviewRemark = itemView.findViewById(R.id.textView_remark);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_income, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull dataAdapter.ViewHolder holder, int position) {
            inComeInfo inComeInfo = this.mList.get(position);
            holder.textviewItem.setText(inComeInfo.mItem);
            holder.textviewAmount.setText(inComeInfo.mAmount);
            holder.textviewRemark.setText(inComeInfo.mRemark);
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

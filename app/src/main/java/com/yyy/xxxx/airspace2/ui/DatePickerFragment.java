package com.yyy.xxxx.airspace2.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.yyy.xxxx.airspace2.app.CONST_ACTIVITY_CODE;
import com.yyy.xxxx.airspace2.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by len on 2017. 1. 4..
 */

public class DatePickerFragment extends DialogFragment implements CONST_ACTIVITY_CODE {

    public interface OnDateListener{
        void onReceivedDate(Date date);
    }

    private OnDateListener mOnDateListener;

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE =
            "com.yyy.xxx.airspace.date";

    private DatePicker mDatePicker;


    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() != null && getActivity() instanceof OnDateListener){
            mOnDateListener = (OnDateListener) getActivity();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        //Calendar도 싱글톤 쓰고있음
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date_picker, null);

        mDatePicker = (DatePicker) view.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year,month,day,null);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                //Fragment로 보내는것
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        mOnDateListener.onReceivedDate(date);
                    }
                })
                .create();
    }

}

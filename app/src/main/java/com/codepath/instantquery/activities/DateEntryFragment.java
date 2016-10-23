package com.codepath.instantquery.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by phoen on 10/22/2016.
 */
public class DateEntryFragment extends DialogFragment {

    static String fragManDateKey = "fragment_date";
    DatePickerDialog.OnDateSetListener onDateSet;

    public DateEntryFragment() {}

    public static DateEntryFragment newInstance (Calendar initDate) {
        DateEntryFragment dateFrag = new DateEntryFragment();
        Bundle args = new Bundle();
        //args.putParcelable(FilterSearchDialogFragment.beginDateKey, Parcels.wrap(initDate));
        args.putSerializable(FilterSearchDialogFragment.beginDateKey, initDate);
        dateFrag.setArguments(args);

        return dateFrag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        Calendar c = (Calendar) getArguments()
                .getSerializable(FilterSearchDialogFragment.beginDateKey);
        if (c == null) {
            c = Calendar.getInstance();
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), onDateSet, year, month, day);

    }

    public void setDateFragCallBack(DatePickerDialog.OnDateSetListener ondate) {
        onDateSet = ondate;
    }
}
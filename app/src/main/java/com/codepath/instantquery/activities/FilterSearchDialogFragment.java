package com.codepath.instantquery.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.codepath.instantquery.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created on 10/22/2016.
 */
public class FilterSearchDialogFragment extends DialogFragment{
    static String beginDateKey = "begindate";
    static String sortOrderKey = "sortorder";
    static String newsDeskKey = "news_desk";
    static String fragManFilterKey = "fragment_filter";

    ImageView ivBeginDate;
    EditText etDate;
    Button btnSave;
    Spinner spSort;
    ArrayAdapter<CharSequence> spAdapter;
    CheckBox cbArts;
    CheckBox cbFS;
    CheckBox cbSports;
    Calendar beginDate;
    String spSelection;
    boolean [] cbnewsDesk;
    public FilterSearchDialogFragment () {

    }

    public static FilterSearchDialogFragment newInstance (Calendar beginDate,
                                                          String sort,
                                                           boolean [] news_desk) {
        FilterSearchDialogFragment filterFrag = new FilterSearchDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(beginDateKey, beginDate);
        args.putString(sortOrderKey, sort);
        args.putBooleanArray(newsDeskKey, news_desk);


        filterFrag.setArguments(args);
        return filterFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //getDialog().setTitle("Filter Your Search");
        View dialogView = inflater.inflate(R.layout.fragment_filter_search, container, false);
        beginDate = (Calendar) getArguments().getSerializable(beginDateKey);
        //beginDate = (Calendar) Parcels.unwrap(getArguments().getParcelable(beginDateKey));
        spSelection = getArguments().getString(sortOrderKey);
        cbnewsDesk = getArguments().getBooleanArray(newsDeskKey);

        ivBeginDate = (ImageView) dialogView.findViewById(R.id.ivdate);
        etDate = (EditText) dialogView.findViewById(R.id.etBeginDate);
        ivBeginDate.setOnClickListener(dateOnclickListener);
        cbArts = (CheckBox) dialogView.findViewById(R.id.checkbox_arts);
        cbFS = (CheckBox) dialogView.findViewById(R.id.checkbox_fashionstyle);
        cbSports = (CheckBox) dialogView.findViewById(R.id.checkbox_sports);
        spSort = (Spinner) dialogView.findViewById(R.id.spSortOrder);
        spAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.sortorder, android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSort.setAdapter(spAdapter);
        btnSave = (Button) dialogView.findViewById(R.id.btnFilterSave);
        btnSave.setOnClickListener(onFilterFragClick);

        if (beginDate != null) {
            etDate.setText(new SimpleDateFormat("MM/dd/yy").format(beginDate.getTime()));
        }
        if (spSelection != null) {
            int position = spAdapter.getPosition(spSelection);
            spSort.setSelection(position);
        }
        if (cbnewsDesk != null) {
            cbArts.setChecked(cbnewsDesk[0]);
            cbFS.setChecked(cbnewsDesk[1]);
            cbSports.setChecked(cbnewsDesk[2]);
        }
        return dialogView;
    }

    private Button.OnClickListener dateOnclickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            DateEntryFragment newDateFragment = DateEntryFragment
                    .newInstance((Calendar) getArguments()
                    .getSerializable(beginDateKey));
            newDateFragment.setDateFragCallBack(newdate);
            newDateFragment.show(getActivity().getSupportFragmentManager(),
                    DateEntryFragment.fragManDateKey);

        }
    };

    DatePickerDialog.OnDateSetListener newdate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet (DatePicker view, int year, int month, int dayOfMonth) {
            // store the values selected into a Calendar instance
            beginDate = Calendar.getInstance();
            beginDate.set(Calendar.YEAR, year);
            beginDate.set(Calendar.MONTH, month);
            beginDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
            etDate.setText(dateFormat.format(beginDate.getTime()));
        }
    };

    private Button.OnClickListener onFilterFragClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            boolean [ ] news_desk = new boolean[3];

            news_desk[0] = cbArts.isChecked();
            news_desk[1] = cbFS.isChecked();
            news_desk[2] = cbSports.isChecked();

            String spSelect = (String) spSort.getSelectedItem();

            FilterFragListener filterListener = (FilterFragListener) getActivity();
            filterListener.onSaveFilter(news_desk, beginDate, spSelect);

            dismiss();
        }
    };

    public interface FilterFragListener {
        void onSaveFilter(boolean [] news_desk, Calendar beginDate, String sort);
    }
}

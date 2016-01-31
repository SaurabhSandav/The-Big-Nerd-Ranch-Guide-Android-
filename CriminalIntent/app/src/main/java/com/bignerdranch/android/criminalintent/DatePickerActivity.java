package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.LinearLayout;

import java.util.Date;

/**
 * Created by saurabh on 21/1/16.
 */
public class DatePickerActivity extends SingleFragmentActivity implements CrimeFragment.Callbacks {

    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";

    public static Intent getIntent(Context context, Date date) {
        Intent intent = new Intent(context, DatePickerActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return DatePickerFragment.newInstance((Date) getIntent().getSerializableExtra(EXTRA_DATE));
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }

    public boolean isTablet() {
        return findViewById(R.id.dialog_container) instanceof LinearLayout;
    }
}

package io.smalldata.beehiveapp.onboarding;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import io.smalldata.beehiveapp.fcm.InAppAnalytics;

/**
 * Created by fnokeke on 1/1/18.
 * TimePicker Dialog
 */

public class SetTimeDialog implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    private EditText mEditText;
    private String mEntryKey;
    private Context mContext;
    private Calendar mCalendar;

    SetTimeDialog(Context context, String entryKey, EditText editText) {
        mContext = context;
        mEntryKey = entryKey;
        mEditText = editText;
        mEditText.setOnClickListener(this);
        mCalendar = Calendar.getInstance();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int mins) {
        String time24Clock = String.format("%s:%s", hourOfDay, mins);
        saveEntry(time24Clock);
        saveEntryKeyAnalytics(time24Clock);

        String userSetTime = to12HourFormat(time24Clock);
        this.mEditText.setText(userSetTime);
    }

    private void saveEntryKeyAnalytics(String time24Clock) {
        switch (mEntryKey) {
            case Constants.KEY_WEEKDAY_WAKE:
                InAppAnalytics.add(mContext, Constants.CHANGED_WEEKDAY_WAKE);
                InAppAnalytics.add(mContext, Constants.VALUE_OF_CHANGED_WEEKDAY_WAKE, time24Clock);
                break;
            case Constants.KEY_WEEKEND_WAKE:
                InAppAnalytics.add(mContext, Constants.CHANGED_WEEKEND_WAKE);
                InAppAnalytics.add(mContext, Constants.VALUE_OF_CHANGED_WEEKEND_WAKE, time24Clock);
                break;
            case Constants.KEY_WEEKDAY_SLEEP:
                InAppAnalytics.add(mContext, Constants.CHANGED_WEEKDAY_SLEEP);
                InAppAnalytics.add(mContext, Constants.VALUE_OF_CHANGED_WEEKDAY_SLEEP, time24Clock);
                break;
            case Constants.KEY_WEEKEND_SLEEP:
                InAppAnalytics.add(mContext, Constants.CHANGED_WEEKEND_SLEEP);
                InAppAnalytics.add(mContext, Constants.VALUE_OF_CHANGED_WEEKEND_SLEEP, time24Clock);
                break;
        }

    }

    private void saveEntry(String entryTimeValue) {
        new Profile(mContext).saveEditTextTimeEntry(mEntryKey, entryTimeValue);
    }

    //    time24clock: hr:mins
    static String to12HourFormat(String time24Clock) {
        String[] timeArr = time24Clock.split(":");
        int hr = Integer.parseInt(timeArr[0]);
        int mins = Integer.parseInt(timeArr[1]);
        return toComplete12HourFormat(hr, mins);
    }

    private static String toComplete12HourFormat(int hourOfDay, int minute) {
        String hr = String.valueOf(hourOfDay);
        String mins = String.valueOf(minute);
        String amPm = "am";

        if (hourOfDay >= 12) {
            hr = String.valueOf(hourOfDay % 12);
            amPm = "pm";
        }

        if (hourOfDay % 12 < 10) {
            hr = "0" + hr;
        }

        if (minute < 10) {
            mins = "0" + String.valueOf(minute);
        }

        return String.format("%s:%s%s", hr, mins, amPm);
    }

    @Override
    public void onClick(View v) {
        int hour = mCalendar.get(Calendar.HOUR);
        int mins = mCalendar.get(Calendar.MINUTE);
        new TimePickerDialog(mContext, this, hour, mins, false).show();
    }
}

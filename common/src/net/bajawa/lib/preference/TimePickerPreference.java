package net.bajawa.lib.preference;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimePickerPreference extends Preference implements OnTimeSetListener {
    
    private static final String DEFAULT_TIME = "12:00";
    
    private String lastValue;
    private int hour = 12;
    private int minute = 0;
    
    private TimePickerDialog dialog;
    private Context context;
    
    public TimePickerPreference(Context context) {
        this(context, null);
    }

    public TimePickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        dialog = new TimePickerDialog(context, this, hour, minute, true);
    }

    @Override
    protected void onClick() {
        super.onClick();
        dialog = new TimePickerDialog(context, this, hour, minute, true);
        dialog.show();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        
        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        
        if (lastValue == null) {
            lastValue = DEFAULT_TIME;
        }

        if (isEnabled()) {
            summaryView.setVisibility(View.VISIBLE);

            if (lastValue != null) {
                summaryView.setText(lastValue);

            } else {
                summaryView.setText(DEFAULT_TIME);
            }

        } else {
            summaryView.setVisibility(View.GONE);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        String value = a.getString(index);
        return value != null ? value : DEFAULT_TIME;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setTime(restorePersistedValue ? getPersistedString(lastValue) : (String) defaultValue);
    }
    
    public void setTime(int hour, int minute) {
        if (hour != this.hour || minute != this.minute) {
            String hourString = hour + "";
            String minuteString = minute + "";
            
            if (hour == 0) {
                hourString = "0" + hour;
            }
            
            if (minute <= 9) {
                minuteString = "0" + minute;
            }
            
            this.hour = hour;
            this.minute = minute;
            this.lastValue = hourString + ":" + minuteString;
            setSummary(lastValue);
            
            if (callChangeListener(lastValue)) {
                persistString(this.lastValue);
                notifyChanged();
            }
        }
    }
    
    public void setTime(String time) {
        if (time != null && !time.trim().equals("")) {
            String[] split = time.split(":");
            if (split.length == 2) {
                setTime(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }
        }
    }
    
    public String getTime() {
        return this.lastValue != null ? this.lastValue : DEFAULT_TIME;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        setTime(hourOfDay, minute);
    }

    @Override
    public void setSummary(CharSequence summary) {
        super.setSummary(summary);
    }
}

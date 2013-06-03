package net.bajawa.lib.preference;

import net.bajawa.lib.R;
import net.bajawa.lib.widget.NumberPicker;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class NumberPickerPreference extends DialogPreference {

    private int defaultValue = 0;
    private int lastValue = defaultValue;
    
    private NumberPicker numberPicker;
    private Context context;
    
    public NumberPickerPreference(Context context) {
        this(context, null);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.colorPickerPreferenceStyle);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        this.context = context;

        setPositiveButtonText(context.getString(R.string.ok));
        setNegativeButtonText(context.getString(R.string.cancel));
    }

    @Override
    protected View onCreateDialogView() {
        numberPicker = new NumberPicker(context);
        numberPicker.setCurrent(lastValue);
        numberPicker.setRange(-100, 100);
        return numberPicker;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);

        if (isEnabled()) {
            summaryView.setText("Current value: " + lastValue);
            summaryView.setVisibility(View.VISIBLE);

        } else {
            summaryView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(numberPicker);
        numberPicker.setCurrent(lastValue);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            if (callChangeListener(numberPicker.getCurrent())) {
                lastValue = numberPicker.getCurrent();
                persistInt(lastValue);
                notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, defaultValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (defaultValue != null) {
            this.defaultValue = (Integer) defaultValue;
        }
        lastValue = restorePersistedValue ? getPersistedInt(lastValue) : this.defaultValue;
    }
    
    public void setCurrent(int currentNumber) {
        if (currentNumber != lastValue) {
            lastValue = currentNumber;
            persistInt(currentNumber);
            notifyChanged();
        }
    }
    
    public int getCurrent() {
        return lastValue;
    }
}

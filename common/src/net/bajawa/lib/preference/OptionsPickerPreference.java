package net.bajawa.lib.preference;

import net.bajawa.lib.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class OptionsPickerPreference extends Preference {

    public static final int OPTION_ENABLE = 0;
    public static final int OPTION_DISABLE = 1;
    public static final int OPTION_NO_CHANGE = 2;
    public static final int DEFAULT_OPTION = OPTION_NO_CHANGE;

    private int defaultValue = DEFAULT_OPTION;
    private int lastValue = defaultValue;
    private Context context;
    private String dialogTitle;
    private String[] options;
    private int enablingIndex = -1;
    private int disablingIndex = -1;

    public OptionsPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.optionsPickerPreferenceStyle);
    }

    public OptionsPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;

        Resources res = context.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OptionsPickerPreference, defStyle, 0);
        options = res.getStringArray(a.getResourceId(R.styleable.OptionsPickerPreference_options, R.array.option_names));
        enablingIndex = a.getInt(R.styleable.OptionsPickerPreference_enablingIndex, -1);
        disablingIndex = a.getInt(R.styleable.OptionsPickerPreference_disablingIndex, -1);
        
        if (enablingIndex != -1 && disablingIndex != -1) {
            throw new IllegalArgumentException("Can only set either enablingIndex OR disablingIndex, not both at the same time!");
        }
        
        a.recycle();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        summaryView.setText(options[lastValue]);
        summaryView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onClick() {
        super.onClick();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setSingleChoiceItems(options, lastValue, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (!callChangeListener(which)) {
                    return;
                }

                setSelectedOption(which);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setTitle(dialogTitle == null ? "Select option" : dialogTitle);
        builder.create().show();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, defaultValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setSelectedOption(restorePersistedValue ? getPersistedInt(lastValue) : (Integer) defaultValue);
    }

    public void setSelectedOption(int newValue) {
        if (lastValue != newValue) {
            this.lastValue = newValue;
            persistInt(newValue);
            notifyDependencyChange(shouldDisableDependents());
            notifyChanged();
        }
    }
    
    public int getSelectedOption() {
        return this.lastValue;
    }

    @Override
    public boolean shouldDisableDependents() {
        boolean shouldDisable = false;
        
        // An index that should enable dependencies is set
        if (enablingIndex != -1) {
            if (lastValue == enablingIndex) {
                shouldDisable = false;
            } else {
                shouldDisable = true;
            }
            
        // An index that should disable dependencies is set
        } else if (disablingIndex != -1) {
            if (lastValue == disablingIndex) {
                shouldDisable = true;
                
            } else {
                shouldDisable = false;
            }
        }

        return shouldDisable || super.shouldDisableDependents();
    }

}

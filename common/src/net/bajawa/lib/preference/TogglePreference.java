package net.bajawa.lib.preference;

import net.bajawa.lib.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class TogglePreference extends Preference {
	
    private int selectedOption = 0;
	private CharSequence[] entries;
	
	public TogglePreference(Context context) {
		this(context, null);
	}

	public TogglePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TogglePreference, defStyle, 0);
		
		entries = a.getTextArray(R.styleable.TogglePreference_entries);
		
		a.recycle();
		
		setWidgetLayoutResource(0);
	}

	public TogglePreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.onOffPreferenceStyle);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		
		// Sync the summary view
		TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        if (summaryView != null) {
            summaryView.setText(entries[selectedOption]);
            summaryView.setVisibility(View.VISIBLE);
        }
	}

	@Override
	protected void onClick() {
		super.onClick();
		
		toggleValue();
	}
	
	private void toggleValue() {
	    if (selectedOption == (entries.length -1) ) {
	        setSelectedOption(0);
	        
	    } else {
	        setSelectedOption(selectedOption + 1);
	    }
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInt(index, 0);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
	    setSelectedOption(restorePersistedValue ? getPersistedInt(selectedOption) : (Integer) defaultValue);
	}
	
	public void setSelectedOption(int selectedOption) {
        if (selectedOption != this.selectedOption) {
            callChangeListener(selectedOption);
            this.selectedOption = selectedOption;
            persistInt(selectedOption);
            notifyChanged();
        }
	}
	
	public int getSelectedOption() {
	    return selectedOption;
	}
}

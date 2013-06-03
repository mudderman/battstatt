package net.bajawa.lib.preference;

import net.bajawa.lib.R;
import net.bajawa.lib.util.DateUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class WeekdaySelectorPreference extends Preference {

    public static final String DEFAULT_VALUES_STRING = "0123456";
    
    private String selectedValuesString;
    private String[] weekdayNames;
    private String[] weekdayAbbrNames;

    public WeekdaySelectorPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekdaySelectorPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        weekdayNames = context.getResources().getStringArray(R.array.weekday_names);
        weekdayAbbrNames = context.getResources().getStringArray(R.array.weekday_abbr_names);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        
        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        
        if (selectedValuesString == null) {
            selectedValuesString = DEFAULT_VALUES_STRING;
        }

        if (isEnabled()) {
            summaryView.setVisibility(View.VISIBLE);
            summaryView.setText(DateUtils.getFormattedSelectedDays(selectedValuesString, false));

        } else {
            summaryView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select weekdays");
        
        final boolean[] selection = stringToArray(selectedValuesString);
        builder.setMultiChoiceItems(weekdayNames, selection, new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selection[which] = isChecked;
            }
        });

        builder.setPositiveButton("Ok", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                
                selectedValuesString = arrayToString(selection);
                
                if (!callChangeListener(selectedValuesString)) {
                    return;
                }
                
                persistString(selectedValuesString);
                notifyChanged();
            }
        });

        builder.setNegativeButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setSelectedDays(restorePersistedValue ? getPersistedString(selectedValuesString) : (String) defaultValue);
    }

    public void setSelectedDays(String days) {
        if (days != null && !days.trim().equals("")) {
            selectedValuesString = days;
            
        } else {
            selectedValuesString = DEFAULT_VALUES_STRING;
        }
        
        persistString(selectedValuesString);
        notifyChanged();
    }
    
    public String getSelectedDays() {
        return selectedValuesString;
    }
    
    public String getFormattedSelectedDays() {
        if (selectedValuesString.equals("0123456") || selectedValuesString.equals("")) {
            return "Every day";
            
        } else if (selectedValuesString.equals("01234")) {
            return "Weekdays";
            
        } else if (selectedValuesString.equals("56")) {
            return "Weekends";
            
        } else {
            StringBuilder builder = new StringBuilder(selectedValuesString.length() * 3);

            for (int i=0; i < selectedValuesString.length(); i++) {
                int index = Integer.parseInt(Character.valueOf(selectedValuesString.charAt(i)).toString());
                builder.append(weekdayAbbrNames[index]).append(", ");
            }

            String string = builder.toString();
            if (string.endsWith(", ")) {
                return string.substring(0, string.length() - 2);
            }

            return string;
        }
    }
    
    public String arrayToString(boolean[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < array.length; i++) {
            if (array[i]) {
                sb.append(i);
            }
        }
        
        return sb.toString();
    }
    
    public boolean[] stringToArray(String string) {
        boolean array[] = { false, false, false, false, false, false, false };
        for (int i=0; i < string.length(); i++) {
            int index = Integer.parseInt(Character.valueOf(string.charAt(i)).toString());
            array[index] = true;
        }
        
        return array;
    }
}

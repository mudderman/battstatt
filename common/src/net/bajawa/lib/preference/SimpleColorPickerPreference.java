package net.bajawa.lib.preference;

import net.bajawa.lib.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SimpleColorPickerPreference extends Preference {
    
    private static final int DEFAULT_COLOR = 0xffffffff;
    
    private String[] colorNames;
    private int[] colorCodes;
    
    private int chosenColor;
    
    private AlertDialog dialog;
    private String dialogTitle;

    public SimpleColorPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.simpleColorPickerPreferenceStyle);
    }

    public SimpleColorPickerPreference(Context context) {
        this(context, null);
    }
    
    public SimpleColorPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        // Get the xml values
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleColorPickerPreference, defStyle, 0);
        Resources res = context.getResources();
        colorNames = res.getStringArray(a.getResourceId(R.styleable.SimpleColorPickerPreference_entries, R.array.color_names));
        colorCodes = res.getIntArray(a.getResourceId(R.styleable.SimpleColorPickerPreference_values, R.array.color_values));
        dialogTitle = a.getString(R.styleable.SimpleColorPickerPreference_dialogTitle);
        a.recycle();
        
        setWidgetLayoutResource(R.layout.simple_color_picker_preference);
        
        // Build the color dialog list
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setAdapter(new SimpleColorListAdapter(context), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int newValue = colorCodes[which];
                if (!callChangeListener(newValue)) {
                    return;
                }
                
                setColor(newValue);
            }
        });
        builder.setTitle(dialogTitle == null ? "Select a color" : dialogTitle);
        dialog = builder.create();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        
//        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        
        View colorBox = view.findViewById(R.id.color);
        View colorFrame = view.findViewById(R.id.colorFrame);
        
        if (colorBox != null) {
            
            if (isEnabled()) {
//                String chosenColorString = view.getContext().getString(R.string.chosen_color_label);
//                
//                if (chosenColor == null) {
//                    colorFrame.setBackgroundColor(Color.BLACK);
//                    colorBox.setBackgroundColor(Color.parseColor(DEFAULT_COLOR));
//                    summaryView.setText(chosenColorString + " " + getColorName(DEFAULT_COLOR));
//                    
//                } else {
                    colorFrame.setBackgroundColor(Color.WHITE);
                    colorBox.setBackgroundColor(chosenColor);
//                    String colorName = getColorName(chosenColor);
//                    if (colorName == null) {
//                        colorName = "None";
//                    }
//                    summaryView.setText(chosenColorString + " " + colorName);
//                }
                
                colorFrame.setVisibility(View.VISIBLE);
                colorBox.setVisibility(View.VISIBLE);
//                summaryView.setVisibility(View.VISIBLE);
                
            } else {
                colorFrame.setVisibility(View.GONE);
                colorBox.setVisibility(View.GONE);
//                summaryView.setVisibility(View.GONE);
            }
        }
    }
    
    @Override
    protected void onClick() {
        super.onClick();
        dialog.show();
    }

//    private String getColorName(String colorCode) {
//        for (int i=0; i < colorCodes.length; i++) {
//            if (colorCodes[i].equals(colorCode)) {
//                return colorNames[i];
//            }
//        }
//        
//        return null;
//    }
    
    public void setColor(int color) {
        if (color != chosenColor) {
            chosenColor = color;
            persistInt(chosenColor);
            notifyChanged();
        }
    }
    
    public int getColor() {
        return this.chosenColor;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_COLOR);
    }
    
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setColor(restorePersistedValue ? getPersistedInt(chosenColor) : (Integer) defaultValue);
    }

    private class SimpleColorListAdapter extends ArrayAdapter<String> {
        private Context context;
        
        public SimpleColorListAdapter(Context context) {
            super(context, 0, colorNames);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.simple_color_list_item, null);
                holder = new ViewHolder();
                holder.colorFrame = convertView.findViewById(R.id.colorFrame);
                holder.color = convertView.findViewById(R.id.color);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
                
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            // Decide if we should show the frame or not
            if (colorCodes[position] == 0xffffffff) {
                holder.colorFrame.setBackgroundColor(Color.BLACK);
                holder.colorFrame.setVisibility(View.VISIBLE);
                
            } else if (colorCodes[position] == 0xff000000) {
                holder.colorFrame.setBackgroundColor(Color.WHITE);
                holder.colorFrame.setVisibility(View.VISIBLE);
                
            } else {
                holder.colorFrame.setVisibility(View.GONE);
            }
            
            holder.color.setBackgroundColor(colorCodes[position]);
            holder.title.setText(colorNames[position]);
            
            return convertView;
        }
        
        class ViewHolder {
            View colorFrame;
            View color;
            TextView title;
        }
    }
}

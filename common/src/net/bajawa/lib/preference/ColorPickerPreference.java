package net.bajawa.lib.preference;

import net.bajawa.lib.R;
import net.bajawa.lib.widget.ColorPicker;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

public class ColorPickerPreference extends Preference {
    
    private int defaultValue = Color.parseColor("#ffffff");
    private int lastColor = defaultValue;
    private ColorPicker colorPicker;
    private boolean displayAlpha;
    private final AlertDialog prefDialog;
    
    private View colorFrame;
    private View colorBox;
    
    public ColorPickerPreference(Context context) {
        this(context, null);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.colorPickerPreferenceStyle);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Get the xml values
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPreference, defStyle, 0);
        displayAlpha = a.getBoolean(R.styleable.ColorPickerPreference_displayAlpha, false);
        a.recycle();

        colorPicker = new ColorPicker(getContext(), displayAlpha);
        colorPicker.setColor(lastColor);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pick a color");
        builder.setView(colorPicker);
        builder.setPositiveButton(context.getString(R.string.ok), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (callChangeListener(colorPicker.getColor())) {
                    lastColor = colorPicker.getColor();
                    persistInt(lastColor);
                    notifyChanged();
                }
            }
        });
        
        builder.setNegativeButton(context.getString(R.string.cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        
        builder.setNeutralButton("Presets", null);
        
        prefDialog = builder.create();
        
        setWidgetLayoutResource(R.layout.simple_color_picker_preference);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        
        colorBox = view.findViewById(R.id.color);
        colorFrame = view.findViewById(R.id.colorFrame);
        
        if (isEnabled()) {
            colorFrame.setVisibility(View.VISIBLE);
            colorBox.setVisibility(View.VISIBLE);
            
            colorBox.setBackgroundColor(Color.argb(0xff, Color.red(lastColor), Color.green(lastColor), Color.blue(lastColor)));

        } else {
            colorFrame.setVisibility(View.GONE);
            colorBox.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        prefDialog.show();
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
        
        lastColor = restorePersistedValue ? getPersistedInt(lastColor) : this.defaultValue;
    }
    
    public int getColor() {
        int alpha;
        if (displayAlpha) {
            alpha = Color.alpha(lastColor);
        } else {
            alpha = 0xff;
        }
        return Color.argb(alpha, Color.red(lastColor), Color.green(lastColor), Color.blue(lastColor));
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        super.setDefaultValue(defaultValue);
        this.defaultValue = (Integer) defaultValue;
        lastColor = (Integer) defaultValue;
        colorPicker.setColor(lastColor);
    }
}

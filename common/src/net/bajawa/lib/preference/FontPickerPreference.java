package net.bajawa.lib.preference;

import java.io.File;
import java.io.FilenameFilter;
import net.bajawa.lib.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FontPickerPreference extends Preference {

    public static final String DEFAULT_FONT = "sans";

    private String[] defaultFontNames = { "Default", "Monospace", "Sans-Serif", "Serif" };
    private String[] defaultFontValues = { "default", "monospace", "sans", "serif" };
    
    private String[] fontNames;
    private String[] fontValues;

    private String chosenFont;

    private AlertDialog dialog;

    private String summaryText = "Chosen font: ";

    public FontPickerPreference(Context context) {
        this(context, null);
    }

    public FontPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.fontPickerPreferenceStyle);
    }

    public FontPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setWidgetLayoutResource(R.layout.font_picker_preference);

        // List the ttfs on sdcard
        File[] customFonts = null;

        File dir = new File("/sdcard/.battstatt");
        if (dir.exists() && dir.canRead()) {
            customFonts = dir.listFiles(new FontFileFilter());
        }

        if (customFonts != null) {
            fontNames = new String[defaultFontNames.length + customFonts.length];
            fontValues = new String[defaultFontValues.length + customFonts.length];

            for (int i = 0; i < fontNames.length; i++) {
                if (i < defaultFontNames.length) {
                    fontNames[i] = defaultFontNames[i];
                    fontValues[i] = defaultFontValues[i];

                } else {
                    int index = i - defaultFontNames.length;
                    fontNames[i] = customFonts[index].getName().substring(0, customFonts[index].getName().indexOf("."));
                    fontValues[i] = customFonts[index].getAbsolutePath();
                }
            }

        } else {
            fontNames = defaultFontNames;
            fontValues = defaultFontValues;
        }

        // Build the list dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setAdapter(new FontPickerListAdapter(context), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String newValue = fontValues[which];
                if (!callChangeListener(newValue)) {
                    return;
                }

                setFont(newValue);
            }
        });

        builder.setTitle("Choose font");
        dialog = builder.create();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView text = (TextView) view.findViewById(R.id.text);
        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);

        if (isEnabled()) {
            text.setVisibility(View.VISIBLE);
            summaryView.setVisibility(View.VISIBLE);

            if (chosenFont != null) {
                
                if (chosenFont.endsWith(".ttf")) {
                    text.setTypeface(Typeface.createFromFile(chosenFont));
                    
                } else {
                    text.setTypeface(Typeface.create(chosenFont, Typeface.NORMAL));
                }
                
                summaryView.setText(summaryText + fontNames[getFontIndex(chosenFont)]);

            } else {
                text.setTypeface(Typeface.create(DEFAULT_FONT, Typeface.NORMAL));
                summaryView.setText(summaryText + fontNames[getFontIndex(DEFAULT_FONT)]);
            }

        } else {
            text.setVisibility(View.GONE);
            summaryView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        dialog.show();
    }

    public void setFont(String font) {
        if (!font.equals(this.chosenFont)) {
            chosenFont = font;
            persistString(chosenFont);
            notifyChanged();
        }
    }

    public String getFont() {
        if (chosenFont != null) {
            return chosenFont;
        }

        return DEFAULT_FONT;
    }

    private int getFontIndex(String font) {
        for (int i = 0; i < fontValues.length; i++) {
            if (fontValues[i].equals(font)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        String value = a.getString(index);
        return value != null ? value : DEFAULT_FONT;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        chosenFont = getPersistedString(DEFAULT_FONT);
    }
    
    @Override
    public void setDefaultValue(Object defaultValue) {
        super.setDefaultValue(defaultValue);
        this.chosenFont = (String) defaultValue;
    }

    private class FontPickerListAdapter extends ArrayAdapter<String> {

        public FontPickerListAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1, android.R.id.text1, fontNames);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            TextView text = (TextView) view.findViewById(android.R.id.text1);
            text.setTextColor(Color.BLACK);
            
            if (position < defaultFontNames.length) {
                text.setTypeface(Typeface.create(fontValues[position], Typeface.NORMAL));
                
            } else {
                text.setTypeface(Typeface.createFromFile(new File(fontValues[position])));
            }

            return view;
        }
    }

    private class FontFileFilter implements FilenameFilter {
        public boolean accept(File dir, String filename) {
            return filename.endsWith(".ttf");
        }
    }
}

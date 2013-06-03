package net.bajawa.batterypro;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;
import net.bajawa.batterypro.provider.BatteryWidgetProvider;
import net.bajawa.lib.preference.FontPickerPreference;
import net.bajawa.lib.preference.NumberPickerPreference;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

public class ConfigurationActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private int widgetId;
    private FontPickerPreference fontPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        startService(new Intent(this, BatteryService.class));

        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        
        // Make sure the activity is using the default locale
        Configuration config = getResources().getConfiguration();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        config.locale = Locale.getDefault();
        getResources().updateConfiguration(config, metrics);

        // Set the pref file to use
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_WRITEABLE);
        getPreferenceManager().setSharedPreferencesName("widget_" + widgetId);
        addPreferencesFromResource(R.xml.configuration_prefs);
        
        fontPref = (FontPickerPreference)findPreference(WidgetConfig.KEY_FONT);
        fontPref.setOnPreferenceChangeListener(this);
        
        // Set default values based on size
        int sizeTens = 0;
        int sizeNumbers = 0;
        int sizeAnd = 0;
        int sizeAction = 0;
        int sizeNumber = 0;
        int offsetLevel = 0;
        int offsetAction = 0;
        int offsetNumber = 0;
        
        WidgetConfig wc = new WidgetConfig(this, widgetId);
        
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        AppWidgetProviderInfo info = manager.getAppWidgetInfo(widgetId);
        switch (info.initialLayout) {
            case R.layout.battery_large:
                sizeTens = WidgetConfig.DEFAULT_SIZE_LARGE_TENS;
                sizeNumbers = WidgetConfig.DEFAULT_SIZE_LARGE_NUMBERS;
                sizeAnd = WidgetConfig.DEFAULT_SIZE_LARGE_AND;
                sizeAction = WidgetConfig.DEFAULT_SIZE_LARGE_ACTION;
                sizeNumber = WidgetConfig.DEFAULT_SIZE_LARGE_NUMBER;
                offsetLevel = WidgetConfig.DEFAULT_OFFSET_LARGE_LEVEL;
                offsetAction = WidgetConfig.DEFAULT_OFFSET_LARGE_ACTION;
                offsetNumber = WidgetConfig.DEFAULT_OFFSET_LARGE_NUMBER;
                break;
                
            case R.layout.battery_medium:
                sizeTens = WidgetConfig.DEFAULT_SIZE_MEDIUM_TENS;
                sizeNumbers = WidgetConfig.DEFAULT_SIZE_MEDIUM_NUMBERS;
                sizeAnd = WidgetConfig.DEFAULT_SIZE_MEDIUM_AND;
                sizeAction = WidgetConfig.DEFAULT_SIZE_MEDIUM_ACTION;
                sizeNumber = WidgetConfig.DEFAULT_SIZE_MEDIUM_NUMBER;
                offsetLevel = WidgetConfig.DEFAULT_OFFSET_MEDIUM_LEVEL;
                offsetAction = WidgetConfig.DEFAULT_OFFSET_MEDIUM_ACTION;
                offsetNumber = WidgetConfig.DEFAULT_OFFSET_MEDIUM_NUMBER;
                break;
                
            case R.layout.battery_small:
                sizeTens = WidgetConfig.DEFAULT_SIZE_SMALL_TENS;
                sizeNumbers = WidgetConfig.DEFAULT_SIZE_SMALL_NUMBERS;
                sizeAnd = WidgetConfig.DEFAULT_SIZE_SMALL_AND;
                sizeAction = WidgetConfig.DEFAULT_SIZE_SMALL_ACTION;
                sizeNumber = WidgetConfig.DEFAULT_SIZE_SMALL_NUMBER;
                offsetLevel = WidgetConfig.DEFAULT_OFFSET_SMALL_LEVEL;
                offsetAction = WidgetConfig.DEFAULT_OFFSET_SMALL_ACTION;
                offsetNumber = WidgetConfig.DEFAULT_OFFSET_SMALL_NUMBER;
                break;
        }
        
        sizeTens = wc.getSizeTens();
        sizeNumbers = wc.getSizeNumbers();
        sizeAnd = wc.getSizeAnd();
        sizeAction = wc.getSizeAction();
        sizeNumber = wc.getSizeNumber();
        offsetLevel = wc.getOffsetLevel();
        offsetAction = wc.getOffsetAction();
        offsetNumber = wc.getOffsetNumber();
        
        ((NumberPickerPreference)findPreference(WidgetConfig.KEY_SIZE_TENS)).setCurrent(sizeTens);
        ((NumberPickerPreference)findPreference(WidgetConfig.KEY_SIZE_NUMBERS)).setCurrent(sizeNumbers);
        ((NumberPickerPreference)findPreference(WidgetConfig.KEY_SIZE_AND)).setCurrent(sizeAnd);
        ((NumberPickerPreference)findPreference(WidgetConfig.KEY_SIZE_ACTION)).setCurrent(sizeAction);
        ((NumberPickerPreference)findPreference(WidgetConfig.KEY_SIZE_NUMBER)).setCurrent(sizeNumber);
        
        ((NumberPickerPreference)findPreference(WidgetConfig.KEY_OFFSET_LEVEL)).setCurrent(offsetLevel);
        ((NumberPickerPreference)findPreference(WidgetConfig.KEY_OFFSET_ACTION)).setCurrent(offsetAction);
        ((NumberPickerPreference)findPreference(WidgetConfig.KEY_OFFSET_NUMBER)).setCurrent(offsetNumber);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            // Update widgets
            sendBroadcast(new Intent(BatteryWidgetProvider.ACTION_UPDATE_WIDGETS));

            setResult(RESULT_OK, resultValue);
        }

        return super.onKeyDown(keyCode, event);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (WidgetConfig.KEY_FONT.equals(preference.getKey())) {
            String fontName = (String) newValue;
            if (fontName.endsWith(".ttf")) {
                fontName = fontName.substring(fontName.lastIndexOf("/") + 1);
                try {
                    BufferedInputStream input = new BufferedInputStream(new FileInputStream(new File((String) newValue)), 1024);
                    byte buffer[] = new byte[1024];
                    int readBytes = 0;
                    
                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(new File(getFilesDir(), widgetId + ".ttf")));
                    
                    while ((readBytes = input.read(buffer)) != -1) {
                        output.write(buffer, 0, readBytes);
                    }
                    
                    output.flush();
                    output.close();
                    input.close();
                    
                } catch (Exception e) {
                    Log.e("Config", "Exception!", e);
                };
            }
        }
        
        return true;
    }

}
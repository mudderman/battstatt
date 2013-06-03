package net.bajawa.battery;

import java.util.Locale;
import net.bajawa.battery.provider.BatteryWidgetProvider;
import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.KeyEvent;

public class ConfigurationActivity extends PreferenceActivity {
	
	private int widgetId;
	private Preference prefPro;
	private static final int DIALOG_PRO = 1;
	
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
        
        // Info for going pro!
        prefPro = findPreference("pro");
        prefPro.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                showDialog(DIALOG_PRO);
                return false;
            }
        });
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_PRO) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Go Pro?");
            builder.setMessage(Html.fromHtml(getString(R.string.pro)));
            
            builder.setPositiveButton("Show me the way!", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=net.bajawa.batterypro"));
                    startActivity(intent);
                }
            });
            
            builder.setNegativeButton("No thank you!", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dismissDialog(DIALOG_PRO);
                }
            });
            AlertDialog dialog = builder.create();
            return dialog;
        }
        return super.onCreateDialog(id);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			// Make sure we pass back the original appWidgetId
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			
			// Always widgets
			sendBroadcast(new Intent(BatteryWidgetProvider.ACTION_UPDATE_WIDGETS));
			
			setResult(RESULT_OK, resultValue);
			finish();
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
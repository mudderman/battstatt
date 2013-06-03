package net.bajawa.battery.provider;

import java.io.File;
import java.util.Locale;

import net.bajawa.battery.BatteryService;
import net.bajawa.battery.R;
import net.bajawa.battery.Utils;
import net.bajawa.battery.WidgetConfig;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

public abstract class BatteryWidgetProvider extends AppWidgetProvider {
	
	public static final String ACTION_UPDATE_WIDGETS = "ACTION_UPDATE_WIDGETS";
	public static final String ACTION_TAP = "WIDGET_TAP";
	
	public static final int SIZE_LARGE = 0;
	public static final int SIZE_MEDIUM = 1;
	public static final int SIZE_SMALL = 2;
	public static final int SIZE_SMALL_NUMBERS = 3;
	
	public static final String NAME_LARGE = "battery_large_";
	public static final String NAME_MEDIUM = "battery_medium_";
	public static final String NAME_SMALL = "battery_small_";
	public static final String NAME_SMALL_NUMBERS = "battery_small_numbers_";
	
	public static final String PACKAGE_SETTINGS = "com.android.settings";
	public static final String ABOUT_STOCK = "com.android.settings.deviceinfo.Status";

	public static final String ABOUT_BATTERY = "com.android.settings.BatteryInfo";
	
	protected int batteryLevel;
	protected int batteryStatus;
    private ComponentName service;
	
	abstract protected RemoteViews buildUpdateView(Context context, RemoteViews baseView, WidgetConfig config);
	
	private RemoteViews createRemoteViewsBase(Context context, WidgetConfig config) {
		// Set the language
		Locale locale;
		if (config.getLocale().equals(WidgetConfig.WIDGET_AUTO_LANGUAGE)) {
			locale = Locale.getDefault();
		} else {
			locale = new Locale(config.getLocale());
		}
		
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		Configuration conf = context.getResources().getConfiguration();
		conf.locale = locale;
		context.getResources().updateConfiguration(conf, dm);
		
		// Init the right lingiri
		
		// Init the layout based on the lingiri
		String layoutName;
		switch (config.getWidgetSize()) {
			case SIZE_LARGE:
				layoutName = NAME_LARGE;
				break;
			case SIZE_MEDIUM:
				layoutName = NAME_MEDIUM;
				break;
			case SIZE_SMALL:
				layoutName = NAME_SMALL;
				break;
			case SIZE_SMALL_NUMBERS:
				layoutName = NAME_SMALL_NUMBERS;
				break;
			default:
				layoutName = NAME_LARGE;
		}
		
		config.setLayout(context.getResources().getIdentifier(layoutName + locale.getLanguage(), "layout", "net.bajawa.battery"));
		RemoteViews views = new RemoteViews(context.getPackageName(), config.getLayout());
		
		// Intent to fire when clicking the widget
		Intent clickIntent = new Intent(ACTION_TAP);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
		views.setOnClickPendingIntent(R.id.widgetContainer, pIntent);
		
		// If the service isn't running
		if (service == null) {
		    service = context.startService(new Intent(context, BatteryService.class));
		}
		
		// Get the battery level and status
		batteryLevel = BatteryService.getBatteryLevel();
		batteryStatus = BatteryService.getBatteryStatus();
		
		// If the level or status is -1 try and start the service, then retry the values
		if (batteryLevel == -1 || batteryStatus == -1) {
		    service = context.startService(new Intent(context, BatteryService.class));
		    
		    SharedPreferences prefs = context.getSharedPreferences("data", Context.MODE_WORLD_WRITEABLE);
		    batteryLevel = prefs.getInt("lastLevel", BatteryService.getBatteryLevel());
		    batteryStatus = prefs.getInt("lastStatus", BatteryService.getBatteryStatus());
		    
		} else {
		    SharedPreferences prefs = context.getSharedPreferences("data", Context.MODE_WORLD_WRITEABLE);
		    batteryLevel = prefs.getInt("lastLevel", BatteryService.getBatteryLevel());
		    batteryStatus = prefs.getInt("lastStatus", BatteryService.getBatteryStatus());
		}
		
		return views;
	}
	
	@Override
    public void onEnabled(Context context) {
	    service = context.startService(new Intent(context, BatteryService.class));
    }

    @Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int i=0; i < appWidgetIds.length; i++) {
		    service = context.startService(new Intent(context, BatteryService.class));
		}
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		for (int i=0; i < appWidgetIds.length; i++) {
			new File(Utils.PATH + "widget_" + appWidgetIds[i] + ".xml").delete();
		}
	}
	
	@Override
	public void onDisabled(Context context) {
		if (context.stopService(new Intent(context, BatteryService.class))) {
		    service = null;
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ACTION_UPDATE_WIDGETS.equals(intent.getAction())) {
			updateWidgets(context);
		}
		super.onReceive(context, intent);
	}

	/**
	 * This will update all the widgets for the subclass
	 * @param context
	 */
	private void updateWidgets(Context context) {
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, getClass()));
        for(int i = 0; i < ids.length; i++) {
        	WidgetConfig config = new WidgetConfig(context, ids[i]);
        	
        	if (!config.configFileExists()) {
        		config = WidgetConfig.createDefaultConfig(context, ids[i]);
            }
        	
        	manager.updateAppWidget(ids[i], buildUpdateView(context, createRemoteViewsBase(context, config), config));
        }
	}
}

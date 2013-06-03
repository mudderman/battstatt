package net.bajawa.batterypro;

import net.bajawa.batterypro.provider.BatteryWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class WidgetClickReceiver extends BroadcastReceiver {

    public static final String PACKAGE_SETTINGS = "com.android.settings";
    public static final String ABOUT_BATTERY = "com.android.settings.BatteryInfo";
    public static final String ABOUT_USAGE = "com.android.settings.fuelgauge.PowerUsageSummary";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Intent configIntent = new Intent();

        // Determine what to open
        if (action.equals(BatteryWidgetProvider.ACTION_INFO)) {
            configIntent.setAction(Intent.ACTION_MAIN);
            configIntent.setComponent(new ComponentName(PACKAGE_SETTINGS, ABOUT_BATTERY));

        } else if (action.equals(BatteryWidgetProvider.ACTION_USAGE)) {
            configIntent.setAction(Intent.ACTION_MAIN);
            configIntent.setComponent(new ComponentName(PACKAGE_SETTINGS, ABOUT_USAGE));

        } else if (action.equals(BatteryWidgetProvider.ACTION_CONFIG)) {
            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            configIntent.setClass(context, ConfigurationActivity.class);
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            configIntent.setData(Uri.parse("" + widgetId));
        }

        configIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(configIntent);

        // Update the widgets
        Intent updateIntent = new Intent(BatteryWidgetProvider.ACTION_UPDATE_WIDGETS);
        context.sendBroadcast(updateIntent);
    }

}

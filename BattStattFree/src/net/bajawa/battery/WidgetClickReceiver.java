package net.bajawa.battery;

import net.bajawa.battery.provider.BatteryWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class WidgetClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent updateIntent = new Intent(BatteryWidgetProvider.ACTION_UPDATE_WIDGETS);
        context.sendBroadcast(updateIntent);
        
        Intent infoIntent = new Intent(Intent.ACTION_MAIN);
        infoIntent.setComponent(new ComponentName(BatteryWidgetProvider.PACKAGE_SETTINGS, BatteryWidgetProvider.ABOUT_BATTERY));
        infoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(infoIntent);
    }

}

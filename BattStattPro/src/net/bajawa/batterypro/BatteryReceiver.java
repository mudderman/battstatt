package net.bajawa.batterypro;

import net.bajawa.batterypro.provider.BatteryWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BatteryReceiver extends BroadcastReceiver {

    private int status = -1;
    private int level = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // If a battery event occured, update level and status fields
        if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {

            int rawLevel = intent.getIntExtra("level", -1);
            int scale = intent.getIntExtra("scale", -1);
            status = intent.getIntExtra("status", -1);

            if (rawLevel >= 0 && scale > 0) {
                level = (rawLevel * 100) / scale;
            }
            
            // Save the level to a shared pref as a "fail safe"
            SharedPreferences prefs = context.getSharedPreferences("data", Context.MODE_WORLD_WRITEABLE);
            prefs.edit().putInt("lastLevel", level).putInt("lastStatus", status).commit();

            Log.d(Utils.TAG, "Battery change: Update status and level fields, new level: " + level);
        }

        // Send widget update broadcast
        context.sendBroadcast(new Intent(BatteryWidgetProvider.ACTION_UPDATE_WIDGETS));
    }

    public int getBatteryLevel() {
        return level;
    }

    public int getBatteryStatus() {
        return status;
    }
}

package net.bajawa.batterypro;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BatteryService extends Service {

    private static boolean running = false;
    private static BatteryReceiver receiver;

    @Override
    public void onStart(Intent intent, int startId) {

        // Only register the receiver if the service wasn't previously running
        if (!running) {
            Log.d(Utils.TAG, "Doing registration of receiver");

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            receiver = new BatteryReceiver();
            registerReceiver(receiver, filter);
            running = true;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(Utils.TAG, "BatteryService is dying!");
        running = false;
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    public static int getBatteryLevel() {
        if (receiver != null) {
            return receiver.getBatteryLevel();
        }
        
        return -1;
    }
    
    public static int getBatteryStatus() {
        if (receiver != null) {
            return receiver.getBatteryStatus();
        }
        
        return -1;
    }
}

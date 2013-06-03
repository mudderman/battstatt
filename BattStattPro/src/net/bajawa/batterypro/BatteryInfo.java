package net.bajawa.batterypro;

import android.os.BatteryManager;

public class BatteryInfo {

    private int batteryLevel;
    private int ten;
    private int number;
    private int status;
    
    public BatteryInfo() {
        setBatteryLevel(BatteryService.getBatteryLevel());
        this.status = BatteryService.getBatteryStatus();
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
        ten = batteryLevel / 10;
        number = batteryLevel % 10;
    }

    public boolean isFull() {
        return batteryLevel == 100;
    }

    public boolean isEmpty() {
        return batteryLevel == 0;
    }

    public int getTen() {
        return ten;
    }

    public int getNumber() {
        return number;
    }

    public boolean isNumber() {
        return batteryLevel < 20;
    }

    public boolean isEvenTen() {
        return batteryLevel % 10 == 0;
    }

    public boolean isCharging() {
        return status == BatteryManager.BATTERY_STATUS_CHARGING;
    }
}
package net.bajawa.batterypro;

import android.content.Context;

public class Language {
    
    private WidgetConfig config;

    private String[] numbers;

    private String loading;
    private String charging;
    private String percent;
    
    private boolean tensFirst;
    
    private BatteryInfo info;
    
    public Language(Context context, WidgetConfig config) {
        this.config = config;
        numbers = context.getResources().getStringArray(R.array.numbers);
        loading = context.getString(R.string.loading);
        charging = context.getString(R.string.charging);
        percent = context.getString(R.string.percent);
        tensFirst = context.getResources().getBoolean(R.bool.tensFirst);
        info = new BatteryInfo();
    }

    public String[] getStrings() {
        if (info.getBatteryLevel() == -1) {
            return new String[] { loading };
        }
        
        if (config.isShowNumbers()) {
            return new String[] { String.valueOf(info.getBatteryLevel()) };
        }
        
        return numbers[info.getBatteryLevel()].split("\\|");
    }
    
    public String getString(int index) {
        if (index < getStrings().length) {
            return getStrings()[index];
        }
        
        throw new IllegalArgumentException("Index " + index + " does not exist!");
    }
    
    public String getActionString() {
        // charging and not full or empty
        if (info.isCharging() && config.isShowCharingString() && (!info.isFull() || !info.isEmpty())) {
            return charging;
            
        // not charging
        } else {
            if (config.isShowPercentageString() && (!info.isFull() && !info.isEmpty())) {
                return percent;
            }
        }
        
        return null;
    }

    public boolean isTensFirst() {
        return tensFirst;
    }
}

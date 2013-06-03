package net.bajawa.batterypro;

import java.io.File;
import net.bajawa.batterypro.provider.BatteryWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.SharedPreferences;

public class WidgetConfig {

    public static final String WIDGET_AUTO_LANGUAGE = "auto";

//    public static final String ACTION_CONFIGURE = "config";
//    public static final String ACTION_BATTERY_INFO = "info";
//    public static final String ACTION_BATTERY_USAGE = "usage";
//    public static final String ACTION_BATTERY_NOTHING = "";

    public static final String RULE_ALL = "all";
    public static final String RULE_FIRST = "first";
    public static final String RULE_MIDDLE = "and";
    public static final String RULE_TENS = "tens";
    public static final String RULE_TENSNUMBERS = "tensandnumbers";
    public static final String RULE_NONE = "none";
    public static final String RULE_FIRSTLETTER = "firstletter";
    public static final String RULE_FIRSTLETTERWORD = "firstletterword";
    public static final String RULE_FIRSTLETTERTENS = "firstlettertens";

    public static final String KEY_BACKGROUND_ENABLED = "widget_background_enabled";
    public static final String KEY_BACKGROUND_COLOR = "widget_background_color";
    public static final String KEY_SHOW_PERCENTAGE = "widget_display_percent_string";
    public static final String KEY_SHOW_CHARGING = "widget_display_charging_string";
    public static final String KEY_LOCALE = "widget_locale";
    public static final String KEY_WIDGET_ACTION = "widget_action";
    public static final String KEY_SHOW_NUMBERS = "widget_display_numbers";
    public static final String KEY_FONT = "widget_text_font";
    public static final String KEY_TEXT_SHADOW_ENABLED = "widget_text_shadow";
    public static final String KEY_CAPITALIZE_RULE = "widget_capitalize_rule";
    public static final String KEY_CAPITALIZE_ACTION = "widget_capitalize_action";
    public static final String KEY_BOLDIFY_RULE = "widget_boldify_rule";
    public static final String KEY_BOLDIFY_ACTION = "widget_boldify_action";
    public static final String KEY_COLOR_TENS = "widget_color_tens";
    public static final String KEY_COLOR_NUMBERS = "widget_color_numbers";
    public static final String KEY_COLOR_AND = "widget_color_and";
    public static final String KEY_COLOR_ACTION = "widget_color_action";
    public static final String KEY_COLOR_NUMBER = "widget_color_number";
    public static final String KEY_SIZE_TENS = "widget_size_tens";
    public static final String KEY_SIZE_NUMBERS = "widget_size_numbers";
    public static final String KEY_SIZE_AND = "widget_size_and";
    public static final String KEY_SIZE_ACTION = "widget_size_action";
    public static final String KEY_SIZE_NUMBER = "widget_size_number";
    public static final String KEY_OFFSET_LEVEL = "widget_offset_level";
    public static final String KEY_OFFSET_ACTION = "widget_offset_action";
    public static final String KEY_OFFSET_NUMBER = "widget_offset_number";

    public static final boolean DEFAULT_BACKGROUND_ENABLED = true;
    public static final int DEFAULT_BACKGROUND_COLOR = 0x7f000000;
    public static final int DEFAULT_TEXT_COLOR = 0xffffffff;
    public static final boolean DEFAULT_SHOW_CHARGING = true;
    public static final boolean DEFAULT_SHOW_PERCENTAGE = true;
    public static final boolean DEFAULT_SHOW_NUMBERS = false;
    public static final String DEFAULT_FONT = "Sans";
    public static final boolean DEFAULT_SHADOW_ENABLED = true;
    public static final String DEFAULT_CAPITALIZE_RULE = RULE_ALL;
    public static final boolean DEFAULT_CAPITALIZE_ACTION = true;
    public static final String DEFAULT_BOLDIFY_RULE = RULE_ALL;
    public static final boolean DEFAULT_BOLDIFY_ACTION = true;
    public static final int DEFAULT_COLOR_TENS = 0xffffffff;
    public static final int DEFAULT_COLOR_NUMBERS = 0xffffffff;
    public static final int DEFAULT_COLOR_AND = 0xffffffff;
    public static final int DEFAULT_COLOR_ACTION = 0xffffffff;
    public static final int DEFAULT_COLOR_NUMBER = 0xffffffff;
    
    public static final int DEFAULT_SIZE_LARGE_TENS = 26;
    public static final int DEFAULT_SIZE_LARGE_NUMBERS = 24;
    public static final int DEFAULT_SIZE_LARGE_AND = 22;
    public static final int DEFAULT_SIZE_LARGE_ACTION = 12;
    public static final int DEFAULT_SIZE_LARGE_NUMBER = 40;
    public static final int DEFAULT_OFFSET_LARGE_LEVEL = -5;
    public static final int DEFAULT_OFFSET_LARGE_ACTION = 0;
    public static final int DEFAULT_OFFSET_LARGE_NUMBER = 0;
    
    public static final int DEFAULT_SIZE_MEDIUM_TENS = 20;
    public static final int DEFAULT_SIZE_MEDIUM_NUMBERS = 18;
    public static final int DEFAULT_SIZE_MEDIUM_AND = 14;
    public static final int DEFAULT_SIZE_MEDIUM_ACTION = 12;
    public static final int DEFAULT_SIZE_MEDIUM_NUMBER = 40;
    public static final int DEFAULT_OFFSET_MEDIUM_LEVEL = -5;
    public static final int DEFAULT_OFFSET_MEDIUM_ACTION = 3;
    public static final int DEFAULT_OFFSET_MEDIUM_NUMBER = 5;
    
    public static final int DEFAULT_SIZE_SMALL_TENS = 20;
    public static final int DEFAULT_SIZE_SMALL_NUMBERS = 18;
    public static final int DEFAULT_SIZE_SMALL_AND = 14;
    public static final int DEFAULT_SIZE_SMALL_ACTION = 12;
    public static final int DEFAULT_SIZE_SMALL_NUMBER = 40;
    public static final int DEFAULT_OFFSET_SMALL_LEVEL = -5;
    public static final int DEFAULT_OFFSET_SMALL_ACTION = 3;
    public static final int DEFAULT_OFFSET_SMALL_NUMBER = 0;
    
    private static final String DEFAULT_WIDGET_ACTION = BatteryWidgetProvider.ACTION_CONFIG;

    private int widgetId;
    private int widgetSize;

    private boolean backgroundEnabled;
    private int backgroundColor;
    private boolean showCharingString;
    private boolean showPercentageString;
    private String localeString;
    private String action;
    private boolean showNumbers;
    private String font;
    private boolean showShadow;
    private String capitalizeRule;
    private String boldifyRule;
    private boolean capitalizeAction;
    private boolean boldifyAction;
    private int colorTens;
    private int colorNumbers;
    private int colorAnd;
    private int colorAction;
    private int colorNumber;
    private int sizeTens;
    private int sizeNumbers;
    private int sizeAnd;
    private int sizeAction;
    private int sizeNumber;
    private int offsetLevel;
    private int offsetAction;
    private int offsetNumber;

    private int layout;

    private Context context;

    public WidgetConfig(Context context, int widgetId) {
        this.context = context;
        this.widgetId = widgetId;

        // Init fields from prefs if configuring widget or default if adding
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        AppWidgetProviderInfo info = manager.getAppWidgetInfo(widgetId);

        if (info != null) {

            // Set the size of the widget
            this.widgetSize = Utils.getWidgetSize(info.initialLayout);

            SharedPreferences prefs = context.getSharedPreferences(getPrefsName(), Context.MODE_WORLD_WRITEABLE);
            this.backgroundEnabled = prefs.getBoolean(KEY_BACKGROUND_ENABLED, DEFAULT_BACKGROUND_ENABLED);
            this.backgroundColor = prefs.getInt(KEY_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
            this.showPercentageString = prefs.getBoolean(KEY_SHOW_PERCENTAGE, DEFAULT_SHOW_PERCENTAGE);
            this.showCharingString = prefs.getBoolean(KEY_SHOW_CHARGING, DEFAULT_SHOW_CHARGING);
            this.localeString = prefs.getString(KEY_LOCALE, WIDGET_AUTO_LANGUAGE);
            this.action = prefs.getString(KEY_WIDGET_ACTION, DEFAULT_WIDGET_ACTION);
            this.showNumbers = prefs.getBoolean(KEY_SHOW_NUMBERS, DEFAULT_SHOW_NUMBERS);
            this.font = prefs.getString(KEY_FONT, DEFAULT_FONT);
            this.showShadow = prefs.getBoolean(KEY_TEXT_SHADOW_ENABLED, DEFAULT_SHADOW_ENABLED);
            this.capitalizeRule = prefs.getString(KEY_CAPITALIZE_RULE, DEFAULT_CAPITALIZE_RULE);
            this.boldifyRule = prefs.getString(KEY_BOLDIFY_RULE, DEFAULT_BOLDIFY_RULE);
            this.capitalizeAction = prefs.getBoolean(KEY_CAPITALIZE_ACTION, DEFAULT_CAPITALIZE_ACTION);
            this.boldifyAction = prefs.getBoolean(KEY_BOLDIFY_ACTION, DEFAULT_BOLDIFY_ACTION);
            this.colorAction = prefs.getInt(KEY_COLOR_ACTION, DEFAULT_COLOR_ACTION);
            this.colorAnd = prefs.getInt(KEY_COLOR_AND, DEFAULT_COLOR_AND);
            this.colorTens = prefs.getInt(KEY_COLOR_TENS, DEFAULT_COLOR_TENS);
            this.colorNumbers = prefs.getInt(KEY_COLOR_NUMBERS, DEFAULT_COLOR_NUMBERS);
            this.colorNumber = prefs.getInt(KEY_COLOR_NUMBER, DEFAULT_COLOR_NUMBER);
            
            switch (widgetSize) {
                case BatteryWidgetProvider.SIZE_LARGE:
                    this.sizeTens = prefs.getInt(KEY_SIZE_TENS, 26);
                    this.sizeAction = prefs.getInt(KEY_SIZE_ACTION, 12);
                    this.sizeAnd = prefs.getInt(KEY_SIZE_AND, 22);
                    this.sizeNumbers = prefs.getInt(KEY_SIZE_NUMBERS, 24);
                    this.sizeNumber = prefs.getInt(KEY_SIZE_NUMBER, 40);
                    
                    this.offsetAction = prefs.getInt(KEY_OFFSET_ACTION, 0);
                    this.offsetLevel = prefs.getInt(KEY_OFFSET_LEVEL, -5);
                    this.offsetNumber = prefs.getInt(KEY_OFFSET_NUMBER, 0);
                    
                    break;
                    
                case BatteryWidgetProvider.SIZE_MEDIUM:
                    this.sizeTens = prefs.getInt(KEY_SIZE_TENS, 22);
                    this.sizeAction = prefs.getInt(KEY_SIZE_ACTION, 12);
                    this.sizeAnd = prefs.getInt(KEY_SIZE_AND, 16);
                    this.sizeNumbers = prefs.getInt(KEY_SIZE_NUMBERS, 20);
                    this.sizeNumber = prefs.getInt(KEY_SIZE_NUMBER, 40);
                    
                    this.offsetAction = prefs.getInt(KEY_OFFSET_ACTION, 3);
                    this.offsetLevel = prefs.getInt(KEY_OFFSET_LEVEL, -5);
                    this.offsetNumber = prefs.getInt(KEY_OFFSET_NUMBER, 0);
                    
                    break;
                    
                case BatteryWidgetProvider.SIZE_SMALL:
                    this.sizeTens = prefs.getInt(KEY_SIZE_TENS, 20);
                    this.sizeAction = prefs.getInt(KEY_SIZE_ACTION, 12);
                    this.sizeAnd = prefs.getInt(KEY_SIZE_AND, 14);
                    this.sizeNumbers = prefs.getInt(KEY_SIZE_NUMBERS, 18);
                    this.sizeNumber = prefs.getInt(KEY_SIZE_NUMBER, 40);
                    
                    this.offsetAction = prefs.getInt(KEY_OFFSET_ACTION, 3);
                    this.offsetLevel = prefs.getInt(KEY_OFFSET_LEVEL, -5);
                    this.offsetNumber = prefs.getInt(KEY_OFFSET_NUMBER, 0);
                    
                    break;
            }

        } else {

            this.widgetSize = BatteryWidgetProvider.SIZE_LARGE;
            this.backgroundEnabled = DEFAULT_BACKGROUND_ENABLED;
            this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
            this.showPercentageString = DEFAULT_SHOW_PERCENTAGE;
            this.showCharingString = DEFAULT_SHOW_CHARGING;
            this.localeString = WIDGET_AUTO_LANGUAGE;
            this.action = DEFAULT_WIDGET_ACTION;
            this.showNumbers = DEFAULT_SHOW_NUMBERS;
            this.font = DEFAULT_FONT;
            this.showShadow = DEFAULT_SHADOW_ENABLED;
            this.capitalizeRule = DEFAULT_CAPITALIZE_RULE;
            this.boldifyRule = DEFAULT_BOLDIFY_RULE;
            this.capitalizeAction = DEFAULT_CAPITALIZE_ACTION;
            this.boldifyAction = DEFAULT_BOLDIFY_ACTION;
            this.colorAction = DEFAULT_COLOR_ACTION;
            this.colorAnd = DEFAULT_COLOR_AND;
            this.colorTens = DEFAULT_COLOR_TENS;
            this.colorNumbers = DEFAULT_COLOR_NUMBERS;
            this.colorNumber = DEFAULT_COLOR_NUMBER;
        }
    }

    public boolean isBackgroundEnabled() {
        return backgroundEnabled;
    }

    public void setBackgroundEnabled(boolean backgroundEnabled) {
        this.backgroundEnabled = backgroundEnabled;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public int getWidgetSize() {
        return widgetSize;
    }

    public void setWidgetSize(int widgetSize) {
        this.widgetSize = widgetSize;
    }

    public int getLayout() {
        if (layout != 0) {
            return layout;
        }

        switch (widgetSize) {
            case BatteryWidgetProvider.SIZE_LARGE:
                return R.layout.battery_large;

            case BatteryWidgetProvider.SIZE_MEDIUM:
                return R.layout.battery_medium;

            case BatteryWidgetProvider.SIZE_SMALL:
                return R.layout.battery_small;

            default:
                return R.layout.battery_large;
        }
    }

    public boolean isShowPercentageString() {
        return showPercentageString;
    }

    public void setShowPercentageString(boolean showPercentageString) {
        this.showPercentageString = showPercentageString;
    }

    public String getLocale() {
        return localeString;
    }

    public void setLocale(String locale) {
        this.localeString = locale;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isShowNumbers() {
        return showNumbers;
    }

    public void setShowNumbers(boolean showNumbers) {
        this.showNumbers = showNumbers;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public boolean isShowShadow() {
        return showShadow;
    }

    public void setShowShadow(boolean showShadow) {
        this.showShadow = showShadow;
    }

    public String getCapitalizeRule() {
        return capitalizeRule;
    }

    public void setCapitalizeRule(String capitalizeRule) {
        this.capitalizeRule = capitalizeRule;
    }

    public String getBoldifyRule() {
        return boldifyRule;
    }

    public void setBoldifyRule(String boldifyRule) {
        this.boldifyRule = boldifyRule;
    }

    public boolean isCapitalizeAction() {
        return capitalizeAction;
    }

    public void setCapitalizeAction(boolean capitalizeAction) {
        this.capitalizeAction = capitalizeAction;
    }

    public boolean isBoldifyAction() {
        return boldifyAction;
    }

    public void setBoldifyAction(boolean boldifyAction) {
        this.boldifyAction = boldifyAction;
    }

    public int getSizeTensDpi() {
        return Utils.dipToPixel(context, sizeTens);
    }

    public void setSizeTens(int sizeTens) {
        this.sizeTens = sizeTens;
    }

    public int getSizeNumbersDpi() {
        return Utils.dipToPixel(context, sizeNumbers);
    }

    public void setSizeNumbers(int sizeNumbers) {
        this.sizeNumbers = sizeNumbers;
    }

    public int getSizeAndDpi() {
        return Utils.dipToPixel(context, sizeAnd);
    }

    public void setSizeAnd(int sizeAnd) {
        this.sizeAnd = sizeAnd;
    }

    public int getSizeActionDpi() {
        return Utils.dipToPixel(context, sizeAction);
    }

    public void setSizeAction(int sizeAction) {
        this.sizeAction = sizeAction;
    }

    public int getSizeNumberDpi() {
        return Utils.dipToPixel(context, sizeNumber);
    }

    public void setSizeNumber(int sizeNumber) {
        this.sizeNumber = sizeNumber;
    }
    
    public int getSizeTens() {
        return sizeTens;
    }

    public int getSizeNumbers() {
        return sizeNumbers;
    }

    public int getSizeAnd() {
        return sizeAnd;
    }

    public int getSizeAction() {
        return sizeAction;
    }

    public int getSizeNumber() {
        return sizeNumber;
    }

    public int getOffsetLevel() {
        return offsetLevel;
    }

    public void setOffsetLevel(int offsetLevel) {
        this.offsetLevel = offsetLevel;
    }

    public int getOffsetAction() {
        return offsetAction;
    }

    public void setOffsetAction(int offsetAction) {
        this.offsetAction = offsetAction;
    }

    public int getOffsetNumber() {
        return offsetNumber;
    }

    public void setOffsetNumber(int offsetNumber) {
        this.offsetNumber = offsetNumber;
    }

    public int getColorTens() {
        return colorTens;
    }

    public void setColorTens(int colorTens) {
        this.colorTens = colorTens;
    }

    public int getColorNumbers() {
        return colorNumbers;
    }

    public void setColorNumbers(int colorNumbers) {
        this.colorNumbers = colorNumbers;
    }

    public int getColorAnd() {
        return colorAnd;
    }

    public void setColorAnd(int colorAnd) {
        this.colorAnd = colorAnd;
    }

    public int getColorAction() {
        return colorAction;
    }

    public void setColorAction(int colorAction) {
        this.colorAction = colorAction;
    }

    public int getColorNumber() {
        return colorNumber;
    }

    public void setColorNumber(int colorNumber) {
        this.colorNumber = colorNumber;
    }
    
    public boolean isShowCharingString() {
        return showCharingString;
    }

    public void setShowCharingString(boolean showCharingString) {
        this.showCharingString = showCharingString;
    }

    public String getFilename() {
        return getPrefsName() + ".xml";
    }

    public String getPrefsName() {
        return "widget_" + widgetId;
    }

    public boolean configFileExists() {
        return new File(Utils.PATH, getFilename()).exists();
    }
    
    public static WidgetConfig createDefaultConfig(Context vContext, int vWidgetId) {
        return new WidgetConfig(vContext, vWidgetId);
    }
}
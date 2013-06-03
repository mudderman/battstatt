package net.bajawa.battery;

import java.io.File;
import net.bajawa.battery.provider.BatteryWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.SharedPreferences;

public class WidgetConfig {
	
	public static final String WIDGET_AUTO_LANGUAGE = "auto";

	public static final String KEY_BACKGROUND_ENABLED = "widget_background_enabled";
	public static final String KEY_BACKGROUND_COLOR = "widget_background_color";
	public static final String KEY_TEXT_COLOR = "widget_text_color";
	public static final String KEY_SHOW_PERCENTAGE = "widget_display_percent_string";
	public static final String KEY_LOCALE = "widget_locale";
	public static final String KEY_ENABLE_GRAPH = "widget_enable_graph";

	public static final boolean DEFAULT_BACKGROUND_ENABLED = true;
	public static final String DEFAULT_BACKGROUND_COLOR = "#000000";
	public static final String DEFAULT_TEXT_COLOR = "#ffffff";
	public static final boolean DEFAULT_SHOW_PERCENTAGE = true;

	private int widgetId;
	private int widgetSize;
	
	private boolean backgroundEnabled;
	private String backgroundColor;
	private String textColor;
	private boolean showPercentageString;
	private String localeString;
	
	private int layout;

	public WidgetConfig(Context context, int widgetId) {
		this.widgetId = widgetId;

		// Init fields from prefs if configuring widget or default if adding
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		AppWidgetProviderInfo info = manager.getAppWidgetInfo(widgetId);

		if (info != null) {
			
			// Set the size of the widget
			this.widgetSize = Utils.getWidgetSize(context, info.initialLayout);

			SharedPreferences prefs = context.getSharedPreferences(getPrefsName(), Context.MODE_WORLD_WRITEABLE);
			this.backgroundEnabled = prefs.getBoolean(KEY_BACKGROUND_ENABLED, DEFAULT_BACKGROUND_ENABLED);
			this.backgroundColor = prefs.getString(KEY_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
			this.textColor = prefs.getString(KEY_TEXT_COLOR, DEFAULT_TEXT_COLOR);
			this.showPercentageString = prefs.getBoolean(KEY_SHOW_PERCENTAGE, DEFAULT_SHOW_PERCENTAGE);
			this.localeString = prefs.getString(KEY_LOCALE, WIDGET_AUTO_LANGUAGE);

		} else {
			
			this.widgetSize = BatteryWidgetProvider.SIZE_LARGE;
			this.backgroundEnabled = DEFAULT_BACKGROUND_ENABLED;
			this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
			this.textColor = DEFAULT_TEXT_COLOR;
			this.showPercentageString = DEFAULT_SHOW_PERCENTAGE;
			this.localeString = WIDGET_AUTO_LANGUAGE;
		}
	}
	
	public boolean isBackgroundEnabled() {
		return backgroundEnabled;
	}

	public void setBackgroundEnabled(boolean backgroundEnabled) {
		this.backgroundEnabled = backgroundEnabled;
	}

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
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

			case BatteryWidgetProvider.SIZE_SMALL_NUMBERS:
				return R.layout.battery_small_number;

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

	public String getFilename() {
		return getPrefsName() + ".xml";
	}

	public String getPrefsName() {
		return "widget_" + widgetId;
	}

	public boolean configFileExists() {
		return new File(Utils.PATH + getFilename()).exists();
	}
	
	public static WidgetConfig createDefaultConfig(Context vContext, int vWidgetId) {
        return new WidgetConfig(vContext, vWidgetId);
    }
}
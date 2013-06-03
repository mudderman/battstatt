package net.bajawa.battery.provider;

import net.bajawa.battery.BatteryInfo;
import net.bajawa.battery.R;
import net.bajawa.battery.Utils;
import net.bajawa.battery.WidgetConfig;
import android.content.Context;
import android.graphics.Color;
import android.os.BatteryManager;
import android.view.View;
import android.widget.RemoteViews;

public class WidgetProviderSmallNumber extends BatteryWidgetProvider {

	@Override
	protected RemoteViews buildUpdateView(Context context, RemoteViews baseView, WidgetConfig config) {
		// Get the text to display
		if (batteryLevel == -1) {
			baseView.setTextViewText(R.id.widget_text, "0");

		} else {
			BatteryInfo info = new BatteryInfo(context, batteryLevel);

			baseView.setTextViewText(R.id.widget_text, "" + info.getBatteryLevel());

			// Check if we should show the charging text
			switch (batteryStatus) {

				// Always show the "charging" text if charging
				case BatteryManager.BATTERY_STATUS_CHARGING:
					baseView.setTextViewText(R.id.widget_action, context.getString(R.string.charging));
					baseView.setViewVisibility(R.id.widget_action, View.VISIBLE);
					break;

				// When not charging, check if we should display "percent" text
				default:
					if (config.isShowPercentageString()) {
						baseView.setTextViewText(R.id.widget_action, context.getString(R.string.percent));
						baseView.setViewVisibility(R.id.widget_action, View.VISIBLE);

					} else {
						baseView.setViewVisibility(R.id.widget_action, View.GONE);
					}

					break;
			}
		}

		// Set the text color
		baseView.setTextColor(R.id.widget_text, Color.parseColor(config.getTextColor()));
		baseView.setTextColor(R.id.widget_action, Color.parseColor(config.getTextColor()));

		// Transparent background
		baseView.setImageViewBitmap(R.id.widgetBackground, Utils.createBackground(config, SIZE_SMALL_NUMBERS));

		return baseView;
	}
}

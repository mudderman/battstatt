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

public class WidgetProviderMedium extends BatteryWidgetProvider {

    @Override
    protected RemoteViews buildUpdateView(Context context, RemoteViews baseView, WidgetConfig config) {
        // Get the text to display
        if (batteryLevel == -1) {
            baseView.setTextViewText(R.id.widget_text, context.getString(R.string.loading));

        } else {
            BatteryInfo info = new BatteryInfo(context, batteryLevel);

            // Fully charged
            if (info.isFull()) {

                baseView.setViewVisibility(R.id.tenBeforeNumber, View.VISIBLE);
                baseView.setViewVisibility(R.id.widget_text, View.VISIBLE);
                baseView.setViewVisibility(R.id.widget_text2, View.VISIBLE);
                baseView.setViewVisibility(R.id.widget_text3, View.GONE);

                baseView.setTextViewText(R.id.widget_text, info.getString1());
                baseView.setTextViewText(R.id.widget_text2, info.getString2());

                // Empty battery
            } else if (info.isEmpty()) {
                baseView.setViewVisibility(R.id.tenBeforeNumber, View.VISIBLE);
                baseView.setViewVisibility(R.id.widget_text, View.GONE);
                baseView.setViewVisibility(R.id.widget_text2, View.VISIBLE);
                baseView.setViewVisibility(R.id.widget_text3, View.GONE);

                baseView.setTextViewText(R.id.widget_text2, info.getString1());

            } else {
                // level < 20
                if (info.isNumber()) {
                    // Numbers display before tens doesn't matter if only one
                    // string showing
                    baseView.setViewVisibility(R.id.tenBeforeNumber, View.VISIBLE);
                    baseView.setViewVisibility(R.id.widget_text, View.GONE);
                    baseView.setViewVisibility(R.id.widget_text2, View.VISIBLE);
                    baseView.setViewVisibility(R.id.widget_text3, View.GONE);

                    baseView.setTextViewText(R.id.widget_text2, info.getNumber());

                    // Level > 19
                } else {

                    // Even ten > 20 (e.g. 20, 30, 40 etc.)
                    if (info.isEvenTen()) {
                        baseView.setViewVisibility(R.id.tenBeforeNumber, View.VISIBLE);
                        baseView.setViewVisibility(R.id.widget_text, View.VISIBLE);
                        baseView.setViewVisibility(R.id.widget_text2, View.GONE);
                        baseView.setViewVisibility(R.id.widget_text3, View.GONE);

                        baseView.setTextViewText(R.id.widget_text, info.getTen());
                        
                    } else {
                        baseView.setViewVisibility(R.id.tenBeforeNumber, View.VISIBLE);
                        baseView.setViewVisibility(R.id.widget_text, View.VISIBLE);
                        baseView.setViewVisibility(R.id.widget_text2, View.VISIBLE);
                        baseView.setViewVisibility(R.id.widget_text3, View.GONE);

                        baseView.setTextViewText(R.id.widget_text, info.getTen());
                        baseView.setTextViewText(R.id.widget_text2, info.getNumber());
                    }
                }
            }

            // Check if we should show the charging text
            switch (batteryStatus) {

                // Always show the "charging" text if charging
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    baseView.setTextViewText(R.id.widget_action, context.getString(R.string.charging));
                    baseView.setViewVisibility(R.id.widget_action, View.VISIBLE);
                    break;

                // When not charging, check if we should display "percent" text
                default:
                    if (!info.isFull()) {
                        if (config.isShowPercentageString()) {
                            baseView.setTextViewText(R.id.widget_action, context.getString(R.string.percent));
                            baseView.setViewVisibility(R.id.widget_action, View.VISIBLE);

                        } else {
                            baseView.setViewVisibility(R.id.widget_action, View.GONE);
                        }

                        // Do not show the "percent" text if the battery is full
                    } else {
                        baseView.setViewVisibility(R.id.widget_action, View.GONE);
                    }
                    break;
            }
        }

        // Set the text color
        baseView.setTextColor(R.id.widget_text, Color.parseColor(config.getTextColor()));
        baseView.setTextColor(R.id.widget_text2, Color.parseColor(config.getTextColor()));
        baseView.setTextColor(R.id.widget_text3, Color.parseColor(config.getTextColor()));
        // baseView.setTextColor(R.id.widget_text4,
        // Color.parseColor(config.getTextColor()));
        // baseView.setTextColor(R.id.widget_text5,
        // Color.parseColor(config.getTextColor()));
        // baseView.setTextColor(R.id.widget_text6,
        // Color.parseColor(config.getTextColor()));
        baseView.setTextColor(R.id.widget_action, Color.parseColor(config.getTextColor()));

        // Transparent background
        baseView.setImageViewBitmap(R.id.widgetBackground, Utils.createBackground(config, SIZE_MEDIUM));

        return baseView;
    }

}

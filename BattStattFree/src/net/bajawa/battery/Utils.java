package net.bajawa.battery;

import net.bajawa.battery.provider.BatteryWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

public class Utils {

	public static final String TAG = "BattStatt";
	public static final String PATH = "/data/data/net.bajawa.battery/shared_prefs/";

	public static int getWidgetSize(Context context, int layout) {
		switch (layout) {
			case R.layout.battery_large:
				return BatteryWidgetProvider.SIZE_LARGE;
				
			case R.layout.battery_medium:
				return BatteryWidgetProvider.SIZE_MEDIUM;
				
			case R.layout.battery_small:
				return BatteryWidgetProvider.SIZE_SMALL;
				
			case R.layout.battery_small_number:
				return BatteryWidgetProvider.SIZE_SMALL_NUMBERS;
				
			default:
				return -1;
		}
	}

	public static Bitmap createBackground(WidgetConfig config, int widgetSize) {
		int columns = 0;
		switch (widgetSize) {
			case BatteryWidgetProvider.SIZE_LARGE:
				columns = 4;
				break;
			case BatteryWidgetProvider.SIZE_MEDIUM:
				columns = 2;
				break;
			case BatteryWidgetProvider.SIZE_SMALL:
			case BatteryWidgetProvider.SIZE_SMALL_NUMBERS:
				columns = 1;
				break;
		}

		int width = (columns * 74) - 2;
		int height = 74;

		Bitmap ret = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas c = new Canvas(ret);

		if (config.isBackgroundEnabled()) {
			int color = Color.parseColor(config.getBackgroundColor());
			int r = (color >> 16) & 0xff;
			int g = (color >> 8) & 0xff;
			int b = (color >> 0) & 0xff;
			c.drawARGB(127, r, g, b);

		} else {
			c.drawARGB(0, 0, 0, 0);
		}

		return ret;
	}
	
	public static float getBestFontSize(String stringToAdjust, float maxWidth, float startSize) {
		Paint paint = new Paint();
		paint.setTextSize(startSize);
		
		float textWidth = paint.measureText(stringToAdjust);
		
		while (textWidth >= maxWidth) {
			paint.setTextSize(paint.getTextSize() - 1);
			textWidth = paint.measureText(stringToAdjust);
		}
		
		return paint.getTextSize();
	}
	
	public static float getStringWidth(String string, float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		return paint.measureText(string);
	}
}

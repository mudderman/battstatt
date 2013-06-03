package net.bajawa.batterypro;

import net.bajawa.batterypro.provider.BatteryWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Utils {

	public static final String TAG = "BattStatt";
	public static final String PATH = "/data/data/net.bajawa.batterypro/shared_prefs/";

	public static int getWidgetSize(int layout) {
		switch (layout) {
			case R.layout.battery_large:
				return BatteryWidgetProvider.SIZE_LARGE;
				
			case R.layout.battery_medium:
				return BatteryWidgetProvider.SIZE_MEDIUM;
				
			case R.layout.battery_small:
				return BatteryWidgetProvider.SIZE_SMALL;
				
			default:
				return -1;
		}
	}

	public static Bitmap createBackground(WidgetConfig config) {
		int width = 1;
		int height = 74;

		Bitmap ret = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas c = new Canvas(ret);

		if (config.isBackgroundEnabled()) {
		    int color = config.getBackgroundColor();
			int a = Color.alpha(color);
			int r = Color.red(color);
			int g = Color.green(color);
			int b = Color.blue(color);
			c.drawARGB(a, r, g, b);

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
	
	public static int dipToPixel(Context context, int dip) {
	    DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        float factor = metrics.densityDpi / 160f;
        return (int)(dip * factor);
	}
	
//	public static Language parseLanguageFile(String file) {
//	    return null;
//	}
}

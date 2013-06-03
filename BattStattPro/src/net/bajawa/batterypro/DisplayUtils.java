package net.bajawa.batterypro;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtils {
    
    public static int dpiToPixel(Context context, int dpi) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        float factor = metrics.densityDpi / 160f;
        return (int)(dpi * factor);
    }
}

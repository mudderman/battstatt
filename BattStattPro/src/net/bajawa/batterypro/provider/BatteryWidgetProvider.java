package net.bajawa.batterypro.provider;

import java.io.File;
import java.util.Locale;
import net.bajawa.batterypro.BatteryInfo;
import net.bajawa.batterypro.BatteryService;
import net.bajawa.batterypro.Language;
import net.bajawa.batterypro.R;
import net.bajawa.batterypro.Utils;
import net.bajawa.batterypro.WidgetConfig;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

public abstract class BatteryWidgetProvider extends AppWidgetProvider {
    
    public static final String ACTION_CONFIG = "bajawa.intent.action.CONFIGURE";
    public static final String ACTION_USAGE = "bajawa.intent.action.USAGE";
    public static final String ACTION_INFO = "bajawa.intent.action.INFO";
    public static final String ACTION_VOID = "bajawa.intent.action.VOID";

    public static final String ACTION_UPDATE_WIDGETS = "bajawa.intent.action.UPDATE_WIDGETS";
    public static final String ACTION_WIDGET_CLICK = "bajawa.intent.action.WIDGET_CLICK";

    public static final int SIZE_LARGE = 0;
    public static final int SIZE_MEDIUM = 1;
    public static final int SIZE_SMALL = 2;

    public static final String NAME_LARGE = "battery_large_";
    public static final String NAME_MEDIUM = "battery_medium_";
    public static final String NAME_SMALL = "battery_small_";

    protected Typeface typefaceNormal;
    protected Typeface typefaceBold;

    private ComponentName service;
    
    protected int batteryLevel;
    protected int batteryStatus;

    abstract protected RemoteViews buildUpdateView(Context context, RemoteViews baseView, WidgetConfig config);

    private RemoteViews createRemoteViewsBase(Context context, WidgetConfig config) {
        // Set the language
        Locale locale;
        if (config.getLocale().equals(WidgetConfig.WIDGET_AUTO_LANGUAGE)) {
            locale = Locale.getDefault();
        } else {
            locale = new Locale(config.getLocale());
        }

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Configuration conf = context.getResources().getConfiguration();
        conf.locale = locale;
        context.getResources().updateConfiguration(conf, dm);

        RemoteViews views = new RemoteViews(context.getPackageName(), config.getLayout());

        // Click action
        Intent configIntent = new Intent(config.getAction());
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, config.getWidgetId());
        
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, configIntent, 0);
        views.setOnClickPendingIntent(R.id.widgetContainer, pIntent);

        // Custon font init
        if (config.getFont().endsWith(".ttf")) {
            File fontFile = new File(context.getFilesDir(), config.getWidgetId() + ".ttf");
            if (fontFile.exists() && fontFile.canRead()) {
                typefaceNormal = Typeface.createFromFile(fontFile);
                typefaceBold = Typeface.create(Typeface.createFromFile(fontFile), Typeface.BOLD);

            } else {
                typefaceNormal = Typeface.create(config.getFont(), Typeface.NORMAL);
                typefaceBold = Typeface.create(config.getFont(), Typeface.BOLD);
            }

        } else {
            typefaceNormal = Typeface.create(config.getFont(), Typeface.NORMAL);
            typefaceBold = Typeface.create(config.getFont(), Typeface.BOLD);
        }
        
        // Start the service if it's not running
        if (service == null) {
            service = context.startService(new Intent(context, BatteryService.class));
        }
        
        batteryLevel = BatteryService.getBatteryLevel();
        batteryStatus = BatteryService.getBatteryStatus();
        
        if (batteryLevel == -1 || batteryStatus == -1) {
            service = context.startService(new Intent(context, BatteryService.class));
            
            SharedPreferences prefs = context.getSharedPreferences("data", Context.MODE_WORLD_WRITEABLE);
            batteryLevel = prefs.getInt("lastLevel", BatteryService.getBatteryLevel());
            batteryStatus = prefs.getInt("lastAction", BatteryService.getBatteryStatus());
            
        } else {
            SharedPreferences prefs = context.getSharedPreferences("data", Context.MODE_WORLD_WRITEABLE);
            batteryLevel = prefs.getInt("lastLevel", BatteryService.getBatteryLevel());
            batteryStatus = prefs.getInt("lastAction", BatteryService.getBatteryStatus());
        }

        return views;
    }

    @Override
    public void onEnabled(Context context) {
        service = context.startService(new Intent(context, BatteryService.class));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            service = context.startService(new Intent(context, BatteryService.class));
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            new File(Utils.PATH + "widget_" + appWidgetIds[i] + ".xml").delete();
            new File(context.getFilesDir(), appWidgetIds[i] + ".ttf").delete();
        }
    }

    @Override
    public void onDisabled(Context context) {
        if (context.stopService(new Intent(context, BatteryService.class))) {
            service = null;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_UPDATE_WIDGETS.equals(intent.getAction())) {
            updateWidgets(context);
        }
        super.onReceive(context, intent);
    }

    /**
     * This will update all the widgets for the subclass
     * 
     * @param context
     */
    private void updateWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, getClass()));
        for (int i = 0; i < ids.length; i++) {
            WidgetConfig config = new WidgetConfig(context, ids[i]);
            
            if (config.configFileExists()) {
                RemoteViews view = buildUpdateView(context, createRemoteViewsBase(context, config), config);
                manager.updateAppWidget(ids[i], view);
                
            } else {
                config = WidgetConfig.createDefaultConfig(context, ids[i]);
                RemoteViews view = buildUpdateView(context, createRemoteViewsBase(context, config), config);
                manager.updateAppWidget(ids[i], view);
            }
        }
    }

    protected Typeface getTypeface(WidgetConfig config, Language language, int currentIndex) {
        BatteryInfo info = new BatteryInfo();

        if (info.isEmpty()) {
            return typefaceBold;
        }

        if (info.isFull() && currentIndex == 0) {
            return typefaceBold;
        }

        if (WidgetConfig.RULE_ALL.equals(config.getBoldifyRule())) {
            return typefaceBold;

        } else if (WidgetConfig.RULE_FIRST.equals(config.getBoldifyRule())) {
            if (currentIndex == 0) {
                return typefaceBold;
            }

        } else if (WidgetConfig.RULE_MIDDLE.equals(config.getBoldifyRule())) {
            if (currentIndex == 1 && language.getStrings().length == 3) {
                return typefaceBold;
            }

        } else if (WidgetConfig.RULE_TENS.equals(config.getBoldifyRule())) {
            switch (currentIndex) {
                case 0:
                    if (language.isTensFirst() && !info.isNumber()) {
                        return typefaceBold;
                    }

                    break;

                case 2:
                    if (!language.isTensFirst()) {
                        return typefaceBold;
                    }

                    break;
            }

        } else if (WidgetConfig.RULE_TENSNUMBERS.equals(config.getBoldifyRule())) {
            if (language.getStrings().length == 3 && currentIndex != 1) {
                return typefaceBold;
            }

        }

        return typefaceNormal;
    }

    protected String applyCapitalizationRule(WidgetConfig config, Language language, int currentIndex) {
        String currentWord = language.getString(currentIndex);
        BatteryInfo info = new BatteryInfo();

        if (info.isEmpty() || info.isFull()) {
            return currentWord.toUpperCase();
        }

        if (WidgetConfig.RULE_ALL.equals(config.getCapitalizeRule())) {
            return currentWord.toUpperCase();

        } else if (WidgetConfig.RULE_FIRST.equals(config.getCapitalizeRule())) {
            if (currentIndex == 0) {
                return currentWord.toUpperCase();
            }

        } else if (WidgetConfig.RULE_MIDDLE.equals(config.getCapitalizeRule())) {
            if (language.getStrings().length == 3 && currentIndex == 1) {
                return currentWord.toUpperCase();
            }

        } else if (WidgetConfig.RULE_TENS.equals(config.getCapitalizeRule())) {
            switch (currentIndex) {
                case 0:
                    if (language.isTensFirst() && !info.isNumber()) {
                        return currentWord.toUpperCase();
                    }
                    break;
                    
                case 2:
                    if (!language.isTensFirst()) {
                        return currentWord.toUpperCase();
                    }
                    break;
            }

        } else if (WidgetConfig.RULE_TENSNUMBERS.equals(config.getCapitalizeRule())) {
            if (language.getStrings().length == 3 && currentIndex != 1) {
                return currentWord.toUpperCase();
            }

        } else if (WidgetConfig.RULE_FIRSTLETTER.equals(config.getCapitalizeRule())) {
            if (currentIndex == 0) {
                return currentWord.substring(0, 1).toUpperCase() + currentWord.substring(1).toLowerCase();
            }

        } else if (WidgetConfig.RULE_FIRSTLETTERWORD.equals(config.getCapitalizeRule())) {
            return currentWord.substring(0, 1).toUpperCase() + currentWord.substring(1).toLowerCase();

        } else if (WidgetConfig.RULE_FIRSTLETTERTENS.equals(config.getCapitalizeRule())) {
            if (language.getStrings().length == 1 || language.getStrings().length == 2 && currentIndex == 0 || language.getStrings().length == 3 && currentIndex == 2) {
                return currentWord.substring(0, 1).toUpperCase() + currentWord.substring(1).toLowerCase();
            }
        }

        return currentWord.toLowerCase();
    }

    protected void paintActionString(WidgetConfig config, Canvas c, String actionString, float yAnchor) {
        if (actionString != null) {

            Paint actionPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            actionPaint.setTextSize(config.getSizeActionDpi());
            actionPaint.setColor(config.getColorAction());

            if (config.isCapitalizeAction()) {
                actionString = actionString.toUpperCase();

            } else {
                actionString = actionString.toLowerCase();
            }

            if (config.isBoldifyAction()) {
                actionPaint.setTypeface(typefaceBold);

            } else {
                actionPaint.setTypeface(typefaceNormal);
            }

            float actionWidth = actionPaint.measureText(actionString);
            float actionXpos = (c.getWidth() - actionWidth) / 2;

            if (config.isShowShadow()) {
                actionPaint.setShadowLayer(2, 2, 2, Color.BLACK);
            }
            
            c.drawText(actionString, actionXpos, yAnchor + config.getOffsetAction(), actionPaint);
        }
    }
}

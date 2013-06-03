package net.bajawa.batterypro.provider;

import net.bajawa.batterypro.BatteryInfo;
import net.bajawa.batterypro.Language;
import net.bajawa.batterypro.R;
import net.bajawa.batterypro.Utils;
import net.bajawa.batterypro.WidgetConfig;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.widget.RemoteViews;

public class WidgetProviderSmall extends BatteryWidgetProvider {

    @Override
    protected RemoteViews buildUpdateView(Context context, RemoteViews baseView, WidgetConfig config) {
        Language language = new Language(context, config);
        BatteryInfo info = new BatteryInfo();

        // init canvas
        int height = Utils.dipToPixel(context, 115);
        int width = Utils.dipToPixel(context, 96);
        Bitmap bit = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas c = new Canvas(bit);

        // Main text
        String[] strings = language.getStrings();

        if (config.isShowNumbers()) {
            Paint numberPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            numberPaint.setTypeface(typefaceNormal);
            numberPaint.setTextSize(config.getSizeNumberDpi());
            numberPaint.setColor(config.getColorNumber());

            // Boldify
            if (!WidgetConfig.RULE_NONE.equals(config.getBoldifyRule())) {
                numberPaint.setTypeface(typefaceBold);
            }

            if (config.isShowShadow()) {
                numberPaint.setShadowLayer(2, 2, 2, Color.BLACK);
            }

            String level = String.valueOf(info.getBatteryLevel());
            float levelWidth = numberPaint.measureText(level);
            float levelXpos = (c.getWidth() - levelWidth) / 2;
            float levelYPos = (numberPaint.getFontMetricsInt().top *= -1) + config.getOffsetNumber();
            c.drawText(level, levelXpos, levelYPos, numberPaint);

            String actionString = language.getActionString();
            if (actionString != null) {
                paintActionString(config, c, actionString, levelYPos);
            }

        } else {
            Paint[] paints = new Paint[strings.length];
            float[] widths = new float[paints.length];
            float[] xVals = new float[strings.length];
            int[] sizes = new int[strings.length];

            float sentenceWidth = 0;

            float totalHeight = 0;

            // init the paints and get the text widths
            for (int i = 0; i < paints.length; i++) {
                paints[i] = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

                paints[i].setTypeface(getTypeface(config, language, i));
                strings[i] = applyCapitalizationRule(config, language, i);

                int currentColor = 0;

                switch (i) {
                    case 0: // First word
                        // If the language is using "and", the first word will
                        // be the number
                        if (paints.length == 3) {
                            if (language.isTensFirst()) {
                                totalHeight += config.getSizeTensDpi();
                                sizes[i] = config.getSizeTensDpi();
                                currentColor = config.getColorTens();

                            } else {
                                totalHeight += config.getSizeNumbersDpi();
                                sizes[i] = config.getSizeNumbersDpi();
                                currentColor = config.getColorNumbers();
                            }

                        } else {
                            totalHeight += config.getSizeTensDpi();
                            sizes[i] = config.getSizeTensDpi();
                            currentColor = config.getColorTens();
                        }

                        break;

                    case 1: // "And" if lanugage using it, numbers otherwise
                        // Numbers
                        if (paints.length == 2) {
                            totalHeight += config.getSizeNumbersDpi();
                            sizes[i] = config.getSizeNumbersDpi();
                            currentColor = config.getColorNumbers();

                            // Language using "and"
                        } else {
                            totalHeight += config.getSizeAndDpi();
                            sizes[i] = config.getSizeAndDpi();
                            currentColor = config.getColorAnd();
                        }
                        break;

                    case 2: // Tens if language using "and"
                        if (language.isTensFirst()) {
                            totalHeight += config.getSizeNumbersDpi();
                            sizes[i] = config.getSizeNumbersDpi();
                            currentColor = config.getColorNumbers();

                        } else {
                            totalHeight += config.getSizeTensDpi();
                            sizes[i] = config.getSizeTensDpi();
                            currentColor = config.getColorTens();
                        }
                        break;
                }

                paints[i].setTextSize(sizes[i]);
                paints[i].setColor(currentColor);

                widths[i] = paints[i].measureText(strings[i]);
                xVals[i] = (c.getWidth() - widths[i]) / 2;
                sentenceWidth += widths[i];
            }

            // Determine the height of all text in order to determine the middle
            // point to draw at
            String actionString = language.getActionString();
            if (actionString != null) {
                totalHeight += config.getSizeActionDpi();
            }

            float yStartPos = (c.getHeight() - totalHeight) / 2;
            yStartPos += (paints[0].getFontMetricsInt().top *= -1);

            // Do the text drawing
            for (int i = 0; i < strings.length; i++) {

                // Dropshadow
                if (config.isShowShadow()) {
                    paints[i].setShadowLayer(2, 2, 2, Color.BLACK);
                }

                switch (i) {
                    case 0:
                        yStartPos += config.getOffsetLevel();
                        break;

                    case 1:
                    case 2:
                        yStartPos += sizes[i];
                        break;
                }

                c.drawText(strings[i], xVals[i], yStartPos, paints[i]);
            }

            if (actionString != null) {
                paintActionString(config, c, actionString, yStartPos + config.getSizeActionDpi());
            }
        }

        // Transparent background
        baseView.setImageViewBitmap(R.id.widgetBackground, Utils.createBackground(config));
        baseView.setImageViewBitmap(R.id.text, bit);

        return baseView;
    }

}

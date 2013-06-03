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

public class WidgetProviderLarge extends BatteryWidgetProvider {

    @Override
    protected RemoteViews buildUpdateView(Context context, RemoteViews baseView, WidgetConfig config) {

        Language language = new Language(context, config);
        BatteryInfo info = new BatteryInfo();

        // init canvas
        int height = Utils.dipToPixel(context, 96);
        int width = Utils.dipToPixel(context, 318);
        Bitmap bit = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas c = new Canvas(bit);

        // Main text
        String[] strings = language.getStrings();

        // Display number instead of text
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
            int[] topVals = new int[widths.length];
            float[] xVals = new float[topVals.length];

            float sentenceWidth = 0;
            
            int largestSize = 0;

            // Init the paints
            for (int i = 0; i < paints.length; i++) {
                paints[i] = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                
                paints[i].setTypeface(getTypeface(config, language, i)); 
                strings[i] = applyCapitalizationRule(config, language, i);
                
                int currentSize = 0;
                int currentColor = 0;
                
                switch (i) {
                    case 0: // First word
                        // If the language is using "and", the first word will
                        // be the number
                        if (paints.length == 3) {
                            if (language.isTensFirst()) {
                                currentSize = config.getSizeTensDpi();
                                currentColor = config.getColorTens();
                            } else {
                                currentSize = config.getSizeNumbersDpi();
                                currentColor = config.getColorNumbers();
                            }
                            
                        } else {
                            currentSize = config.getSizeTensDpi();
                            currentColor = config.getColorTens();
                        }
                        break;

                    case 1: // "And" if lanugage using it, numbers otherwise
                        // Numbers
                        if (paints.length == 2) {
                            currentSize = config.getSizeNumbersDpi();
                            currentColor = config.getColorNumbers();

                            // Language using "and"
                        } else {
                            currentSize = config.getSizeAndDpi();
                            currentColor = config.getColorAnd();
                        }
                        break;

                    case 2: // Tens if language using "and"
                        if (language.isTensFirst()) {
                            currentSize = config.getSizeNumbersDpi();
                            currentColor = config.getColorNumbers();
                        } else {
                            currentSize = config.getSizeTensDpi();
                            currentColor = config.getColorTens();
                        }
                        break;
                }
                
                paints[i].setTextSize(currentSize);
                paints[i].setColor(currentColor);

                
                if (currentSize > largestSize) {
                    largestSize = currentSize;
                }

                widths[i] = paints[i].measureText(strings[i]);
                topVals[i] = (paints[0].getFontMetricsInt().top *= -1);

                sentenceWidth += widths[i];
            }

            // Determine the height of all text in order to determine the middle
            // point to draw at
            float totalHeight = largestSize;

            String actionString = language.getActionString();
            if (actionString != null) {
                totalHeight += config.getSizeActionDpi();
            }

            float yStartPos = (c.getHeight() - totalHeight) / 2;
            yStartPos += + (paints[0].getFontMetricsInt().top *= -1);

            // Do the text drawing
            for (int i = 0; i < strings.length; i++) {

                // Update the x positions of the texts
                if (i != 0) {
                    xVals[i] = xVals[i - 1] + widths[i - 1];

                } else {
                    xVals[0] = (c.getWidth() - sentenceWidth) / 2;
                }

                // Draw it
                if (config.isShowShadow()) {
                    paints[i].setShadowLayer(2, 2, 2, Color.BLACK);
                }

                c.drawText(strings[i], xVals[i], yStartPos + config.getOffsetLevel(), paints[i]);
            }

            // Paint action text, which can be "charging", "percent" or null
            // (nothing to display)
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

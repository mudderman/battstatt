package net.bajawa.lib.widget;

import net.bajawa.lib.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ColorPicker extends RelativeLayout {

    private static final String SUPERSTATE = "superState";
    private static final String COLOR = "color";

    private View swatch;
    private SeekBar red;
    private SeekBar green;
    private SeekBar blue;
    private SeekBar alpha;
    private TextView textBox;
    private OnColorChangedListener listener;

    private Boolean displayAlpha;

    private String[] colorNames;
    private int[] colorValues;

    public ColorPicker(Context context) {
        super(context);
        initMixer(null);
    }

    public ColorPicker(Context context, boolean displayAlpha) {
        super(context);
        this.displayAlpha = displayAlpha;
        initMixer(null);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMixer(attrs);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initMixer(attrs);
    }

    private void initMixer(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.color_picker, this, true);

        swatch = findViewById(R.id.swatch);

        red = (SeekBar) findViewById(R.id.red);
        red.setMax(0xff);
        red.setOnSeekBarChangeListener(onMix);

        green = (SeekBar) findViewById(R.id.green);
        green.setMax(0xff);
        green.setOnSeekBarChangeListener(onMix);

        blue = (SeekBar) findViewById(R.id.blue);
        blue.setMax(0xff);
        blue.setOnSeekBarChangeListener(onMix);

        TextView alphaLabel = (TextView) findViewById(R.id.alphaLabel);
        alpha = (SeekBar) findViewById(R.id.alpha);
        alpha.setMax(0xff);
        alpha.setOnSeekBarChangeListener(onMix);
        
        textBox = (TextView) findViewById(R.id.textBox);
        setTextBoxText();

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SimpleColorPickerPreference, R.styleable.ColorPickerPreference_colorPickerPreferenceStyle, 0);

            if (displayAlpha == null) {
                displayAlpha = a.getBoolean(R.styleable.ColorPickerPreference_displayAlpha, false);
            }

            if (!displayAlpha) {
                alphaLabel.setVisibility(View.GONE);
                alpha.setVisibility(View.GONE);
            }

            setColor(a.getInt(R.styleable.ColorPickerPreference_color, 0xffffffff));

            a.recycle();

        } else {
            if (!displayAlpha) {
                alphaLabel.setVisibility(View.GONE);
                alpha.setVisibility(View.GONE);
            }
            setColor(0xffffffff);
        }

        // Setup the presets list
        Resources res = getResources();
        colorNames = res.getStringArray(R.array.color_names);
        colorValues = res.getIntArray(R.array.color_values_int);

        findViewById(R.id.presetsButton).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setItems(colorNames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setColor(colorValues[which]);
                    }
                });
                builder.create().show();
            }
        });
    }

    private void setTextBoxText() {
        StringBuilder sb = new StringBuilder();
        
        // hex (argb)
        sb.append("#");
        if (displayAlpha) {
            sb.append(String.format("%02x", alpha.getProgress()));
        }
            
        sb.append(String.format("%02x", red.getProgress()));
        sb.append(String.format("%02x", green.getProgress()));
        sb.append(String.format("%02x", blue.getProgress()));
        
        // (a)rgb
        sb.append("\n");
        if (displayAlpha) {
            sb.append(alpha.getProgress()).append(", ");
        }
        
        sb.append(red.getProgress()).append(", ");
        sb.append(green.getProgress()).append(", ");
        sb.append(blue.getProgress());
        
        textBox.setText(sb);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }

    public int getColor() {
        int alphaValue;
        if (displayAlpha) {
            alphaValue = alpha.getProgress();
        } else {
            alphaValue = 0xff;
        }

        return (Color.argb(alphaValue, red.getProgress(), green.getProgress(), blue.getProgress()));
    }

    public void setColor(int color) {
        red.setProgress(Color.red(color));
        green.setProgress(Color.green(color));
        blue.setProgress(Color.blue(color));
        if (displayAlpha) {
            alpha.setProgress(Color.alpha(color));
        }
    }

    public void setDisplayAlpha(boolean displayAlpha) {
        this.displayAlpha = displayAlpha;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable(SUPERSTATE, super.onSaveInstanceState());
        state.putInt(COLOR, getColor());
        return (state);
    }

    @Override
    public void onRestoreInstanceState(Parcelable ss) {
        Bundle state = (Bundle) ss;
        super.onRestoreInstanceState(state.getParcelable(SUPERSTATE));
        setColor(state.getInt(COLOR));
    }

    private SeekBar.OnSeekBarChangeListener onMix = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int color = getColor();

            swatch.setBackgroundColor(color);
            setTextBoxText();
            
            if (listener != null) {
                listener.onColorChange(color);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    public interface OnColorChangedListener {
        public void onColorChange(int argb);
    }
}

package net.bajawa.lib.preference;

import net.bajawa.lib.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class VolumePickerPreference extends DialogPreference implements OnCheckedChangeListener {

    private int chosenStream;
    private int chosenVolume = -1;

    private AudioManager manager;

    private LayoutInflater inflater;
    private View volumePickerView;

    private int streamMax;
    private int streamCurrentVolume;
    private SeekBar volumebar;
    private CheckBox noChangeCheckbox;

    public VolumePickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.volumePickerPreferenceStyle);
    }

    public VolumePickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.inflater = LayoutInflater.from(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VolumePickerPreference, defStyle, 0);
        chosenStream = a.getInt(R.styleable.VolumePickerPreference_volumeType, -1);
        a.recycle();

        if (chosenStream == -1) {
            chosenStream = AudioManager.STREAM_RING;
        }

        setPositiveButtonText("Ok");
        setNegativeButtonText("Cancel");

        streamMax = manager.getStreamMaxVolume(chosenStream);
        streamCurrentVolume = manager.getStreamVolume(chosenStream);
    }

    @Override
    protected View onCreateDialogView() {
        volumePickerView = inflater.inflate(R.layout.volume_picker, null);
        volumebar = (SeekBar) volumePickerView.findViewById(R.id.volumebar);
        noChangeCheckbox = (CheckBox) volumePickerView.findViewById(R.id.noChangeCheck);
        noChangeCheckbox.setOnCheckedChangeListener(this);

        volumebar.setMax(streamMax);

        // If volume is -1, box is checked and seekbar is set to the system
        // volume
        if (chosenVolume == -1) {
            noChangeCheckbox.setChecked(true);
            volumebar.setProgress(streamCurrentVolume);
            volumebar.setEnabled(false);

        } else {
            noChangeCheckbox.setChecked(false);
            volumebar.setProgress(chosenVolume);
            volumebar.setEnabled(true);
        }

        return volumePickerView;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            if (noChangeCheckbox.isChecked()) {
                setVolume(-1);

            } else {
                setVolume(volumebar.getProgress());
            }
        }
    }
    
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        
        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        
        if (chosenVolume == -1) {
            summaryView.setText("No change");
            
        } else {
            summaryView.setText(chosenVolume + "/" + streamMax);
            
        }
        summaryView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        // If a persisted value should be restored
        if (restorePersistedValue) {
            chosenVolume = getPersistedInt(streamCurrentVolume);

        } else {
            chosenVolume = -1;
        }

        setVolume(chosenVolume);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setControls(isChecked);
    }

    /**
     * 
     * @param noChangeChecked
     *            true if using "no change" option, false otherwise
     */
    private void setControls(boolean noChangeChecked) {
        // If volume is -1, box is checked and seekbar is set to the system
        // volume
        if (noChangeChecked) {
            noChangeCheckbox.setChecked(true);
            volumebar.setProgress(streamCurrentVolume);
            volumebar.setEnabled(false);

        } else {
            noChangeCheckbox.setChecked(false);
            if (chosenVolume != -1) {
                volumebar.setProgress(chosenVolume);
            }
            volumebar.setEnabled(true);
        }
    }
    
    public void setVolume(int volume) {
        if (chosenVolume != volume) {
            if (!callChangeListener(volume)) {
                return;
            }
            
            chosenVolume = volume;
            persistInt(chosenVolume);
            notifyChanged();
        }
    }
    
    public int getVolume() {
        return this.chosenVolume;
    }
}

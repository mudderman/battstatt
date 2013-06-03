package net.bajawa.lib.widget;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class VolumePicker extends LinearLayout {
    
    private int chosenStream;
    private boolean noChangeChecked;

    private Context context;
    private AudioManager manager;

    public VolumePicker(Context context) {
        this(context, null);
    }
    
    public VolumePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        this.context = context;
        this.manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        initPicker(attrs);
    }
    
    private void initPicker(AttributeSet attrs) {
    }
    
    public int getVolume() {
        return -1;
    }
    
    public void setVolume(int volume) {
    }
}

package net.bajawa.lib.preference;

import net.bajawa.lib.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class OnOffPreference extends Preference {
	
	private static final int DEFAULT_DRAWABLE_ON = R.drawable.onoff_pref_on;
	private static final int DEFAULT_DRAWABLE_OFF = R.drawable.onoff_pref_off;

	private boolean checked;
	private CharSequence summaryOn;
	private CharSequence summaryOff;
	private boolean disableDependentsState;
	private int drawableOn = DEFAULT_DRAWABLE_ON;
	private int drawableOff = DEFAULT_DRAWABLE_OFF;
	private boolean disableGraphics;
	
	private Bitmap bitmap;
	
	private static ColorMatrix matrix;
	private static ColorMatrixColorFilter matrixFilter;
	private static Paint paint;
	
	static {
		matrix = new ColorMatrix();
		matrix.setSaturation(0);
		
		matrixFilter = new ColorMatrixColorFilter(matrix);
		
		paint = new Paint();
		paint.setColorFilter(matrixFilter);
	}

	public OnOffPreference(Context context) {
		this(context, null);
	}

	public OnOffPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OnOffPreference, defStyle, 0);

		summaryOn = a.getString(R.styleable.OnOffPreference_summaryOn);
		summaryOff = a.getString(R.styleable.OnOffPreference_summaryOff);
		disableDependentsState = a.getBoolean(R.styleable.OnOffPreference_disableDependentsState, false);
		
		drawableOn = a.getResourceId(R.styleable.OnOffPreference_drawableOn, DEFAULT_DRAWABLE_ON);
		drawableOff = a.getResourceId(R.styleable.OnOffPreference_drawableOff, DEFAULT_DRAWABLE_OFF);
		
		disableGraphics = a.getBoolean(R.styleable.OnOffPreference_disableGraphics, false);
		
		a.recycle();
		
		setWidgetLayoutResource(R.layout.toggle_preference);
	}

	public OnOffPreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.onOffPreferenceStyle);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		
		// Only use custom drawables if both are set
		if (drawableOn == DEFAULT_DRAWABLE_ON) {
			drawableOff = DEFAULT_DRAWABLE_OFF;
		}
		
		if (drawableOff == DEFAULT_DRAWABLE_OFF) {
			drawableOn = DEFAULT_DRAWABLE_ON;
		}
		
		// Set the state of the image
		if (disableGraphics) {
			((ImageView) view.findViewById(R.id.toggleImage)).setImageBitmap(null);
		} else {
			setToggleImage(((ImageView) view.findViewById(R.id.toggleImage)));
		}
		
		// Sync the summary view
		TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        if (summaryView != null) {
            boolean useDefaultSummary = true;
            if (checked && summaryOn != null) {
                summaryView.setText(summaryOn);
                useDefaultSummary = false;
            } else if (!checked && summaryOff != null) {
                summaryView.setText(summaryOff);
                useDefaultSummary = false;
            }

            if (useDefaultSummary) {
                final CharSequence summary = getSummary();
                if (summary != null) {
                    summaryView.setText(summary);
                    useDefaultSummary = false;
                }
            }
            
            int newVisibility = View.GONE;
            if (!useDefaultSummary) {
                // Someone has written to it
                newVisibility = View.VISIBLE;
            }
            if (newVisibility != summaryView.getVisibility()) {
                summaryView.setVisibility(newVisibility);
            }
        }
	}

	@Override
	protected void onClick() {
		super.onClick();

		boolean newValue = !checked;
		if (!callChangeListener(newValue)) {
			return;
		}

		setChecked(newValue);
	}

	private void setToggleImage(ImageView imageView) {
		if (imageView != null) {
			if (checked) {
				imageView.setImageResource(drawableOn);
				
			} else {
				imageView.setImageResource(drawableOff);
			}
			
			
			if (isEnabled()) {
				if (checked) {
					imageView.setImageResource(drawableOn);
					
				} else {
					imageView.setImageResource(drawableOff);
				}
				
			} else {
				if (bitmap != null) {
					bitmap.recycle();
				}
				
				bitmap = createBitmap(checked ? drawableOn : drawableOff);
				imageView.setImageBitmap(bitmap);
			}
		}
	}
	
	public Bitmap createBitmap(int resId) {
		Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), resId);
		Bitmap retBit = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
		
		Canvas c = new Canvas(retBit);
		c.drawBitmap(bmp, 0.0f, 0.0f, paint);
		
		bmp.recycle();
		return retBit;
	}

	public void setChecked(boolean checked) {
		if (this.checked != checked) {
			this.checked = checked;
			persistBoolean(checked);
			notifyDependencyChange(shouldDisableDependents());
			notifyChanged();
		}
	}
	
	public void setChecked(int checked) {
	    setChecked(checked == 1);
	}

	public boolean isChecked() {
		return checked;
	}

	@Override
	public boolean shouldDisableDependents() {
		boolean shouldDisable = disableDependentsState ? checked : !checked;
		return shouldDisable || super.shouldDisableDependents();
	}

	public void setSummaryOn(CharSequence summary) {
		summaryOn = summary;
		if (isChecked()) {
			notifyChanged();
		}
	}

	public void setSummaryOn(int summaryResId) {
		setSummaryOn(getContext().getString(summaryResId));
	}

	public CharSequence getSummaryOn() {
		return summaryOn;
	}

	public void setSummaryOff(CharSequence summary) {
		summaryOff = summary;
		if (!isChecked()) {
			notifyChanged();
		}
	}

	public void setSummaryOff(int summaryResId) {
		setSummaryOff(getContext().getString(summaryResId));
	}

	public CharSequence getSummaryOff() {
		return summaryOff;
	}

	public boolean getDisableDependentsState() {
		return disableDependentsState;
	}

	public void setDisableDependentsState(boolean disableDependentsState) {
		this.disableDependentsState = disableDependentsState;
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getBoolean(index, false);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		setChecked(restorePersistedValue ? getPersistedBoolean(checked) : (Boolean) defaultValue);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			// No need to save instance state since it's persistent
			return superState;
		}

		final SavedState myState = new SavedState(superState);
		myState.checked = isChecked();
		return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			// Didn't save state for us in onSaveInstanceState
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		setChecked(myState.checked);
	}

	private static class SavedState extends BaseSavedState {
		boolean checked;

		public SavedState(Parcel source) {
			super(source);
			checked = source.readInt() == 1;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(checked ? 1 : 0);
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}

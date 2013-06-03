package net.bajawa.lib.preference;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.bajawa.lib.R;
import net.bajawa.lib.view.CategoryListAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ActionSelectorPreference extends Preference {
    
    private static final String DEFAULT_COLOR = "#ffffff";
    
    private AlertDialog dialog;
    
    private Context context;
    private PackageManager pm;
    
    private CategoryListAdapter adapter;
    private AllAppsAdapter appsAdapter;
    
    private ResolveInfo selectedAction;

    public ActionSelectorPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.simpleColorPickerPreferenceStyle);
    }

    public ActionSelectorPreference(Context context) {
        this(context, null);
    }
    
    public ActionSelectorPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        this.context = context;
        this.pm = context.getPackageManager();
        
        // Get the xml values
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleColorPickerPreference, defStyle, 0);
        Resources res = context.getResources();
        a.recycle();
        
        appsAdapter = new AllAppsAdapter();
        
        adapter = new CategoryListAdapter(context);
        adapter.addSection("Installed applications", appsAdapter);
        
        // Build the color dialog list
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setAdapter(adapter, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setTitle("Select an action");
        dialog = builder.create();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
//        
//        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
//        
//        View colorBox = view.findViewById(R.id.color);
//        View colorFrame = view.findViewById(R.id.colorFrame);
//        
//        if (colorBox != null) {
//            
//            if (isEnabled()) {
//                String chosenColorString = view.getContext().getString(R.string.chosen_color_label);
//                
//                if (chosenColor == null) {
//                    colorFrame.setBackgroundColor(Color.BLACK);
//                    colorBox.setBackgroundColor(Color.parseColor(DEFAULT_COLOR));
//                    summaryView.setText(chosenColorString + " " + getColorName(DEFAULT_COLOR));
//                    
//                } else {
//                    colorFrame.setBackgroundColor(Color.WHITE);
//                    colorBox.setBackgroundColor(Color.parseColor(chosenColor));
//                    String colorName = getColorName(chosenColor);
//                    if (colorName == null) {
//                        colorName = "None";
//                    }
//                    summaryView.setText(chosenColorString + " " + colorName);
//                }
//                
//                colorFrame.setVisibility(View.VISIBLE);
//                colorBox.setVisibility(View.VISIBLE);
//                summaryView.setVisibility(View.VISIBLE);
//                
//            } else {
//                colorFrame.setVisibility(View.GONE);
//                colorBox.setVisibility(View.GONE);
//                summaryView.setVisibility(View.GONE);
//            }
//        }
    }
    
    @Override
    protected void onClick() {
        super.onClick();
        dialog.show();
    }

    private String getColorName(String colorCode) {
//        for (int i=0; i < colorCodes.length; i++) {
//            if (colorCodes[i].equals(colorCode)) {
//                return colorNames[i];
//            }
//        }
        
        return null;
    }
    
    public void setColor(String colorCode) {
//        if (!colorCode.equals(chosenColor)) {
//            chosenColor = colorCode;
//            persistString(chosenColor);
//            notifyChanged();
//        }
    }
    
    public String getColor() {
//        if (chosenColor == null) {
//            return this.chosenColor;
//        }
        
        return DEFAULT_COLOR;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }
    
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
//        setColor(restorePersistedValue ? getPersistedString(chosenColor) : (String) defaultValue);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.color = getColor();
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
        setColor(myState.color);
    }

    private static class SavedState extends BaseSavedState {
        String color;

        public SavedState(Parcel source) {
            super(source);
            color = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(color);
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
    
    class AllAppsAdapter extends BaseAdapter {

        private List<ResolveInfo> appInfos;
        private LayoutInflater inflater;

        public AllAppsAdapter() {
            inflater = LayoutInflater.from(context);
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            appInfos = pm.queryIntentActivities(mainIntent, 0);

            Collections.sort(appInfos, new ResolverComparator());
        }
        
        public int getCount() {
            return appInfos.size();
        }

        public Object getItem(int position) {
            return appInfos.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.image_list_item, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.text1);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ResolveInfo info = appInfos.get(position);
            holder.text.setText(info.loadLabel(pm));
            holder.image.setImageDrawable(info.loadIcon(pm));

            return convertView;
        }

    }

    class ViewHolder {
        TextView text;
        ImageView image;
    }

    class ResolverComparator implements Comparator<ResolveInfo> {
        public int compare(ResolveInfo object1, ResolveInfo object2) {
            return object1.loadLabel(pm).toString().compareToIgnoreCase(object2.loadLabel(pm).toString());
        }
    }
}

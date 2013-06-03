package net.bajawa.lib.view;

import java.util.LinkedHashMap;
import java.util.Map;
import net.bajawa.lib.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class CategoryListAdapter extends BaseAdapter {

    public final static int            TYPE_SECTION_HEADER = 0;

    private final Map<String, Adapter> sections            = new LinkedHashMap<String, Adapter>();
    private final ArrayAdapter<String> headers;
    private Context                    context;

    public CategoryListAdapter(Context context) {
        this.context = context;
        headers = new ArrayAdapter<String>(context, R.layout.list_category_item, R.id.list_category_text);
    }
    
    public void removeSection(String section) {
        this.headers.remove(section);
        this.sections.remove(section);
    }

    public void addSection(int stringResId, Adapter adapter) {
        this.addSection(context.getString(stringResId), adapter);
    }
    
    public void addSection(String section, Adapter adapter) {
        if (adapter != null) {
            this.headers.add(section);
            this.sections.put(section, adapter);
            
        } else {
            throw new IllegalArgumentException("Cannot add null adapter!");
        }
    }
    
    public void insertSection(String section, Adapter adapter, int index) {
        if (adapter != null) {
            this.headers.insert(section, index);
            this.sections.put(section, adapter);
            
        } else {
            throw new IllegalArgumentException("Cannot add null adapter!");
        }
    }
    
    public Object getItem(int position) {
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) {
                return section;
            }

            if (position < size) {
                return adapter.getItem(position - 1);
            }

            // otherwise jump into next section
            position -= size;
        }
        return null;
    }

    public int getCount() {
        // total together all sections, plus one for each section header
        int total = 0;
        for(Adapter adapter : this.sections.values()) {
            total += adapter.getCount() + 1;
        }
        return total;
    }

    public int getViewTypeCount() {
        // assume that headers count as one, then total all sections
        int total = 1;
        for(Adapter adapter : this.sections.values()) {
            total += adapter.getViewTypeCount();
        }
        return total;
    }

    public int getItemViewType(int position) {
        int type = 1;
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) {
                return TYPE_SECTION_HEADER;
            }

            if (position < size) {
                return type + adapter.getItemViewType(position - 1);
            }

            // otherwise jump into next section
            position -= size;
            type += adapter.getViewTypeCount();
        }
        return -1;
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionnum = 0;
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) {
                return headers.getView(sectionnum, convertView, parent);
            }

            if (position < size) {
                return adapter.getView(position - 1, convertView, parent);
            }

            // otherwise jump into next section
            position -= size;
            sectionnum++;
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }
}

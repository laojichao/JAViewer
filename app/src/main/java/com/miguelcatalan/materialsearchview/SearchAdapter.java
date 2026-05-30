package com.miguelcatalan.materialsearchview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.javiewer.R;

public class SearchAdapter extends BaseAdapter implements Filterable {

    private final Context context;
    private String[] data;
    private String[] allData;
    private final Drawable suggestionIcon;
    private final boolean ellipsize;
    private ArrayFilter filter;

    public SearchAdapter(Context context, String[] data, Drawable suggestionIcon, boolean ellipsize) {
        this.context = context;
        this.allData = data;
        this.data = data;
        this.suggestionIcon = suggestionIcon;
        this.ellipsize = ellipsize;
    }

    @Override
    public int getCount() {
        return data != null ? data.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return data != null ? data[position] : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.suggest_list_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.suggest_text);
        ImageView imageView = convertView.findViewById(R.id.suggest_icon);

        if (textView != null) {
            textView.setText(data[position]);
            if (ellipsize) {
                textView.setMaxLines(1);
            }
        }

        if (imageView != null) {
            if (suggestionIcon != null) {
                imageView.setImageDrawable(suggestionIcon);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ArrayFilter();
        }
        return filter;
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                synchronized (this) {
                    results.values = allData;
                    results.count = allData.length;
                }
            } else {
                String prefixString = prefix.toString().toLowerCase(Locale.getDefault());
                List<String> newValues = new ArrayList<>();
                for (String value : allData) {
                    if (value.toLowerCase(Locale.getDefault()).contains(prefixString)) {
                        newValues.add(value);
                    }
                }
                results.values = newValues.toArray(new String[0]);
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data = (String[]) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}

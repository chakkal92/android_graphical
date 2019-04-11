package com.gui.gui.ny_times;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gui.gui.R;

import java.util.List;

/***
 * Custom list adapter class
 * Helps to implement a ListView with a custom layout
 * Used for displaying word list on NYSavedFragment and NYSearchFragment
 * */
public class NYNewsListAdapter extends BaseAdapter {

    Context context;
    List<NYNews> newsList;
    static LayoutInflater inflater;

    /***
     * @param context Context for inflating the layout file using layout inflater
     * @param newsList list of NYNews class to display data in each row
     * */
    public NYNewsListAdapter(Context context, List<NYNews> newsList) {
        this.context = context;
        this.newsList = newsList;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_ny_news, parent, false);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvDocType = convertView.findViewById(R.id.tvDocType);
            holder.tvSource = convertView.findViewById(R.id.tvSource);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NYNews news = (NYNews) getItem(position);
        String title = news.getTitle();
        String docType = news.getDocument_type();
        String source = news.getSource();

        holder.tvTitle.setText(title);
        holder.tvDocType.setText(docType);
        holder.tvSource.setText(source);

        return convertView;
    }

    /***
     * ViewHolder pattern is used.
     * */
    static class ViewHolder {
        TextView tvTitle, tvDocType, tvSource;
    }
}

package com.gui.gui.news_feed;

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
 * Used for displaying word list on NFSavedFragment and NFSearchFragment
 * */
public class NFNewsTitlesAdapter extends BaseAdapter {

    private List<NFNewsData> newsData;
    private Context context;
    private static LayoutInflater inflater;

    public NFNewsTitlesAdapter(List<NFNewsData> newsData, Context context) {
        this.newsData = newsData;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return newsData.size();
    }

    @Override
    public Object getItem(int position) {
        return newsData.get(position);
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
            convertView = inflater.inflate(R.layout.nf_item_news, parent, false);
            holder.tvAuthor = convertView.findViewById(R.id.tvAuthor);
            holder.tvNewsTitle = convertView.findViewById(R.id.tvNewsTitle);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        NFNewsData data = (NFNewsData) getItem(position);
        String newsTitle = data.getTitle();
        String authorName = data.getAuthor();
        holder.tvNewsTitle.setText(newsTitle);
        holder.tvAuthor.setText(authorName);

        return convertView;
    }

    static class ViewHolder{
        TextView tvAuthor, tvNewsTitle;
    }
}

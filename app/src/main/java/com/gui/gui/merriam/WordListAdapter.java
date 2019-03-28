package com.gui.gui.merriam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gui.gui.R;

import java.util.ArrayList;

/***
 * Custom list adapter class
 * Helps to implement a ListView with a custom layout
 * Used for displaying word list on MerriamSearchFragment and MerriamSavedFragment
 * */
public class WordListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Word> words;
    private static LayoutInflater inflater;

    public WordListAdapter(Context context, ArrayList<Word> words) {
        this.context = context;
        this.words = words;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return words.size();
    }

    @Override
    public Object getItem(int position) {
        return words.get(position);
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
            convertView = inflater.inflate(R.layout.merriam_item_list, parent, false);
            holder.tvWord = convertView.findViewById(R.id.tvWord);
            holder.tvWordType = convertView.findViewById(R.id.tvWordType);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Word word = (Word) getItem(position);
        String toShow = word.getHw();
        String type = word.getFl();
        holder.tvWord.setText(toShow);
        holder.tvWordType.setText(type);
        return convertView;
    }

    static class ViewHolder {
        TextView tvWord, tvWordType;
    }
}

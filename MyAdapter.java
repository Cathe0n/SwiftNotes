package org.geeksforgeeks.simple_notes_application_java;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class MyAdapter extends ArrayAdapter<Note> {
    private Activity context;
    private List<Note> notes;

    public MyAdapter(@NonNull Activity context, @NonNull List<Note> objects) {
        super(context, R.layout.item_note, objects);
        this.context = context;
        this.notes = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.item_note, parent, false);
            holder = new ViewHolder();
            holder.title = row.findViewById(R.id.noteTitle);
            holder.tag = row.findViewById(R.id.noteTag);
            holder.container = row.findViewById(R.id.noteContainer);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.tag.setText(note.getTag() == null ? "" : ("#" + note.getTag()));

        // set background color rounded - use GradientDrawable
        int color = note.getColor();
        GradientDrawable bg = (GradientDrawable) holder.container.getBackground();
        if (bg == null) {
            bg = new GradientDrawable();
            bg.setCornerRadius(24f);
        }
        bg.setColor(color);
        holder.container.setBackground(bg);

        return row;
    }

    static class ViewHolder {
        TextView title;
        TextView tag;
        View container;
    }
}

package org.geeksforgeeks.simple_notes_application_java;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class NotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* create a simple layout file activity_notes.xml (provided below)
           or adjust IDs if you already have one */
        setContentView(R.layout.activity_notes);

        TextView tvTitle = findViewById(R.id.tvNoteTitle);
        TextView tvTag = findViewById(R.id.tvNoteTag);
        TextView tvContent = findViewById(R.id.tvNoteContent);
        View container = findViewById(R.id.notesContainer);

        String title = getIntent().getStringExtra("TITLE");
        String tag = getIntent().getStringExtra("TAG");
        String content = getIntent().getStringExtra("CONTENT");
        int color = getIntent().getIntExtra("COLOR", 0xFF6B4CB0);

        tvTitle.setText(title == null ? "" : title);
        tvTag.setText(tag == null ? "" : ("#" + tag));
        tvContent.setText(content == null ? "" : content);

        GradientDrawable bg = (GradientDrawable) container.getBackground();
        if (bg == null) {
            bg = new GradientDrawable();
            bg.setCornerRadius(8f);
        }
        bg.setColor(color);
        container.setBackground(bg);
    }
}

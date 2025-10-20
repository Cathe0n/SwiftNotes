package org.geeksforgeeks.simple_notes_application_java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_NOTE = 1;
    private MyAdapter adapter;
    private List<Note> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        Button addNoteButton = findViewById(R.id.add_button);

        items = new ArrayList<>();

        // optional demo note
        items.add(new Note("Temp Add Element", "#temp", "This is temporary content", 0xFF00C6FF));

        adapter = new MyAdapter(this, items);
        listView.setAdapter(adapter);

        // open AddNoteActivity
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
            }
        });

        // open detail when clicking an item
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Note clicked = items.get(position);
            Intent intent = new Intent(MainActivity.this, NotesActivity.class);
            // pass fields
            intent.putExtra("TITLE", clicked.getTitle());
            intent.putExtra("TAG", clicked.getTag());
            intent.putExtra("CONTENT", clicked.getContent());
            intent.putExtra("COLOR", clicked.getColor());
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("NEW_NOTE");
            String tag = data.getStringExtra("NEW_TAG");
            String content = data.getStringExtra("NEW_CONTENT");
            int color = data.getIntExtra("NEW_COLOR", 0xFF6B4CB0); // fallback purple

            if (title != null) {
                Note newNote = new Note(title, tag, content, color);
                items.add(0, newNote); // add to top
                adapter.notifyDataSetChanged();
            }
        }
    }
}

package org.geeksforgeeks.simple_notes_application_java;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";

    public static final String EXTRA_NOTE_CONTENT = "NOTE_CONTENT";
    public static final String EXTRA_UPDATED_CONTENT = "UPDATED_CONTENT";
    // optional: pass back index/id so the caller knows which item to update
    public static final String EXTRA_NOTE_INDEX = "NOTE_INDEX";

    private EditText etContent;
    private Button btnSave;
    private int noteIndex = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure this matches your layout filename
        setContentView(R.layout.activity_detail);

        // find views after setContentView and guard for nulls
        etContent = findViewById(R.id.etDetailContent);
        btnSave = findViewById(R.id.btnSaveDetail);

        if (etContent == null) {
            Log.e(TAG, "etDetailContent view not found! Check activity_detail.xml for id etDetailContent");
            Toast.makeText(this, "Layout error: edit field missing", Toast.LENGTH_LONG).show();
            // avoid further NPEs
            finish();
            return;
        }

        if (btnSave == null) {
            Log.w(TAG, "btnSaveDetail not found — continuing without explicit Save button");
        }

        // Get incoming extras safely
        Intent in = getIntent();
        if (in != null && in.hasExtra(EXTRA_NOTE_CONTENT)) {
            String content = in.getStringExtra(EXTRA_NOTE_CONTENT);
            if (content != null) etContent.setText(content);
        } else {
            Log.i(TAG, "No incoming note content provided. etContent left blank.");
        }

        // optional index
        if (in != null && in.hasExtra(EXTRA_NOTE_INDEX)) {
            noteIndex = in.getIntExtra(EXTRA_NOTE_INDEX, -1);
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnResultAndFinish();
                }
            });
        }
    }

    // When backing out, return the content as well so the caller can update
    @Override
    public void onBackPressed() {
        returnResultAndFinish();
        super.onBackPressed();
    }

    private void returnResultAndFinish() {
        if (etContent == null) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return;
        }
        String updated = etContent.getText().toString();
        Intent out = new Intent();
        out.putExtra(EXTRA_UPDATED_CONTENT, updated);
        if (noteIndex != -1) out.putExtra(EXTRA_NOTE_INDEX, noteIndex);
        setResult(Activity.RESULT_OK, out);
        finish();
    }
}

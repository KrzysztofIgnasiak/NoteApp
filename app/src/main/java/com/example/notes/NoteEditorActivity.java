package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.content.Intent;

import static android.media.CamcorderProfile.get;
import static android.widget.EditText.*;

public class NoteEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        EditText editText = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();
        int noteId = intent.getIntExtra("noteId",-1);

        if(noteId !=-1)
        {
            editText.setText(MainActivity.notes.get(noteId));
        }
    }
}
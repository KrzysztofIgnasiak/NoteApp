package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.content.Intent;

import java.util.HashSet;

import static android.media.CamcorderProfile.get;
import static android.widget.EditText.*;

public class NoteEditorActivity extends AppCompatActivity {
    int noteId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        EditText editText = (EditText) findViewById(R.id.editText); //get connection with editText

        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId",-1); //get id of note

        if(noteId !=-1) //if there is no note
        {
            editText.setText(MainActivity.notes.get(noteId));
        }
        else
        {
            MainActivity.notes.add("");
            noteId = MainActivity.notes.size() -1; //set id
            MainActivity.arrayAdapter.notifyDataSetChanged(); //change text
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                MainActivity.notes.set(noteId,String.valueOf(s)); //adding note
                //TODO add encrypted version
                MainActivity.arrayAdapter.notifyDataSetChanged(); // displaying changes

                SharedPreferences sharedPreferences = getApplicationContext()
                        .getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                HashSet<String> set = new HashSet(MainActivity.notes);
                //initialise shared preferences where notes with be saved as hashSet
                sharedPreferences.edit().putStringSet("notes",set).apply();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
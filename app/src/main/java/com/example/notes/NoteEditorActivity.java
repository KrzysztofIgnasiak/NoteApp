package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;

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
            editText.setText(MainActivity.notes1.get(noteId));
        }
        else
        {
            MainActivity.notes1.add("");
            MainActivity.EncryptedNotes.add("");
            noteId = MainActivity.notes1.size() -1; //set id
            MainActivity.arrayAdapter.notifyDataSetChanged(); //change text
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                MainActivity.notes1.set(noteId,String.valueOf(s)); //adding note
                MainActivity.arrayAdapter.notifyDataSetChanged(); // displaying changes

            }

            @Override
            public void afterTextChanged(Editable s) {

                //initialise iv
                SharedPreferences sharedPreferencesIv = getApplicationContext()
                        .getSharedPreferences("com.example.iv", Context.MODE_PRIVATE);

                //encryption
                //get iv
                String ivString = sharedPreferencesIv.getString("iv",null);
                byte[] iv = Base64.decode(ivString,Base64.NO_WRAP);
                //get encrypted
                String encrypted = NotesSecurity.encryptNote(String.valueOf(s),getApplicationContext(),iv);

                //add
                MainActivity.EncryptedNotes.set(noteId,encrypted);

                SharedPreferences sharedPreferencesEncrypted = getApplicationContext()
                        .getSharedPreferences("com.example.EncryptedNotes", Context.MODE_PRIVATE);

                HashSet<String> set = new HashSet(MainActivity.EncryptedNotes);
                //initialise shared preferences where notes with be saved as hashSet
                sharedPreferencesEncrypted.edit().putStringSet("EncryptedNotes",set).apply();
            }
        });
    }
}
package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

   static ArrayList<String> notes = new ArrayList<>();
   static ArrayList<String> notes1 = new ArrayList<>();
    static ArrayList<String> EncryptedNotes = new ArrayList<>();
   static ArrayAdapter arrayAdapter;

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
       MenuInflater menuInflater = getMenuInflater();
       menuInflater.inflate(R.menu.add_note_menu,menu);

       return super.onCreateOptionsMenu(menu);
   }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.add_note)
        {
            Intent intent = new Intent(getApplicationContext(),NoteEditorActivity.class);
            startActivity(intent);

            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView ListView = (ListView) findViewById(R.id.ListView);

        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
        HashSet<String> set = (HashSet<String>)sharedPreferences.getStringSet("notes",null); //getting notes


        byte[] iv = new  byte[0];
        SharedPreferences sharedPreferencesIv = getApplicationContext()
                .getSharedPreferences("com.example.iv", Context.MODE_PRIVATE);
        String ivString = sharedPreferencesIv.getString("iv",null);

        if(ivString == null) // if iv not exist
        {
            iv = KeyStore_subSystem.GenerateIv(); // create iv
            ivString  =  Base64.encodeToString(iv, Base64.NO_WRAP);
            sharedPreferencesIv.edit().putString("iv", ivString).apply();
        }
        else
        {
            Log.d("sucess","iv form Preferences");
            iv = Base64.decode(ivString,Base64.NO_WRAP);
        }

        //TODO create encryptedList
        //TODO decrypt
        //testing

        if(set == null) {

            notes.add("Example note");
        }
        else
        {
            notes = new ArrayList(set);
            //notes1 = new ArrayList(set);


        }
        //testing
        SharedPreferences sharedPreferencesEncrypted = getApplicationContext()
                .getSharedPreferences("com.example.EncryptedNotes", Context.MODE_PRIVATE);
       // HashSet<String> setEncrypted = (HashSet<String>)sharedPreferences.getStringSet("EncryptedNotes",null); //getting notes
       // setEncrypted = null;

           // for (int i = 0;i<notes.size();i++) {
             //   String Note1 = notes.get(i);
             //   Log.d("before", Note1);

               // Log.d("size", String.valueOf(notes.size()));
                // byte [] iv = KeyStore_subSystem.GenerateIv();
                //String encrypted = NotesSecurity.EncryptNote(Note1, getApplicationContext(), iv);
                //Log.d("after", encrypted);
                //EncryptedNotes.add(encrypted);
                //String backed = NotesSecurity.DecryptNote(encrypted, getApplicationContext(), iv);
                //Log.d("backed", backed);

           // HashSet<String>   setEncrypted = new HashSet(MainActivity.EncryptedNotes);
         //   sharedPreferencesEncrypted.edit().putStringSet("EncryptedNotes",setEncrypted).apply();

        HashSet<String>  setEncrypted = (HashSet<String>)sharedPreferencesEncrypted.getStringSet("EncryptedNotes",null); //getting notes
                if(setEncrypted != null)
                {
                    Log.d("sucess","Not empty");
                }
        EncryptedNotes = new ArrayList(setEncrypted);
        for (int i = 0;i<EncryptedNotes.size();i++)
        {
            String encrypted = EncryptedNotes.get(i);
            String backed = NotesSecurity.DecryptNote(encrypted, getApplicationContext(), iv);
            Log.d("backed", backed);
            notes1.add(backed);
        }




        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, notes1);

        ListView.setAdapter(arrayAdapter);

        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(),NoteEditorActivity.class);
                intent.putExtra("noteId",position);
                startActivity(intent);
            }
        });

        ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // delete notes
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               new AlertDialog.Builder(MainActivity.this)
                       .setIcon(android.R.drawable.ic_dialog_alert)
                       .setTitle("are you sure")
                       .setMessage("Do you want to delete this note").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       notes1.remove(position);
                       EncryptedNotes.remove(position);
                       //Todo encrypted notes remove
                       arrayAdapter.notifyDataSetChanged();

                       SharedPreferences sharedPreferencesEncrypted = getApplicationContext()
                               .getSharedPreferences("com.example.EncryptedNotes", Context.MODE_PRIVATE);
                       HashSet<String> set = new HashSet(MainActivity.EncryptedNotes);

                       sharedPreferencesEncrypted.edit().putStringSet("EncryptedNotes",set).apply();
                   }
               }).setNegativeButton("No",null)
                       .show();


                return true;
            }
        });

    }
}
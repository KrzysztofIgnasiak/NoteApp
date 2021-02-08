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
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

   static ArrayList<String> notes = new ArrayList<>();
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


        byte[] iv = new  byte[0];
        SharedPreferences sharedPreferencesIv = getApplicationContext()
                .getSharedPreferences("com.example.iv", Context.MODE_PRIVATE);
        String ivString = sharedPreferencesIv.getString("iv",null);

        if(ivString == null) // if iv not exist
        {
            iv = keyStoreSubSystem.generateIv(); // create iv
            ivString  =  Base64.encodeToString(iv, Base64.NO_WRAP); //to string
            sharedPreferencesIv.edit().putString("iv", ivString).apply(); //save
        }
        else
        {
            iv = Base64.decode(ivString,Base64.NO_WRAP); // to bits
        }

        //get encrypted notes
        SharedPreferences sharedPreferencesEncrypted = getApplicationContext()
                .getSharedPreferences("com.example.EncryptedNotes", Context.MODE_PRIVATE);


        HashSet<String>  setEncrypted = (HashSet<String>)sharedPreferencesEncrypted.getStringSet("EncryptedNotes",null); //getting notes

                if(setEncrypted == null)
                {
                    String example = "Example Note"; //create example note
                    String encrypted = NotesSecurity.encryptNote(example,getApplicationContext(),iv);
                    EncryptedNotes.add(encrypted);
                }
                else
                {
                    EncryptedNotes = new ArrayList(setEncrypted); //read encrypted notes
                }

        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0;i<EncryptedNotes.size();i++) //decrypt notes
        {
            String encrypted = EncryptedNotes.get(i);
            String backed = NotesSecurity.decryptNote(encrypted, getApplicationContext(), iv);
            temp.add(backed);
        }
        notes = temp; //load decrypted




        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, notes);//create adapter

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
                       notes.remove(position); //remove note
                       EncryptedNotes.remove(position); //remove encrypted note
                       //Todo encrypted notes remove
                       arrayAdapter.notifyDataSetChanged();

                       //update encrypted notes in shared preferences
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
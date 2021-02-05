package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Bundle;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

   static ArrayList<String> notes = new ArrayList<>();
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


        //TODO create encryptedList
        //TODO decrypt

        if(set == null) {

            notes.add("Example note");
        }
        else
        {
            notes = new ArrayList(set);
        }

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, notes);

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
                       notes.remove(position);
                       arrayAdapter.notifyDataSetChanged();

                       SharedPreferences sharedPreferences = getApplicationContext()
                               .getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                       HashSet<String> set = new HashSet(MainActivity.notes);

                       sharedPreferences.edit().putStringSet("notes",set).apply();
                   }
               }).setNegativeButton("No",null)
                       .show();


                return true;
            }
        });

    }
}
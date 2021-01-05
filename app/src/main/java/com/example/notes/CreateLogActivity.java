package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import java.util.Optional;

public class CreateLogActivity extends AppCompatActivity {

    EditText etNewUser;
    EditText etNewPassword;
    Button btCreateLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_log);

        btCreateLog = findViewById(R.id.bt_add_log); //get connection with button
        btCreateLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNewUser = findViewById(R.id.et_username_create); //get connections with EditTexts
                etNewPassword = findViewById(R.id.et_password_create);

                String newUser = etNewUser.getText().toString(); //get new username
                String newPassword = etNewPassword.getText().toString(); //get new password

                Optional<String> saltOptional = Security.generateSalt(512); //create random salt
                String salt = saltOptional.orElse("");

                Optional<String> hashNewPasswordOptional = Security.hashPassword(newPassword,salt); //hash new password
                String hashNewPassword = hashNewPasswordOptional.orElse("");

                SharedPreferences pref = getSharedPreferences("com.example.notes.utilities",
                        Context.MODE_PRIVATE); // initialise shared preferences, where password
                //username and salt will be stored

                SharedPreferences.Editor editor = pref.edit();

                editor.putString("LogIn",newUser); //save password, username and salt
                editor.putString("salt",salt);
                editor.putString("Password",hashNewPassword);

                editor.apply();
            }
        });

    }
}
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

        btCreateLog = findViewById(R.id.bt_add_log);
        btCreateLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNewUser = findViewById(R.id.et_username_create);
                etNewPassword = findViewById(R.id.et_password_create);

                String newUser = etNewUser.getText().toString();
                String newPassword = etNewPassword.getText().toString();

                Optional<String> saltOptional = Security.generateSalt(512);
                String salt = saltOptional.orElse("");

                Optional<String> hashNewPasswordOptional = Security.hashPassword(newPassword,salt);
                String hashNewPassword = hashNewPasswordOptional.orElse("");

                SharedPreferences pref = getSharedPreferences("com.example.notes.utilities",
                        Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = pref.edit();

                editor.putString("LogIn",newUser);
                editor.putString("salt",salt);
                editor.putString("Password",hashNewPassword);

                editor.apply();
            }
        });

    }
}
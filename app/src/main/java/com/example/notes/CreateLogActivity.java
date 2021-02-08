package com.example.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
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

                //keystore encryption
                //user
                byte [] newUserBytes = new byte[0];
                try {
                    newUserBytes = newUser.getBytes("UTF-8"); // to bytes
                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }

                EncryptHandler HandlerUser = keyStoreSubSystem.encryptPassword(getApplicationContext(),newUserBytes); //encrypt
                byte [] newUserEncryptedBytes = HandlerUser.getEncrypted();
                byte [] newUserIv = HandlerUser.getIv();

                String newUserEncrypted = Base64.encodeToString(newUserEncryptedBytes, Base64.NO_WRAP); // to string
                String newUserIvString =  Base64.encodeToString(newUserIv, Base64.NO_WRAP); //encode iv as string

                //password
                byte [] newPasswordBytes = new byte[0];
                try {
                    newPasswordBytes = hashNewPassword.getBytes("UTF-8");
                }
                catch(UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                EncryptHandler handlerPassword = keyStoreSubSystem.encryptPassword(getApplicationContext(),newPasswordBytes); //encrypt
                byte [] newPasswordEncryptedBytes = handlerPassword.getEncrypted();
                byte [] newPasswordIv = handlerPassword.getIv();

                String newPasswordEncrypted = Base64.encodeToString(newPasswordEncryptedBytes, Base64.NO_WRAP); // to string
                String newPasswordIvString =  Base64.encodeToString(newPasswordIv, Base64.NO_WRAP); //encode iv as string



                SharedPreferences.Editor editor = pref.edit();

                editor.putString("LogIn",newUserEncrypted); //save password, username, salt and ives
                editor.putString("salt",salt);
                editor.putString("Password",newPasswordEncrypted);
                editor.putString("UserIv",newUserIvString);
                editor.putString("PasswordIv",newPasswordIvString);


                editor.apply();
            }
        });

    }
}
package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
//import android.hardware.biometrics.BiometricManager;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.concurrent.Executor;

public class LogOnActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btSubmit;
    Button btFinger;
    Button btCreate;
    private final int maxAttempts = 4;
    private int attempt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_on);

        etUsername = findViewById(R.id.et_username); //getting connections with buttons
        etPassword = findViewById(R.id.et_password);  //and editTexts
        btSubmit = findViewById(R.id.bt_submit);
        btCreate = findViewById(R.id.bt_create);

        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CreateLogActivity.class);
                startActivity(intent); //go to CreateLogActivity
            }
        });


        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(attempt <maxAttempts)
                {
                    SharedPreferences pref = getSharedPreferences("com.example.notes.utilities",
                            Context.MODE_PRIVATE); //initialise shared preferences, where login, password
                    // and salt is stored

                    String passwordEncrypted = pref.getString("Password",null); //get stored password
                    String salt = pref.getString("salt",null); // get stored string
                    String userNameEncrypted = pref.getString("LogIn",null); // get stored username
                    String userIv = pref.getString("userIv",null);
                    String passwordIv = pref.getString("passwordIv",null);


                    String tryPassword = etPassword.getText().toString(); // get password from user

                    if(userNameEncrypted.isEmpty() ||salt.isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),
                                "you have to create username and password",Toast.LENGTH_SHORT).show();
                    }
                    //decrypt with keystore
                    //everything to bytes
                    byte [] userIvBytes = Base64.decode(userIv,Base64.NO_WRAP);
                    byte [] passwordIvBytes = Base64.decode(passwordIv,Base64.NO_WRAP);
                    byte [] userNameEncryptedBytes = Base64.decode(userNameEncrypted,Base64.NO_WRAP);
                    byte [] passwordEncryptedBytes = Base64.decode(passwordEncrypted,Base64.NO_WRAP);

                    //decrypt
                    byte [] userBytes = keyStoreSubSystem.decryptPassword(userNameEncryptedBytes,getApplicationContext(),userIvBytes);
                    byte [] hashOryginalPasswordBytes = keyStoreSubSystem.decryptPassword(passwordEncryptedBytes,getApplicationContext(),passwordIvBytes);

                    //back to string
                    String userName = null;
                    String hashOryginalPassword = null;
                    try {
                        userName = new String(userBytes, "UTF-8");

                    } catch ( UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        hashOryginalPassword = new String(hashOryginalPasswordBytes, "UTF-8");
                    } catch ( UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    // hashing try Password
                    Optional<String> tryHashPasswordOptional = Security.hashPassword(tryPassword,salt);//hash password received from user

                    String tryHashPassword = tryHashPasswordOptional.orElse("");

                    //compare
                    if(etUsername.getText().toString().equals(userName) &&
                            hashOryginalPassword.equals(tryHashPassword)) // check whether username and password are correct
                    {
                        etUsername.setText("");
                        etPassword.setText("");
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent); // go to MainActivity
                    }
                    else
                    {
                        etUsername.setText("");
                        etPassword.setText("");
                        Toast.makeText(getApplicationContext(),
                                "invalid Username or Password",Toast.LENGTH_SHORT).show();
                        attempt++;
                    }
                }
                else
                {
                    System.exit(0);
                }
            }

        });

        btFinger = findViewById(R.id.login_btn);

        BiometricManager biometricManager;
        biometricManager = BiometricManager.from(this);


        Executor executor = ContextCompat.getMainExecutor(this); // an object that executes submitted runnable tasks.

        //initialise and use class that manages a system-provided biometric info
        BiometricPrompt biometricPrompt = new BiometricPrompt(LogOnActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class); //if authentication success then go to Main actvitiy
                  startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("login")
                .setDescription("Use your fingerprint to login to your app")
                .setNegativeButtonText("Cancel")
                .build();

        btFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(biometricManager.canAuthenticate()) //checking if it is possible to use sensors
                {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        biometricPrompt.authenticate(promptInfo);
                        // Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        //  startActivity(intent);
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                        Toast.makeText(getApplicationContext(),
                                "the device don't have a fingerprint sensor",Toast.LENGTH_SHORT).show();
                        btFinger.setVisibility(View.GONE);
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                        Toast.makeText(getApplicationContext(),
                                "the biometric sensors in currently unavailable",Toast.LENGTH_SHORT).show();
                        btFinger.setVisibility(View.GONE);
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        Toast.makeText(getApplicationContext(),
                                "your device don't have any fingerprint saved",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }


}
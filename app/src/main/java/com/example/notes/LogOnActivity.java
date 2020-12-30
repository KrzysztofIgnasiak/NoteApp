package com.example.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class LogOnActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_on);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btSubmit = findViewById(R.id.bt_submit);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etUsername.getText().toString().equals("admin") &&
                etPassword.getText().toString().equals("admin"))
                {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                   // AlertDialog.Builder builder = new AlertDialog.Builder(LogOnActivity.this);

                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "invalid Username or Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
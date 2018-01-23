package com.twechy.twechy_v2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twechy.twechy_v2.Database.Login_Helper;

public class LoginActivity extends AppCompatActivity {

    private EditText userName, password;
    private TextView login, register;
    private Login_Helper helper;

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.loginUserNameTxt);
        password = findViewById(R.id.loginPasswordTxt);

        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.btnRegister);


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void registerUser(View view) {

        startActivity(new Intent(getApplicationContext(), RegisterFromDbActivity.class));
    }

    public void loginUser(View view) {

        String user = userName.getText().toString();
        String pass = password.getText().toString();

        if ( user.trim().isEmpty() || pass.trim().isEmpty() ){
            Toast.makeText(this, "Please Enter your User Name or Password !!", Toast.LENGTH_SHORT).show();
        }else {

            if (helper.getUser(user, pass)){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }

    }
}

package com.twechy.twechy_v2.Login_Registration;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.twechy.twechy_v2.R;

public class RegisterActivity extends AppCompatActivity{

    EditText userName, password;
    TextView  register;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        auth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.userNameReg);
        password = findViewById(R.id.passwordReg);
        register = findViewById(R.id.registerUser);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = userName.getText().toString();
                String pass = password.getText().toString();


                final ProgressDialog dialog = ProgressDialog.show(RegisterActivity.this, "Please Wait...", "Processing...", true);
                auth.createUserWithEmailAndPassword(name, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        dialog.dismiss();
                        if (task.isSuccessful()) {

                            Toast.makeText(RegisterActivity.this, "Registration Successful..", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}

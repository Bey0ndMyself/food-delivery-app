package com.example.hp.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hp.myapplication.Common.Common;
import com.example.hp.myapplication.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText edtMail;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtMail = findViewById(R.id.edtMail);
        edtPassword = findViewById(R.id.edtPassword);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent home = new Intent(SignIn.this, Home.class);
                    Common.currentUser = new User(edtMail.getText().toString(), edtPassword.getText().toString());
                    startActivity(home);
                    finish();
                }
            }
        };

        findViewById(R.id.btnSignIn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mAuth.signInWithEmailAndPassword(edtMail.getText().toString(), edtPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignIn.this, "Aвторизация успешна", Toast.LENGTH_SHORT).show();
                            Intent home = new Intent(SignIn.this, Home.class);
                            Common.currentUser = new User(edtMail.getText().toString(), edtPassword.getText().toString());
                            startActivity(home);
                            finish();
                        } else
                            Toast.makeText(SignIn.this, "Aвторизация провалена", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

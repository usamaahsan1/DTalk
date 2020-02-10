package com.androidcave.dtalk.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcave.dtalk.ConversationActivity;
import com.androidcave.dtalk.MainUsersActivity;
import com.androidcave.dtalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    EditText edEmail, edPass;
    TextView tvForgetPass, tvSignUpLink;
    Button btnSignIn;
    //ProgressBar progressBarLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);
        // intialize app before calling any firebase instance
       // FirebaseApp.initializeApp(this);


        edEmail = findViewById(R.id.login_email);
        edPass = findViewById(R.id.login_password);
        tvForgetPass = findViewById(R.id.login_forget_pass);
        btnSignIn = findViewById(R.id.login_btn);
        tvSignUpLink = findViewById(R.id.login_signup_link);
        //progressBarLogin = findViewById(R.id.pbLoadingLogin);



        mAuth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edEmail.getText().toString();
                String password = edPass.getText().toString();

                if (email.isEmpty()){
                    edEmail.setError("Please enter a valid email.");
                }
                else if (password.isEmpty()){
                    edPass.setError("Please enter your valid password.");
                }
                else {

                    final AlertDialog dialog = new SpotsDialog.Builder().setContext(LoginActivity.this).build();
                    dialog.setMessage("Authenticating...");
                    dialog.show();

                    //progressBarLogin.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){
                                        sendToMainUsersActivity();
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "Error: " +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    dialog.dismiss();
                                    //progressBarLogin.setVisibility(View.INVISIBLE);
                                }
                            });

                }


            }
        });

        tvForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                finish();
            }
        });

        tvSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check the user status if already logged in
        FirebaseUser crntUser = mAuth.getCurrentUser();
        if (crntUser != null){
            sendToMainUsersActivity();
        }

    }

    private void sendToMainUsersActivity() {
        startActivity(new Intent(LoginActivity.this, MainUsersActivity.class));
        finish();
    }
}

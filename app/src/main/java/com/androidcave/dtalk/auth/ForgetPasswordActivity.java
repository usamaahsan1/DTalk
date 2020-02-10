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

import com.androidcave.dtalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText edEmail;
    Button btnPassReset;
    TextView tvBackLink;
    //ProgressBar progressBarFrgtPass;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forget_password);

        edEmail = findViewById(R.id.frgt_pass_email);
        btnPassReset = findViewById(R.id.frgt_pass_btn);
        tvBackLink = findViewById(R.id.frgt_pass_login_link);
        //progressBarFrgtPass = findViewById(R.id.pbLoadingFrgtPass);

        mAuth = FirebaseAuth.getInstance();

        btnPassReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edEmail.getText().toString();

                if (email.isEmpty()){
                    edEmail.setError("Invalid email");
                }
                else {

                    final AlertDialog dialog = new SpotsDialog.Builder().setContext(ForgetPasswordActivity.this ).build();
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    //progressBarFrgtPass.setVisibility(View.VISIBLE);

                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(ForgetPasswordActivity.this, "A password reset email has been sent to your. Please check out you inbox.", Toast.LENGTH_SHORT).show();
                                    }

                                    else {
                                        Toast.makeText(ForgetPasswordActivity.this, "There is some problem in sending email. Please check out your internet connection.", Toast.LENGTH_SHORT).show();
                                    }

                                    dialog.dismiss();
                                    //progressBarFrgtPass.setVisibility(View.INVISIBLE);
                                }
                            });
                }


            }
        });

        tvBackLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                finish();
            }
        });

    }
}

package com.androidcave.dtalk.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcave.dtalk.R;
import com.androidcave.dtalk.profile.SetupProfileActivity;
import com.androidcave.dtalk.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    EditText edName,edEmail,edPass,edConPass;
    CheckBox chTerms;
    Button btnSignUp;
    //ProgressBar pbLoading;
    TextView tvSignInLink, tvRegPolicies;

    FirebaseAuth authReg;
    DatabaseReference usersDbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        
        edName=findViewById(R.id.reg_username);
        edEmail=findViewById(R.id.reg_email);
        edPass=findViewById(R.id.reg_password);
        edConPass=findViewById(R.id.reg_confirm_password);
        chTerms=findViewById(R.id.reg_checkbox);
        btnSignUp=findViewById(R.id.signup_btn);
        tvSignInLink=findViewById(R.id.reg_login_link);
        tvRegPolicies = findViewById(R.id.reg_policies);
        //pbLoading=findViewById(R.id.pbLoadingReg);


        authReg=FirebaseAuth.getInstance();
        usersDbReference=FirebaseDatabase.getInstance().getReference().child("Users");
        
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=edName.getText().toString();
                String email=edEmail.getText().toString();
                String password=edPass.getText().toString();
                String confirmPassword=edConPass.getText().toString();

                if (name.isEmpty()){
                    edName.setError("Please enter name first");
                }else if (email.isEmpty()){
                    edEmail.setError("Please enter email first");
                }else if (!email.contains("@")){
                    edEmail.setError("Please enter valid email");
                }else if (password.isEmpty()){
                    edPass.setError("Please enter password");
                }else if (password.length()<7){
                    edPass.setError("please enter password of minimum 8 characters");
                }else if (!confirmPassword.equals(password)){
                    edConPass.setError("Password does not match");
                }else if (!chTerms.isChecked()){
                    chTerms.setError("Please accept terms and conditions first");
                }else {
                    //TODO(2): here the server code run for push data to server

                    final AlertDialog dialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).build();
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    //pbLoading.setVisibility(View.VISIBLE);

                  authReg.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {

                          dialog.dismiss();
                          //pbLoading.setVisibility(View.GONE);

                          if (!task.isSuccessful()) {
                              Toast.makeText(RegisterActivity.this, "Registration failed, Check your Network Connection!", Toast.LENGTH_SHORT).show();
                          } else {
                              onAuthSuccess(task.getResult().getUser());
                              //Toast.makeText(RegisterActivity.this, "we will make pass of login here", Toast.LENGTH_SHORT).show();
                              //finish();
                          }
                      }
                  });

                }

            }
        });

        tvSignInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        tvRegPolicies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Popup wd polocies with scrollview
                Toast.makeText(RegisterActivity.this, "Check the comment in java source and add it please.", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void onAuthSuccess(FirebaseUser user) {

                writeNewUser(user.getUid(),edName.getText().toString(),user.getEmail(),null);
                startActivity(new Intent(RegisterActivity.this, SetupProfileActivity.class));
                finish();
    }

    private void writeNewUser(String userId, String name, String email, String profilePicUrl) {

        User user=new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),name,email,0,null,null,null);
       // User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),name, profilePicUrl,email,null,null);

        usersDbReference.child(userId).setValue(user);


    }
}

package com.androidcave.dtalk.profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcave.dtalk.R;
import com.androidcave.dtalk.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import dmax.dialog.SpotsDialog;

public class SetupProfileActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 22;

    ImageButton slctPic;
    ImageView selectedProfileImg;
    TextView updatePass, delAccount;
    TextInputEditText bio, userName, fullName, email, phone, dob;
    Spinner country, gender, catDeafNor;
    Button btnDone;

    android.app.AlertDialog dialog;

    private Uri selectedProfileImageUri;
    private User currentUser;

    DatabaseReference currentUserDBRef;
    ValueEventListener getCurrentUserInfo;
    StorageReference usersProfileStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        slctPic = findViewById(R.id.NewMessages);

        selectedProfileImg = findViewById(R.id.stp_pro_image);
        updatePass = findViewById(R.id.stp_pro_update_pass);
        delAccount = findViewById(R.id.stp_pro_delete);
        bio = findViewById(R.id.stp_pro_bio);
        btnDone=findViewById(R.id.btnDoneSetupProfile);
        userName = findViewById(R.id.stp_pro_username);
        //fullName = findViewById(R.id.stp_pro_full_name);
        email = findViewById(R.id.stp_pro_email);
        //phone = findViewById(R.id.stp_pro_phone);
        //dob = findViewById(R.id.stp_pro_dob);
        country = findViewById(R.id.stp_pro_country);
        gender = findViewById(R.id.stp_pro_gender);
        catDeafNor = findViewById(R.id.stp_pro_deaf_or_normal);

        dialog = new SpotsDialog.Builder().setContext(SetupProfileActivity.this).build();
        dialog.setMessage("Updating...");


        currentUserDBRef=FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());

      // Toast.makeText(this, "current user is:"+currentUserDBRef.toString(), Toast.LENGTH_SHORT).show();

        usersProfileStorageRef= FirebaseStorage.getInstance().getReference().child("ProfilePictures");

        getCurrentUserInfo=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              currentUser = dataSnapshot.getValue(User.class);

                userName.setText(currentUser.getUserName());
                email.setText(currentUser.getUserEmail());

                if (currentUser.getUserType()!=null){

                if (currentUser.getUserType().equals("Deaf")){
                    catDeafNor.setSelection(0);
                }else {
                    catDeafNor.setSelection(1);
                }
                if (currentUser.getGender().equals("Male")){
                    catDeafNor.setSelection(0);
                }else {
                    catDeafNor.setSelection(1);
                }
                if (currentUser.getUserCountry().equals("Pakistan")){
                    catDeafNor.setSelection(0);
                }else {
                    catDeafNor.setSelection(1);
                }

                bio.setText(currentUser.getUserBio());
                    }

                //Toast.makeText(SetupProfileActivity.this, "URL: "+currentUser.getUserProfileImgURL(), Toast.LENGTH_SHORT).show();
                if (currentUser.getUserProfileImgURL()!=null){
                    Glide.with(getApplicationContext()).load(currentUser.getUserProfileImgURL())
                            .into(selectedProfileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        currentUserDBRef.addValueEventListener(getCurrentUserInfo);

            if (currentUser != null){
         userName.setText(currentUser.getUserName());
         email.setText(currentUser.getUserEmail());
            }



        slctPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });





            btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentUser =new User(FirebaseAuth.getInstance().getUid().toString(),
                            userName.getText().toString(),
                            email.getText().toString(),
                            null,
                            null,
                            null,
                            null,
                            gender.getSelectedItem().toString(),
                            bio.getText().toString(),
                            country.getSelectedItem().toString(),
                            catDeafNor.getSelectedItem().toString(),
                            0);

                    // first we upload image to server storage then store values to db

                     addImgToStorage();


                    dialog.show();

                }
            });

            delAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle("Delete account?");
                    builder.setMessage("You are about to delete your account. Do you really want to proceed?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Delete user from database
                            Toast.makeText(SetupProfileActivity.this, "User deleted successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //No action
                            Toast.makeText(SetupProfileActivity.this, "You have changed your mind!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.show();

                }
            });
    }

    private void addImgToStorage() {
        if (selectedProfileImageUri==null){
            Toast.makeText(this, "Please select profile image first", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
        else {


    final StorageReference profilePicRef=usersProfileStorageRef.child(selectedProfileImageUri.getLastPathSegment());

        profilePicRef.putFile(selectedProfileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUri = uri;

                        currentUser.setUserProfileImgURL(downloadUri.toString());

                        currentUserDBRef.setValue(currentUser);
                    }
                });




                startActivity(new Intent(SetupProfileActivity.this,ProfileActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetupProfileActivity.this, "Please check network connection", Toast.LENGTH_SHORT).show();
            }
        });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            selectedProfileImageUri = selectedImageUri;
            selectedProfileImg.setImageURI(selectedImageUri);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SetupProfileActivity.this,ProfileActivity.class));
        finish();

    }
}

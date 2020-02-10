package com.androidcave.dtalk.profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidcave.dtalk.R;
import com.androidcave.dtalk.models.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName,tvEmail,tvBio, tvStatus;
    CircleImageView imgProfile;
    Button btnEditProfile;

    DatabaseReference currentUserDBRef;
    ValueEventListener getCurrentUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName=findViewById(R.id.profileName);
        tvEmail=findViewById(R.id.profileEmail);
        tvBio=findViewById(R.id.profileStatus);
        tvStatus = findViewById(R.id.userStatus);
        imgProfile=findViewById(R.id.ivProfileImage);

        btnEditProfile=findViewById(R.id.btnEdtProfile);

        currentUserDBRef= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());



        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,SetupProfileActivity.class));
                finish();

            }
        });


        getCurrentUserInfo=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                tvName.setText(user.getUserName());
                tvEmail.setText(user.getUserEmail());
                tvBio.setText(user.getUserBio());
                tvStatus.setText(user.getUserCatagory());




                if (user.getUserProfileImgURL()!=null){
                Glide.with(getApplicationContext())
                        .load(user.getUserProfileImgURL())
                        .into(imgProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        currentUserDBRef.addValueEventListener(getCurrentUserInfo);

    }
}

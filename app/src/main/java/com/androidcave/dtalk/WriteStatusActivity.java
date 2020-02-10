package com.androidcave.dtalk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcave.dtalk.models.Status;
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

import java.util.ArrayList;
import java.util.List;

public class WriteStatusActivity extends AppCompatActivity {
    private static final int RC_PHOTO_PICKER =22 ;
    ImageView imageStatus;
    EditText edStatusText;
    Button btnGetImage,btnSend;
    ProgressBar pbbStatusUpload;
    ImageView imgUserProfile;
    TextView txtUserProfile;

    Uri selectedStatusImageUri;
    User currentUser;
    DatabaseReference mStatusDbRef;
    DatabaseReference mUsersDbRef;
    StorageReference mStatusStorageRef;
    ValueEventListener getCurrentUserListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_status);

        imageStatus=findViewById(R.id.img_write_status);
        edStatusText=findViewById(R.id.edtTxt_write_status);
        btnGetImage=findViewById(R.id.btn_import_gallery);
        btnSend=findViewById(R.id.btn_send_status);
        pbbStatusUpload=findViewById(R.id.pbStatusUpload);
        imgUserProfile=findViewById(R.id.img_user_profile);
        txtUserProfile=findViewById(R.id.txt_user_profile);


        mStatusDbRef= FirebaseDatabase.getInstance().getReference().child("Statuses");
        mUsersDbRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mStatusStorageRef= FirebaseStorage.getInstance().getReference().child("StateusPics");


        getCurrentUserListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
//              Toast.makeText(WriteStatusActivity.this, currentUser.getProfileImgUrl(), Toast.LENGTH_SHORT).show();
                if (currentUser.getUserProfileImgURL()!=null){

                    Glide.with(imageStatus.getContext()).load(currentUser.getUserProfileImgURL()).into(imgUserProfile);
                }
                txtUserProfile.setText(currentUser.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUsersDbRef.child(currentUid).addValueEventListener(getCurrentUserListener);





        btnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stateTxt= edStatusText.getText().toString();
                if (stateTxt.isEmpty() && selectedStatusImageUri == null){
                    Toast.makeText(WriteStatusActivity.this, "Please enter image or text", Toast.LENGTH_SHORT).show();
                }else {
                    pbbStatusUpload.setVisibility(View.VISIBLE);
                    final String pId = mStatusDbRef.push().getKey();
                    //String imgUri = selectedStatusImageUri.toString();
                    String statusTxt = edStatusText.getText().toString();
                    List<String> likedBy = new ArrayList<>();
                    final Status status = new Status(pId,null,statusTxt,0,null,FirebaseAuth.getInstance().getCurrentUser().getUid());

                    if (selectedStatusImageUri != null){
                        final StorageReference statePicRef = mStatusStorageRef.child(selectedStatusImageUri.getLastPathSegment());

                        statePicRef.putFile(selectedStatusImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                statePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        status.setStatusImgUrl(uri.toString());
                                        // Toast.makeText(WriteStatusActivity.this, "Image uploaded no data storing", Toast.LENGTH_SHORT).show();
                                        mStatusDbRef.child(pId).setValue(status);
                                        pbbStatusUpload.setVisibility(View.INVISIBLE);
                                        finish();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pbbStatusUpload.setVisibility(View.INVISIBLE);
                                Toast.makeText(WriteStatusActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        mStatusDbRef.child(pId).setValue(status);
                        pbbStatusUpload.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(WriteStatusActivity.this,MainUsersActivity.class));
                    }




                }
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedUri = data.getData();
            selectedStatusImageUri = selectedUri;
            imageStatus.setImageURI(selectedStatusImageUri);
            imageStatus.setVisibility(View.VISIBLE);
        }
    }
}

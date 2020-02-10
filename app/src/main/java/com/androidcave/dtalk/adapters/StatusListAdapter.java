package com.androidcave.dtalk.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcave.dtalk.R;
import com.androidcave.dtalk.models.Status;
import com.androidcave.dtalk.models.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StatusListAdapter extends RecyclerView.Adapter<StatusListAdapter.StatusViewHolder> {
   private List<Status> statusList;
   private Context context;


   private DatabaseReference mStatusDbRef;
   private ValueEventListener mGetLikersListListener;

    public StatusListAdapter(List<Status> statusList, Context context) {
        this.statusList = statusList;
        this.context = context;
        mStatusDbRef = FirebaseDatabase.getInstance().getReference().child("Statuses");
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.status_item,viewGroup,false);
        StatusViewHolder viewHolder = new StatusViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder statusViewHolder, int i) {
        statusViewHolder.bind(statusList.get(i));
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder{
    ImageView imgStatus,imgPosterProfile;
    TextView tvTxtStatus,tvTotalLikes,tvPosterName;
    Button btnLike;

      public StatusViewHolder(@NonNull View itemView) {
          super(itemView);

          imgStatus=itemView.findViewById(R.id.img_status);
          imgPosterProfile=itemView.findViewById(R.id.img_status_profile_state);
          tvTxtStatus=itemView.findViewById(R.id.txt_status);
          tvPosterName=itemView.findViewById(R.id.txt_profile_user_state);
          btnLike=itemView.findViewById(R.id.btn_like);
          tvTotalLikes=itemView.findViewById(R.id.txt_total_likes);



      }

      void bind(final Status status){
          tvTxtStatus.setText(status.getStatusText());

          DatabaseReference mUsersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");

          ValueEventListener mGetUser = new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  User user = dataSnapshot.getValue(User.class);

                  if (user.getUserProfileImgURL()!=null){

                      Glide.with(imgPosterProfile.getContext()).load(user.getUserProfileImgURL()).into(imgPosterProfile);
                  }
                  tvPosterName.setText(user.getUserName());
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          };
          mUsersDbRef.child(status.getPosterId()).addValueEventListener(mGetUser);


         //TODO: now work for checking already liked or not

                    String currentUid= FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (status.getLikedBy()!=null){

                  if (status.getLikedBy().containsValue(currentUid)){
                      Drawable img = context.getResources().getDrawable(R.drawable.ic_favorite_red_24dp);
                 btnLike.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
                 btnLike.setText("Liked");
                 btnLike.setTextColor(context.getResources().getColor(R.color.iconLikeColor));
                 btnLike.setEnabled(false);
                  } else {
                      Drawable img = context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp);
                      btnLike.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
                      btnLike.setText("Like");
                      btnLike.setTextColor(Color.BLACK);
                  }
                }


          // Here the checking for likes is finished

          String totalLikes = String.valueOf(status.getTotalLikes())+" Persons liked";
          tvTotalLikes.setText(totalLikes);

          if (status.getStatusImgUrl()!=null){
              imgStatus.setVisibility(View.VISIBLE);
              Glide.with(imgStatus.getContext()).load(status.getStatusImgUrl()).into(imgStatus);
          }







          btnLike.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  //TODO: CHANGE icon and update total likes
                  String cUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                 mStatusDbRef.child(status.getStatusId()).child("likedBy").push().setValue(cUid);

                 int totalLikes = status.getTotalLikes()+1;
                 mStatusDbRef.child(status.getStatusId()).child("totalLikes").setValue(totalLikes);


                  Drawable img = context.getResources().getDrawable(R.drawable.ic_favorite_red_24dp);
                 btnLike.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
                 btnLike.setText("Liked");
                 btnLike.setTextColor(Color.BLUE);






              }
          });
      }
  }
}

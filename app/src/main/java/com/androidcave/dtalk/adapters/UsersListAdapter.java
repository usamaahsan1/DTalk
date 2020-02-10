package com.androidcave.dtalk.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcave.dtalk.R;
import com.androidcave.dtalk.models.Message;
import com.androidcave.dtalk.models.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersListViewHolder> {
    List<User> userList;
    Context context;

    final private ListItemClickListner mOnClickListner;

    public interface ListItemClickListner{
        void onListItemClick(User clickedUser);
    }

    public UsersListAdapter(List<User> userList,ListItemClickListner itemClickListner, Context context) {
        this.userList = userList;
        mOnClickListner=itemClickListner;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context=viewGroup.getContext();
        int LayoutInflatorId=R.layout.user_item;
        LayoutInflater inflater=LayoutInflater.from(context);
        boolean shouldAttachToParentImediately=false;
        View view=inflater.inflate(LayoutInflatorId,viewGroup,shouldAttachToParentImediately);
        UsersListViewHolder profilesViewHolder=new UsersListViewHolder(view);
        return profilesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListViewHolder usersListViewHolder, int i) {
        User user = this.userList.get(i);
        usersListViewHolder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView unReadMsgs,userName,userLastMsg,lastTimeDate;
    CircleImageView userProfileImg;
     public UsersListViewHolder(@NonNull View itemView) {
         super(itemView);

        unReadMsgs=itemView.findViewById(R.id.NewMessages);
         userName=itemView.findViewById(R.id.UserName);
         userLastMsg=itemView.findViewById(R.id.UserLastMsg);
         lastTimeDate=itemView.findViewById(R.id.UserLastTimeDate);
         userProfileImg=itemView.findViewById(R.id.UserProfileImage);
         itemView.setOnClickListener(this);
     }

     void bind(User user){
       if (user.getUnreadMessages()==0){
           unReadMsgs.setVisibility(View.GONE);
       } else {
      unReadMsgs.setText(String.valueOf(user.getUnreadMessages()));
       }
      userName.setText(user.getUserName());
      userLastMsg.setText(user.getUserLastMsg());
     // lastTimeDate.setText(String.valueOf(user.getTimeDateLastCon()));
        // Toast.makeText(context, user.getUserStatus(), Toast.LENGTH_SHORT).show();
       try {
           if (user.getUserStatus().equals("online")){
               lastTimeDate.setTextColor(Color.GREEN);
               lastTimeDate.setText(user.getUserStatus());
           }else {
               String lastTimeConnected=setLastTime(user.getUserStatus());
               lastTimeDate.setTextColor(Color.RED);
               lastTimeDate.setText(lastTimeConnected);
           }
       } catch (Exception e){
          // Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
       }
            //call method for set last message
            setLastMessage(user);

           if (user.getUserProfileImgURL()!=null){
         Glide.with(userProfileImg.getContext())
                 .load(user.getUserProfileImgURL())
                 .into(userProfileImg);
           } else
               {
                   userProfileImg.setImageResource(R.drawable.pro);
               }



     }

        private String setLastTime(String userStatus) {

            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date=dateFormat.parse(userStatus);
                Calendar cal=Calendar.getInstance();
                cal.setTime(date);

                Calendar addedCal=Calendar.getInstance();
                addedCal.setTime(date);
                addedCal.add(Calendar.HOUR,24);

                Calendar currentCal=Calendar.getInstance();
                if (currentCal.after(addedCal) || currentCal.equals(addedCal)){
                    //TODO: here we write to to return date and month
                    int dd=cal.get(Calendar.DATE);
                    int mm=cal.get(Calendar.MONTH);

                    String dateAndMonth=String.valueOf(dd)+" "+getMonthForInt(mm);
                    return  dateAndMonth;
                }
                else{
                    //TODO: here we write code to send hours and minutes

                    Date time=cal.getTime();

                    DateFormat timeFormat=new SimpleDateFormat("K:mm a");

                    String strTime=timeFormat.format(time);

                    return strTime;

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return userStatus;
        }

        public String getMonthForInt(int num) {
            String month = "wrong";
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] months = dfs.getMonths();
            if (num >= 0 && num <= 11 ) {
                month = months[num];
            }
            return month.toUpperCase().substring(0,3);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition=getAdapterPosition();
            User user=userList.get(adapterPosition);
            mOnClickListner.onListItemClick(user);
        }


       private void setLastMessage(final User user){
           DatabaseReference mMessagesRef = FirebaseDatabase.getInstance().getReference().child("Conversations");
            final List<Message> megList = new ArrayList<>();
           ValueEventListener mValListener = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   for (DataSnapshot data : dataSnapshot.getChildren()){
                       final Message message = data.getValue(Message.class);

                       if (message.getSenderUID().equals(FirebaseAuth.getInstance().getUid())&& message.getReceiverUID().equals(user.getUID())
                               || message.getSenderUID().equals(user.getUID()) && message.getReceiverUID().equals(FirebaseAuth.getInstance().getUid()))
                       {
                           megList.add(message);
                       }





                   }
                   //Toast.makeText(context, String.valueOf(megList.size()), Toast.LENGTH_SHORT).show();
                   if (megList.size()>0){
                       if (megList.get(megList.size()-1).getSenderUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                           userLastMsg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.last_msg_list_users_tick,0,0,0);
                       }

                       if (megList.get(megList.size()-1).getTxtMessage().length()>30){
                           String message = megList.get(megList.size()-1).getTxtMessage().substring(0,30)+"...";
                           userLastMsg.setTextColor(Color.BLACK);
                           userLastMsg.setText(message);
                           userLastMsg.setTypeface(null,Typeface.NORMAL);
                       }else {
                           userLastMsg.setTextColor(Color.BLACK);
                           userLastMsg.setText(megList.get(megList.size()-1).getTxtMessage());
                           userLastMsg.setTypeface(null,Typeface.NORMAL);
                       }

                   } else {
                       userLastMsg.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                       userLastMsg.setTextColor(Color.GRAY);
                       userLastMsg.setText("NOT ANY MESSAGE SENT");
                       userLastMsg.setTypeface(null,Typeface.ITALIC);
                   }

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           };


           mMessagesRef.addValueEventListener(mValListener);
       }
    }
}

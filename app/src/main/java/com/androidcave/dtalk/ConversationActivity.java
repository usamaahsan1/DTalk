package com.androidcave.dtalk;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcave.dtalk.adapters.ConversationAdapter;
/*import com.androidcave.dtalk.classification.MainClassificationActivity;*//**/
import com.androidcave.dtalk.models.Message;
import com.androidcave.dtalk.models.User;
import com.androidcave.dtalk.profile.ProfileActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class ConversationActivity extends AppCompatActivity implements ConversationAdapter.MessageListItemClickListner {
    private static final int CONTEXT_MENU_DELETE_FOR_ME =22 ;
    private static final int CONTEXT_MENU_DELETE_FOR_EVERYONE = 33;
    User user;
private List<Message> messages;
RecyclerView rcConversation;
ConversationAdapter adapter;
CircleImageView imageViewActionBar;

EditText edMessageText;
ImageButton btnSendMsg,btnCamera;

private DatabaseReference conversationDatabaseRef;

private ChildEventListener getAllConversationChildListener;
    private Message messageClicked;
    private int adapterPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        rcConversation=findViewById(R.id.recyclerConversation);
        edMessageText=findViewById(R.id.edTypeMessage);
        btnSendMsg=findViewById(R.id.btnSendMessage);

        btnCamera=findViewById(R.id.btnCam);
        messages=new ArrayList<>();



       /* btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent =new Intent(ConversationActivity.this, MainClassificationActivity.class);
                mIntent.putExtra("ConvUser",user);
                startActivity(mIntent);
            }
        });*/


        Intent intent=getIntent();
        user= (User) intent.getSerializableExtra("ConversationUser");

        String chatDefData = intent.getStringExtra("CHAT");

        edMessageText.setText(chatDefData);

        setActionBar();

        conversationDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Conversations");


        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        rcConversation.setLayoutManager(layoutManager);
        rcConversation.setHasFixedSize(true);

        edMessageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() >0){
                    btnSendMsg.setEnabled(true);
                }else {
                    btnSendMsg.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtMessage=edMessageText.getText().toString();
                String currentUser=FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (txtMessage.isEmpty()){

                }else{

                final String msgId=conversationDatabaseRef.push().getKey();
             Message message=new Message(msgId,currentUser,user.getUID(),txtMessage,"sending",getCurrentTimeDate());

             conversationDatabaseRef.child(msgId).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if (task.isSuccessful()){

                       conversationDatabaseRef.child(msgId).child("msgStatus").setValue("sent");
                         adapter.notifyDataSetChanged();
                     }
                 }
             });

                edMessageText.setText("");
                }
            }
        });


            getAllConversationChildListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Message message=dataSnapshot.getValue(Message.class);
                    if (message.getSenderUID().equals(FirebaseAuth.getInstance().getUid())&& message.getReceiverUID().equals(user.getUID())
                        || message.getSenderUID().equals(user.getUID()) && message.getReceiverUID().equals(FirebaseAuth.getInstance().getUid()))
                    {
                        messages.add(message);
                    }


                    adapter=new ConversationAdapter(messages,getApplicationContext(),ConversationActivity.this);

                    rcConversation.setAdapter(adapter);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    messages=new ArrayList<>();
                    Message message=dataSnapshot.getValue(Message.class);
                    if (message.getSenderUID().equals(FirebaseAuth.getInstance().getUid())&& message.getReceiverUID().equals(user.getUID())
                            || message.getSenderUID().equals(user.getUID()) && message.getReceiverUID().equals(FirebaseAuth.getInstance().getUid()))
                    {
                        if (message.getMsgStatus().equals("sent")){
                            messages.add(message);
                        }


                    }

                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            conversationDatabaseRef.addChildEventListener(getAllConversationChildListener);



    }


    public void setActionBar(){
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView=mInflater.inflate(R.layout.custom_action_bar_layout,null);
        TextView mNameActionBar=mCustomView.findViewById(R.id.title_text_Action_bar);
       // Glide.with(this).load(user.getUserProfileImgURL()).into(imageViewActionBar);
        imageViewActionBar=mCustomView.findViewById(R.id.circularimageView_action_bar);
        TextView   mSubStatusCon=mCustomView.findViewById(R.id.statusUser_Actionbar);
        //TODO(1): make correct below
        mNameActionBar.setText(user.getUserName());
        mSubStatusCon.setText(setLastTime(user.getUserStatus()));
        imageViewActionBar.setImageResource(R.drawable.pro);
//        Picasso.get().load(Uri.parse(user.getProfileImageUrl())).into(imageViewActionBar);
//        mActionBar.setDisplayUseLogoEnabled(true);
        Glide.with(imageViewActionBar.getContext()).load(user.getUserProfileImgURL()).into(imageViewActionBar);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        
        // Open profile on click image icon
        imageViewActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Toast.makeText(ConversationActivity.this, "Opening User's profile...", Toast.LENGTH_SHORT).show();
               // startActivity(new Intent(ConversationActivity.this, ProfileActivity.class));
            }
        });
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

                DateFormat timeFormat=new SimpleDateFormat("h:mm a");

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

    private String getCurrentTimeDate(){

    Calendar calendar=Calendar.getInstance();

    Date currentDateTime=calendar.getTime();

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

     String formatedDateTime=dateFormat.format(currentDateTime);

     return formatedDateTime;
    }

    @Override
    public void onMessageItemClick(Message messageClicked, int position) {
        this.messageClicked=messageClicked;
        adapterPosition=position;
        registerForContextMenu(rcConversation);
        openContextMenu(rcConversation);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE,CONTEXT_MENU_DELETE_FOR_ME,Menu.NONE,"Delete for Me");
        if (messageClicked.getSenderUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())){
            menu.add(Menu.NONE,CONTEXT_MENU_DELETE_FOR_EVERYONE,Menu.NONE,"Delete for Everyone");
        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content) , "Message is deleted!",Snackbar.LENGTH_LONG).setAction("close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        View view=snackbar.getView();
        TextView tvSnack=view.findViewById(android.support.design.R.id.snackbar_text);
        tvSnack.setTextColor(Color.YELLOW);
        snackbar.setDuration(5000);
        switch (item.getItemId()){
            case CONTEXT_MENU_DELETE_FOR_EVERYONE:
                DatabaseReference delRef=FirebaseDatabase.getInstance().getReference().child("Conversations");
                delRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageSnap :dataSnapshot.getChildren()){
                            Message message=messageSnap.getValue(Message.class);
                            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                            if (message.getSenderUID().equals(user.getUid().toString())){
                                if (message.getTimeDateSent().equals(messageClicked.getTimeDateSent())){
                                    messageSnap.getRef().removeValue();
                                }
                            }
                        }
                        messages.remove(adapterPosition);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                // Toast.makeText(this, messageClicked.getTextMessage()+"\n Deleted for Everyone ", Toast.LENGTH_SHORT).show();
                snackbar.setText("Message is deleted for Everyone!");
                snackbar.show();
                break;
            case CONTEXT_MENU_DELETE_FOR_ME:
                messages.remove(adapterPosition);
                synchronized (messages){
                    messages.notify();
                    adapter.notifyItemRangeChanged(adapterPosition,messages.size());
                }
                adapter.notifyDataSetChanged();
                //Toast.makeText(this, messageClicked.getTextMessage()+"\n is Delete for you!", Toast.LENGTH_SHORT).show();
                snackbar.setText("Message is deleted for You!");
                snackbar.show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ConversationActivity.this,MainUsersActivity.class));
        finish();
    }
}

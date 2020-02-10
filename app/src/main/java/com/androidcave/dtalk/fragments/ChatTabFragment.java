package com.androidcave.dtalk.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcave.dtalk.ConversationActivity;
import com.androidcave.dtalk.R;
import com.androidcave.dtalk.adapters.UsersListAdapter;
import com.androidcave.dtalk.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatTabFragment extends Fragment implements UsersListAdapter.ListItemClickListner {
RecyclerView recyclerView;
private List<User> users;
UsersListAdapter listAdapter;
View mView;


private FirebaseDatabase usersDatabase;
private DatabaseReference userDbReference;
private DatabaseReference currentUserConnectionState;
private ChildEventListener usersListChildListener;
private ValueEventListener connectionStateListener;


private String currentUserUID;
    public ChatTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =inflater.inflate(R.layout.fragment_chat_tab, container, false);
            mView = view;
        recyclerView=view.findViewById(R.id.RVChatList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        users=new ArrayList<>();

        listAdapter=new UsersListAdapter(users,this,getContext());
        recyclerView.setAdapter(listAdapter);


        usersDatabase=FirebaseDatabase.getInstance();

        usersDatabase.getReference().keepSynced(true);

        currentUserUID=FirebaseAuth.getInstance().getCurrentUser().getUid();

        userDbReference=usersDatabase.getReference().child("Users");

        currentUserConnectionState=userDbReference.child(currentUserUID).child("userStatus");


        connectionStateListener=userDbReference.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected=(Boolean)dataSnapshot.getValue();
                if (connected){
                    currentUserConnectionState.setValue("online");

                    currentUserConnectionState.onDisconnect().setValue(getCurrentTime());

                    // snackbar code
                    final Snackbar snackbar = Snackbar.make(mView.getRootView().findViewById(android.R.id.content), "You are Connected!",Snackbar.LENGTH_LONG).setAction("close", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                    View view=snackbar.getView();
                    TextView tvSnack=view.findViewById(android.support.design.R.id.snackbar_text);
                    tvSnack.setTextColor(Color.GREEN);
                    snackbar.setDuration(10000);
                    snackbar.show();

                }
                else {
                    final Snackbar snackbar = Snackbar.make(mView.getRootView().findViewById(android.R.id.content) , "You are Disconnected!",Snackbar.LENGTH_LONG).setAction("close", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                    View view=snackbar.getView();
                    TextView tvSnack=view.findViewById(android.support.design.R.id.snackbar_text);
                    tvSnack.setTextColor(Color.RED);
                    snackbar.setDuration(10000);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        usersListChildListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    String uid=dataSnapshot.getKey();
                        //   Toast.makeText(getContext(), getCurrentTime(), Toast.LENGTH_SHORT).show();
                    if (!uid.equals(currentUserUID)){
                        User user=dataSnapshot.getValue(User.class);

                        users.add(user);

                        listAdapter=new UsersListAdapter(users,ChatTabFragment.this,getContext());
                        recyclerView.setAdapter(listAdapter);
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

        userDbReference.addChildEventListener(usersListChildListener);


        return view;
    }

    @Override
    public void onListItemClick(User clickedUser) {
        Intent intent=new Intent(getActivity(),ConversationActivity.class);
        intent.putExtra("ConversationUser", clickedUser);
        startActivity(intent);
    }


    public static String getCurrentTime(){
        Calendar cal=Calendar.getInstance();

        Date date=cal.getTime();

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate=dateFormat.format(date);

        return formattedDate;
    }
}

package com.androidcave.dtalk.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidcave.dtalk.R;
import com.androidcave.dtalk.adapters.StatusListAdapter;
import com.androidcave.dtalk.models.Status;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusTabFragment extends Fragment {

    RecyclerView recStatusList;
    StatusListAdapter adapter;
    List<Status> statusList;

    DatabaseReference mStatusDbRef;
    ChildEventListener mStateListener;
    ValueEventListener mStateValuesListener;
    public StatusTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status_tab, container, false);

        recStatusList = view.findViewById(R.id.recylerStatusList);

        mStatusDbRef = FirebaseDatabase.getInstance().getReference().child("Statuses");

        recStatusList.setLayoutManager(new LinearLayoutManager(getContext()));

        recStatusList.setHasFixedSize(true);
        statusList = new ArrayList<>();
        adapter = new StatusListAdapter(statusList,getContext());
        recStatusList.setAdapter(adapter);
        //fillRecycler();

        mStateValuesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(getContext(), dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                statusList.clear();
                adapter.notifyDataSetChanged();
                for (DataSnapshot valueRes : dataSnapshot.getChildren()){
                    //  Toast.makeText(getContext(), valueRes.toString(), Toast.LENGTH_SHORT).show();
                    Status status = valueRes.getValue(Status.class);
                    statusList.add(status);
                }


                adapter = new StatusListAdapter(statusList,getContext());
                recStatusList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mStatusDbRef.addValueEventListener(mStateValuesListener);


        // mStatusDbRef.addChildEventListener(mStateListener);

        return view;
    }
    private void fillRecycler(){
        mStateListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // DataSnapshot dataSnapshotRes = dataSnapshot.child("Statuses");

                for (DataSnapshot valueRes : dataSnapshot.getChildren()){
                    Toast.makeText(getContext(), valueRes.toString(), Toast.LENGTH_SHORT).show();
//                        Status status = valueRes.getValue(Status.class);
//                        statusList.add(status);
                }


                adapter = new StatusListAdapter(statusList, getContext());

                recStatusList.setAdapter(adapter);
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


    }

    public void notifiyChangeFromFrag() {
        adapter = new StatusListAdapter(statusList,getContext());
//        recStatusList.setAdapter(adapter);

        fillRecycler();

    }
}

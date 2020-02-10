package com.androidcave.dtalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidcave.dtalk.auth.LoginActivity;
/*import com.androidcave.dtalk.classification.MainClassificationActivity;*/
import com.androidcave.dtalk.fragments.ChatTabFragment;
import com.androidcave.dtalk.fragments.StatusTabFragment;
import com.androidcave.dtalk.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainUsersActivity extends AppCompatActivity {
    ViewPager pager;
    TabLayout tabLayout;
    FloatingActionButton btnAddNewStatus;
    DatabaseReference currentUserStateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_users);

        pager=findViewById(R.id.pagerMain);
       btnAddNewStatus=findViewById(R.id.AddNew);

        tabLayout=findViewById(R.id.TabsMainUsers);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));

        setUpViewPager(pager);


        btnAddNewStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUsersActivity.this,WriteStatusActivity.class));
            }
        });



       currentUserStateRef=FirebaseDatabase.getInstance().getReference()
               .child("Users").child(FirebaseAuth.getInstance()
                       .getCurrentUser().getUid()).child("userStatus");




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId=item.getItemId();

        switch (itemId){

            case R.id.optLogout:
                currentUserStateRef.setValue(getCurrentTime());
                FirebaseAuth.getInstance().signOut();
                //And send to sign in activity
                startActivity(new Intent(new Intent(this,LoginActivity.class)));
                finish();
                break;

//            case R.id.uid:
//                Toast.makeText(this, FirebaseAuth.getInstance().getUid(), Toast.LENGTH_SHORT).show();
//               // startActivity(new Intent(new Intent(this, MainClassificationActivity.class)));
//                break;

            case R.id.optProfile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;

                default:
                    Toast.makeText(this, "No action performed!", Toast.LENGTH_SHORT).show();
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpViewPager(ViewPager viewPager){
    SwipePagerAdapter pagerAdapter=new SwipePagerAdapter(getSupportFragmentManager());
    pagerAdapter.addFragment(new ChatTabFragment(),String.valueOf(0));
    pagerAdapter.addFragment(new StatusTabFragment(),String.valueOf(1));
    viewPager.setAdapter(pagerAdapter);
}

    public class SwipePagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> stepsList =new ArrayList<>();

        public SwipePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:{
                    //stepView.go(0,true);
                    return new ChatTabFragment();
                }
                case 1:{
                    //stepView.go(1,true);
                    return new StatusTabFragment();
                }
                default:
                    return null;
            }



        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment, String step) {
            mFragmentList.add(fragment);
            stepsList.add(step);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return stepsList.get(position);
        }
    }

    public static String getCurrentTime(){
        Calendar cal=Calendar.getInstance();

        Date date=cal.getTime();

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate=dateFormat.format(date);

        return formattedDate;
    }
}

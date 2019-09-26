package com.example.gp4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //private FragmentManager fragmentManager = getSupportFragmentManager();
    private EmotionFragment emotionFragment = new EmotionFragment();
    private MakeFragment makeFragment = new MakeFragment();
    private MyFragment myFragment = new MyFragment();

    private BottomNavigationView bottomNavigationView;
    private String after = ""; // inputCatridge에서 넘어왔는지 알아보기 위한 변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        after = intent.getStringExtra("inputCatridge");

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.main_activity_bottomnavigationview);


        // 첫 화면 지정
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.replace(R.id.main_activity_framelayout, emotionFragment).commitAllowingStateLoss();
        replaceFragment(emotionFragment.newInstance());

        // 네비게이션 바 선택 시
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()){
                    case R.id.emotionAnalysis :
                        //fragmentTransaction.replace(R.id.main_activity_framelayout, emotionFragment).commitAllowingStateLoss();
                        replaceFragment(emotionFragment.newInstance());
                        break;
                    case R.id.make:
                        //fragmentTransaction.replace(R.id.main_activity_framelayout, makeFragment).commitAllowingStateLoss();
                        replaceFragment(makeFragment.newInstance());
                        break;
                    case R.id.my :
                        //fragmentTransaction.replace(R.id.main_activity_framelayout, myFragment).commitAllowingStateLoss();
                        replaceFragment(myFragment.newInstance());
                        break;
                }
                return true;
            }
        });

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_framelayout, fragment).commit();
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public interface onKeyBackPressedListener {
        void onBackKey();
    }
    private onKeyBackPressedListener mOnKeyBackPressedListener;
    public void setOnKeyBackPressedListener(onKeyBackPressedListener listener){
        mOnKeyBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if( after.equals("yes") ){ }
        else{ super.onBackPressed(); }

    }
}

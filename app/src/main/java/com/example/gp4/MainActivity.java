package com.example.gp4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EmotionFragment emotionFragment = new EmotionFragment(); // 스트레스 지수 및 감정 파악
    private MakeFragment makeFragment = new MakeFragment(); // 커스터마이징
    private MyFragment myFragment = new MyFragment(); // 저장된 향 정보
    private TurnonFragment turnonFragment = new TurnonFragment(); // 디퓨저 작동 부분

    private FloatingActionButton fab;
    private BottomNavigationView bottomNavigationView;
    private String after = ""; // inputCatridge에서 넘어왔는지 알아보기 위한 변수
    private long lastTimeBackPressed; // 뒤로가기 버튼이 클릭된 시간

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        after = intent.getStringExtra("inputCatridge"); // 카트리지 정보 입력 후 넘어왔는지 확인하기 위한 변수

        fab = (FloatingActionButton)findViewById(R.id.main_activity_fab);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.main_activity_bottomnavigationview);

        // 첫 화면 지정
        replaceFragment(emotionFragment.newInstance());

        // 네비게이션 바 선택 시
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.emotionAnalysis :
                        replaceFragment(emotionFragment.newInstance());
                        break;
                    case R.id.make:
                        replaceFragment(makeFragment.newInstance());
                        break;
                    case R.id.my :
                        replaceFragment(myFragment.newInstance());
                        break;
                }
                return true;
            }
        });


        // 플로팅 버튼 클릭 시
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(turnonFragment.newInstance());
            }
        });

    }


    @SuppressLint("RestrictedApi")
    public void replaceFragment(Fragment fragment) {
        // 플로팅 버튼 설정
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        Boolean turnOn = sharedPreferences.getBoolean("turnOn", false); // 디퓨저 작동하는지 가져오기

        if(turnOn){
            fab.setVisibility(View.VISIBLE); // 디퓨저가 작동하고 있을 때에만 보이도록
        }else{
            fab.setVisibility(View.INVISIBLE);
        }

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

    // 뒤로가기 설정
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBackPressed() {
        // 카트리지 정보 등록 후 바로 메인 액티비티로 왔을 때
        if( after.equals("yes") ){
            // 2초 이내에 뒤로가기 버튼 재 클릭시 어플 종료
            if(System.currentTimeMillis() - lastTimeBackPressed < 2000) {
                finishAffinity();
                return;
            }

            // 뒤로 한번 클릭 시 메시지
            LayoutInflater inflater = getLayoutInflater();
            View toastDesign = inflater.inflate(R.layout.toast_design, (ViewGroup)findViewById(R.id.toast_design_root));
            TextView textView = toastDesign.findViewById(R.id.toast_design_textview); // 토스트 꾸미기 위함
            textView.setTextColor(R.color.colorPrimaryDark);

            textView.setText("'뒤로'버튼 한번 더 누르시면 앱이 종료됩니다.");
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 30);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastDesign);
            toast.show();

            lastTimeBackPressed = System.currentTimeMillis();
        }
        // 스플래쉬 화면에서 바로 메인 화면으로 왔을 때
        else{
            super.onBackPressed(); //어플 종료
             }

    }

}

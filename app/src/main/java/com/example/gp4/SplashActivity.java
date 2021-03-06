package com.example.gp4;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.UUID;

// 로딩 화면
public class SplashActivity extends Activity {

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        //정보 삭제를 위한 코드
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.clear();
//            editor.commit();
//            Log.v("확인", "정보 삭제 확인"); // 여기까지 정보 삭제 코드

        String tmpInfo = sharedPreferences.getString("Info", ""); // UserInfo 파일에서 Info에 저장되어 있는 값 불러오기
        String tmpId = sharedPreferences.getString("pushID", ""); // UserInfo 파일에서 pushID 값 불러오기

        if(tmpInfo.equals("")){ // 회원 등록 하지 않았을 경우
            startActivity(new Intent(this, RegisterActivity.class));
        }else {
            if(tmpId.equals("")){
                // 카트리지 정보를 저장하지 않았다면
                startActivity(new Intent(this, InputCatridgeActivity.class));
            }else{
            gson = new GsonBuilder().create();
            UserInfo userInfo = gson.fromJson(tmpInfo, UserInfo.class);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("inputCatridge", "no"); // 메인 액티비티에서 뒤로 가기 설정에 필요한 정보
                startActivity(intent);
            }
        }

        finish();
    }
}

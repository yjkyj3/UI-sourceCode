package com.example.dictionary;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onBtnClick(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "https://endic.naver.com/popManager.nhn?m=search&query=XXX%20%EC%B6%9C%EC%B2%98:%20https://flystone.tistory.com/15%20[MomO]"));
        startActivity(intent);
    }
}
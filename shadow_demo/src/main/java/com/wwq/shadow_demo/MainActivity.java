package com.wwq.shadow_demo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.wwq.pluginlibrary.ITest;
import com.wwq.pluginlibrary.ShadowActivity;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends ShadowActivity implements ITest {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("shadow_ca","plugin onCreate---");
//        TextView textView=new TextView(this);
//        textView.setText("我是插件");
//        textView.setTextSize(100);
//        textView.setTextColor(Color.WHITE);
//        setContentView(textView);
        setContentView(R.layout.wwq);
        Toast.makeText(this,"我是插件弹出的toast",Toast.LENGTH_SHORT).show();
        Log.d("shadow_ca","plugin onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("shadow_ca","plugin onStart---");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("shadow_ca","plugin onResume---");
    }
}
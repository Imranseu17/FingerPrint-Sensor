package com.zkteco.silkiddemo.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.zkteco.silkiddemo.R;
import com.zkteco.silkiddemo.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 8000;
    ActivityWelcomeBinding welcomeBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       welcomeBinding = DataBindingUtil. setContentView(this,R.layout.activity_welcome);

//       welcomeBinding.data.setText("EmpID: "+getIntent().getStringExtra("eID")+" "+
//                   "VerifyID: "+getIntent().getIntExtra("verifyID",0)+" "+
//                  "Base64: "+getIntent().getStringExtra("strBase64")+" "+
//               "Template: "+getIntent().getStringExtra("template")+" "+
//               "DateTime: "+getIntent().getStringExtra("dateTime"));

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(WelcomeActivity.this, VerifyActivity.class);
                mainIntent.putExtra("eID",getIntent().getStringExtra("eID"));
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
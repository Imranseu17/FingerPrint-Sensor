package com.zkteco.silkiddemo.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.mapzen.speakerbox.Speakerbox;
import com.zkteco.silkiddemo.R;
import com.zkteco.silkiddemo.databinding.ActivityWelcomeBinding;

import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    ActivityWelcomeBinding welcomeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       welcomeBinding = DataBindingUtil. setContentView(this,R.layout.activity_welcome);



       welcomeBinding.name.setText("Welcome "+getIntent().getStringExtra("eName"));
       welcomeBinding.inTime.setText("In Time "+getIntent().getStringExtra("dateTime"));

        Speakerbox speakerbox = new Speakerbox(getApplication());
        speakerbox.play(welcomeBinding.name.getText().toString());

    

   
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(WelcomeActivity.this, VerifyActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }



}
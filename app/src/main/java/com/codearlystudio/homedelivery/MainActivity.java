package com.codearlystudio.homedelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.codearlystudio.homedelivery.data.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import in.codeshuffle.typewriterview.TypeWriterListener;
import in.codeshuffle.typewriterview.TypeWriterView;

public class MainActivity extends AppCompatActivity {

    Animation bottomAnim;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        preferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        editor = preferences.edit();
        setAnimation();
        initiateFirebaseToken();
    }

    void initiateFirebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                editor.putString(Constants.USER_FCM_TOKEN, task.getResult());
                editor.commit();
            }
        });
    }

    void setAnimation(){
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        final TypeWriterView typeWriterView = findViewById(R.id.typeWriterViewTitle);
        typeWriterView.setWithMusic(false);
        typeWriterView.setTypeWriterListener(new TypeWriterListener() {
            @Override
            public void onTypingStart(String text) { }
            @Override
            public void onTypingEnd(String text) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, IntroActivity.class));
                        finish();
                    }
                },500);
            }
            @Override
            public void onCharacterTyped(String text, int position) { }
            @Override
            public void onTypingRemoved(String text) { }
        });
        typeWriterView.animateText(Constants.INTRO_TITLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

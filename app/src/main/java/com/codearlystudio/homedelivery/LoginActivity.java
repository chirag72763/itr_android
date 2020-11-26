package com.codearlystudio.homedelivery;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.google.android.material.textfield.TextInputEditText;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Animation slideInside, slideOutside, slideRightInside, slideRightOutside;
    ProgressBar progressPhone, progressOtp, progressName;
    TextInputEditText etNumber, etOtpNumber, etNameText;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RelativeLayout relativePhone, relativeOtp, relativeName;
    Button btnContinue;
    int currentScreen = 0; //0 for phone, 1 for OTP, 2 for Name
    String generatedOTP = "", userId = "", userName= "";
    boolean firstTimeLogin = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        initializeComponents();
        btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                btnContinue.setClickable(false);
                switch (currentScreen){
                    case 0:
                        Random random = new Random();
                        generatedOTP = String.format("%04d", random.nextInt(10000));
                        checkValidNumberAndLogin(etNumber.getText().toString().trim());
                        break;
                    case 1:
                        checkOTP(generatedOTP);
                        break;
                    case 2:
                        if (firstTimeLogin){
                            if (!etNameText.getText().toString().trim().equals("")){
                                btnContinue.setClickable(false);
                                uploadUserPhoneAndName(etNumber.getText().toString().trim(),etNameText.getText().toString().trim());
                            }
                        }
                        break;
                }
            }
        });
    }

    @SuppressLint("CommitPrefEdits")
    void initializeComponents(){
        slideInside = AnimationUtils.loadAnimation(this, R.anim.slide_right_to_left_inside);
        slideOutside = AnimationUtils.loadAnimation(this, R.anim.slide_right_to_left_outside);
        slideRightInside = AnimationUtils.loadAnimation(this, R.anim.slide_left_to_right_inside);
        slideRightOutside = AnimationUtils.loadAnimation(this, R.anim.slide_left_to_right_outside);
        progressName = findViewById(R.id.loader_name);
        progressOtp = findViewById(R.id.loader_otp);
        progressPhone = findViewById(R.id.loader_phone);
        relativeName = findViewById(R.id.relative_name);
        relativePhone = findViewById(R.id.relative_phone);
        relativeOtp = findViewById(R.id.relative_otp);
        etNumber = findViewById(R.id.et_number);
        etOtpNumber = findViewById(R.id.et_otp_number);
        etNameText = findViewById(R.id.et_name_text);
        pref = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        editor = pref.edit();
    }

    void checkValidNumberAndLogin(String number) {
        if (number.length() == 10) {
            checkIfUserExists(number);
            btnContinue.setClickable(false);
        } else {
            btnContinue.setClickable(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(Constants.WRONG_INPUT_TITLE);
            builder.setMessage(Constants.ENTER_CORRECT);
            builder.setPositiveButton(Constants.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    void checkIfUserExists(String number){
        progressPhone.setVisibility(View.VISIBLE);
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.checkIfUserExists(number, generatedOTP);
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        String[] separated = serverResponse.getMessage().split(":");
                        userId = separated[0].replaceAll("\\s+","");
                        userName = separated[1].trim();
                        firstTimeLogin = false;
                    }
                    else {
                        progressName.setVisibility(View.GONE);
                        btnContinue.setClickable(false);
                    }
                    progressPhone.setVisibility(View.GONE);
                    btnContinue.setClickable(true);
                    switchToOtpScreen();
                }
                else {
                    progressName.setVisibility(View.GONE);
                    btnContinue.setClickable(true);
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressName.setVisibility(View.GONE);
                btnContinue.setClickable(true);
            }
        });
    }

    void uploadUserPhoneAndName(String phone, final String name){
        progressName.setVisibility(View.VISIBLE);
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.uploadUserPhone(phone, name);
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        sendFCMTokenToServer(serverResponse.getMessage(), etNumber.getText().toString().trim(), name);
                    } else {
                        progressName.setVisibility(View.GONE);
                        btnContinue.setClickable(false);
                    }
                } else {
                    progressName.setVisibility(View.GONE);
                    btnContinue.setClickable(false);
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressName.setVisibility(View.GONE);
                btnContinue.setClickable(false);
            }
        });
    }

    void switchToOtpScreen(){
        currentScreen = 1;
        relativePhone.setAnimation(slideOutside);
        relativePhone.setVisibility(View.GONE);
        relativeOtp.startAnimation(slideInside);
        relativeOtp.setVisibility(View.VISIBLE);
    }

    void checkOTP ( String generatedOTP ){
        if (etOtpNumber.length() == 4 && Objects.requireNonNull(etOtpNumber.getText()).toString().trim().equals(generatedOTP)){
            if (firstTimeLogin){
                btnContinue.setClickable(true);
                switchToNameScreen();
            }
            else {
                sendFCMTokenToServer(userId, Objects.requireNonNull(etNumber.getText()).toString().trim(), userName);
            }
        }
        else {
            btnContinue.setClickable(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(Constants.WRONG_OTP);
            builder.setMessage(Constants.ENTER_CORRECT_OTP + Objects.requireNonNull(etNumber.getText()).toString().trim());
            builder.setPositiveButton(Constants.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    void sendFCMTokenToServer(final String userId, final String number, final String name) {
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.uploadFcmToken(userId, pref.getString(Constants.USER_FCM_TOKEN, null));
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @SuppressLint("Assert")
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        editor.putInt(Constants.LOGIN_FLAG, 1);
                        editor.putString(Constants.USER_ID, userId);
                        editor.putString(Constants.USER_NAME, name);
                        editor.putString(Constants.USER_PHONE, number);
                        editor.commit();
                        finish();
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) { }
        });
    }

    void switchToNameScreen(){
        currentScreen = 2;
        relativeOtp.setAnimation(slideOutside);
        relativeOtp.setVisibility(View.GONE);
        relativeName.startAnimation(slideInside);
        relativeName.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        switch (currentScreen){
            case 2:
                relativeName.setAnimation(slideRightOutside);
                relativeName.setVisibility(View.GONE);
                relativeOtp.setAnimation(slideRightInside);
                relativeOtp.setVisibility(View.VISIBLE);
                currentScreen = 1;
                break;
            case 1:
                relativeOtp.setAnimation(slideRightOutside);
                relativeOtp.setVisibility(View.GONE);
                relativePhone.setAnimation(slideRightInside);
                relativePhone.setVisibility(View.VISIBLE);
                currentScreen = 0;
                break;
            case 0:
                super.onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

}
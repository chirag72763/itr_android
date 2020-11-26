package com.codearlystudio.homedelivery;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;

public class Address extends AppCompatActivity {

    public static TextInputEditText etName, etPhone, etAddress, etLandmark, etCity, etState, etEmail, etPincode;
    Button btnAddAddress;
    String name, phone, pincode, address, landmark, city, state, email;
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;
    Intent intent;
    @SuppressLint("StaticFieldLeak")
    public static Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        ctx = this;
        intent = getIntent();
        declareComponents();
    }

    @SuppressLint({"ClickableViewAccessibility", "CommitPrefEdits"})
    void declareComponents() {
        preferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        editor = preferences.edit();
        etPincode = findViewById(R.id.et_pincode);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etLandmark = findViewById(R.id.et_landmark);
        etCity = findViewById(R.id.et_city);
        etState = findViewById(R.id.et_state);
        etEmail = findViewById(R.id.et_email);
        etName.setText(preferences.getString(Constants.USER_NAME, null));
        etPhone.setText(preferences.getString(Constants.USER_PHONE, null));
        btnAddAddress = findViewById(R.id.add_address_to_server);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddressToServer();
            }
        });
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void addAddressToServer(){
        name = Objects.requireNonNull(etName.getText()).toString();
        phone = Objects.requireNonNull(etPhone.getText()).toString();
        pincode = Objects.requireNonNull(etPincode.getText()).toString().trim();
        address = Objects.requireNonNull(etAddress.getText()).toString();
        landmark = Objects.requireNonNull(etLandmark.getText()).toString();
        city = Objects.requireNonNull(etCity.getText()).toString();
        state = Objects.requireNonNull(etState.getText()).toString();
        email = Objects.requireNonNull(etEmail.getText()).toString();
        boolean valid = checkInputs(name,phone,pincode,address,landmark,city,state,email);
        if (valid) {
            btnAddAddress.setEnabled(false);
            Api getResponse = AppConfig.getRetrofit().create(Api.class);
            Call<ServerResponse> call = getResponse.addNewAddress(name,phone,pincode,address,landmark,city,state,email,preferences.getString(Constants.USER_ID,null));
            call.enqueue(new retrofit2.Callback<ServerResponse>() {
                @SuppressLint("Assert")
                @Override
                public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                    ServerResponse serverResponse = response.body();
                    if (serverResponse != null) {
                        if (serverResponse.getSuccess()) {
                            if (Objects.equals(intent.getStringExtra("FROM"), "Account")){
                                MasterActivity.showAddresses();
                            }
                            else if (Objects.equals(intent.getStringExtra("FROM"), "Cart")){
                                Cart.showAddresses();
                            }
                            onBackPressed();
                        }
                    }
                }
                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    btnAddAddress.setEnabled(true);
                }
            });
        }
    }
    boolean checkInputs(String name, String phone, String pincode, String address, String landmark, String city, String state, String email){
        if (!name.equals("") && !phone.equals("") && !pincode.equals("") && !address.equals("") && !landmark.equals("") && !city.equals("") && !state.equals("") && !email.equals("")){
            if (phone.length() == 10){
                if (isEmail(email)){
                    if (pincode.length() == 6){
                        return true;
                    }
                    else {
                        showAlert("Enter correct pincode");
                        return false;
                    }
                }
                else {
                    showAlert("Enter correct email address");
                    return false;
                }
            }
            else {
                showAlert(Constants.ENTER_CORRECT_NO);
                return false;
            }
        }
        else {
            showAlert(Constants.PROVIDE_ALL);
            return false;
        }
    }
    static void showAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    static boolean isEmail(String email) {
        boolean matchFound1;
        boolean returnResult;
        email = email.trim();
        if(email.equalsIgnoreCase(""))
            returnResult = false;
        else if(!Character.isLetter(email.charAt(0)))
            returnResult = false;
        else {
            Pattern p1 = Pattern.compile("^\\.|^@ |^_");
            Matcher m1 = p1.matcher(email);
            matchFound1 = m1.matches();
            Pattern p = Pattern.compile("^[a-zA-z0-9._-]+[@]{1}+[a-zA-Z0-9]+[.]{1}+[a-zA-Z]{2,4}$");
            Matcher m = p.matcher(email);
            boolean matchFound = m.matches();
            StringTokenizer st = new StringTokenizer(email, ".");
            String lastToken = null;
            while (st.hasMoreTokens()) {
                lastToken = st.nextToken();
            }
            returnResult= matchFound && Objects.requireNonNull(lastToken).length() >= 2 && email.length() - 1 != lastToken.length() && !matchFound1;
        }
        return returnResult;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
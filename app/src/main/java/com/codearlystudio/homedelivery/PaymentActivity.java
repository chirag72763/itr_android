package com.codearlystudio.homedelivery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codearlystudio.homedelivery.adapters.AdapterConfirmItem;
import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.codearlystudio.homedelivery.models.CartItems;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kaopiz.kprogresshud.KProgressHUD;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

@SuppressLint("StaticFieldLeak")
public class PaymentActivity extends AppCompatActivity {
    static SharedPreferences preferences;
    static Context ctx;
    TextView itemTotalTextView;
    String totalText, scheduleDate, scheduleTime;
    RelativeLayout payOnDelivery;
    TextView dateTime, name, address, cityState, contact, totalTextView, deliveryFee, toPay;
    RecyclerView recyclerViewOrder;
    List<CartItems> cartItemsList;
    AdapterConfirmItem adapterConfirmItem;
    float total, finalTotal;
    static float delivery_fee = 0.0f;
    static KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        ctx = this;
        hud = KProgressHUD.create(ctx).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true).setCornerRadius(100.0f).setAnimationSpeed(5).setDimAmount(0.7f);
        declareComponents();
        isOnline();
    }

    void declareComponents() {
        totalTextView = findViewById(R.id.total_textView);
        deliveryFee = findViewById(R.id.delivery_fee);
        toPay = findViewById(R.id.to_pay);
        dateTime = findViewById(R.id.date_time);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        cityState = findViewById(R.id.city_state);
        contact = findViewById(R.id.contact);
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        payOnDelivery = findViewById(R.id.pay_on_delivery);
        Intent intent = getIntent();
        scheduleDate = intent.getStringExtra(Constants.SCHEDULE_DATE);
        scheduleTime = intent.getStringExtra(Constants.SCHEDULE_TIME);
        preferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        itemTotalTextView = findViewById(R.id.item_total);
        payOnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmAlert();
            }
        });
        recyclerViewOrder = findViewById(R.id.recyclerViewOrder);
        recyclerViewOrder.setHasFixedSize(true);
        recyclerViewOrder.setNestedScrollingEnabled(true);
        recyclerViewOrder.setLayoutManager(new LinearLayoutManager(ctx));
    }

    void getDefaultAddress(){
        hud.show();
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                Constants.FETCH_DEFAULT_ADDRESSES + preferences.getString(Constants.USER_ID,null), new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("text");
                    dateTime.setText(scheduleDate + ", " + scheduleTime);
                    name.setText(jsonArray.getJSONObject(0).getString("address_name"));
                    address.setText(jsonArray.getJSONObject(0).getString("address")+", "
                            +jsonArray.getJSONObject(0).getString("address_landmark")+" - "
                            +jsonArray.getJSONObject(0).getString("address_pincode"));
                    cityState.setText(jsonArray.getJSONObject(0).getString("address_city")
                            +" | "+jsonArray.getJSONObject(0).getString("address_state"));
                    contact.setText(jsonArray.getJSONObject(0).getString("address_phone")
                            +" | " +jsonArray.getJSONObject(0).getString("address_email"));
                    getProducts();
                }catch (JSONException e){
                    hud.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hud.dismiss();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    void getProducts(){
        total = 0.0f;
        cartItemsList = new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                Constants.CONFIRM_CART_URL + preferences.getString(Constants.USER_ID,null), new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("text");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        CartItems pojoItems = new CartItems(
                                o.getString("cart_id"),
                                o.getString("product_id"),
                                o.getString("variant_id"),
                                o.getString("product_name"),
                                o.getString("product_image"),
                                o.getString("variant_size"),
                                o.getString("variant_price"),
                                o.getString("variant_available"),
                                o.getString("product_available"),
                                o.getString("cart_quantity"),
                                o.getString("total_product_price")
                                );
                        total = total + Float.parseFloat(pojoItems.getTotalProductPrice());
                        cartItemsList.add(pojoItems);
                    }
                    String fee = jsonObject.getString("delivery_fee");
                    delivery_fee = Float.parseFloat(fee);
                    totalTextView.setText("₹ " +total);
                    deliveryFee.setText("₹ " +delivery_fee);
                    total = total + delivery_fee;
                    toPay.setText("₹ " +total);
                    adapterConfirmItem = new AdapterConfirmItem(cartItemsList, ctx);
                    recyclerViewOrder.setAdapter(adapterConfirmItem);
                    adapterConfirmItem.notifyDataSetChanged();
                    if (cartItemsList.size() == 1){
                        totalText = "Total ₹" + total + " - " + cartItemsList.size() + " Item";
                    }
                    else {
                        totalText = "Total ₹" + total + " - " + cartItemsList.size() + " Items";
                    }
                    itemTotalTextView.setText(totalText);
                    hud.dismiss();
                }catch (JSONException e){
                    hud.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hud.dismiss();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    void verifyTotal(){
        hud.show();
        finalTotal = 0.0f;
        cartItemsList = new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                Constants.CONFIRM_CART_URL+preferences.getString(Constants.USER_ID,null), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("text");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        finalTotal = finalTotal + Float.parseFloat(o.getString("total_product_price"));
                    }
                    String fee = jsonObject.getString("delivery_fee");
                    delivery_fee = Float.parseFloat(fee);
                    finalTotal = finalTotal + delivery_fee;
                    if (finalTotal == total){
                        addNewOrder();
                    }
                    else {
                        hud.dismiss();
                        Toast.makeText(getApplicationContext(),"Prices of some items have changed. Taking you back to cart", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }catch (JSONException e){
                    hud.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hud.dismiss();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }


    void showConfirmAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(Constants.ORDER_TITLE);
        builder.setMessage(Constants.CONFIRM_ORDER);
        builder.setPositiveButton(Constants.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verifyTotal();
            }
        });
        builder.setNegativeButton(Constants.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    void addNewOrder() {
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.addNewOrder(
                preferences.getString(Constants.USER_ID, null),
                dateTime.getText().toString().trim(),
                ""+delivery_fee);
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        Intent i = new Intent(ctx, MasterActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                }
                hud.dismiss();
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                hud.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    void isOnline() {
        final BottomSheetDialog dialogInternet = new BottomSheetDialog(this);
        dialogInternet.setContentView(R.layout.no_internet_layout);
        dialogInternet.setCancelable(false);
        dialogInternet.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInternet.dismiss();
                isOnline();
            }
        });
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            dialogInternet.dismiss();
            getDefaultAddress();
        }
        else {
            dialogInternet.dismiss();
            dialogInternet.show();
        }
    }


}

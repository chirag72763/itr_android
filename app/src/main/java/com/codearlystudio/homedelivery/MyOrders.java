package com.codearlystudio.homedelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codearlystudio.homedelivery.adapters.AdapterOrders;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.models.Orders;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyOrders extends AppCompatActivity {

    LinearLayout mainContent;
    public static RecyclerView recyclerView, recyclerViewPast;
    LinearLayout noOrders;
    List<Orders> listOrders;
    List<Orders> listOrdersPast;
    Context ctx;
    SharedPreferences preferences;
    AdapterOrders adapterOrders;
    public static Parcelable recylcerViewState;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        ctx = MyOrders.this;
        declareComponents();
    }

    void declareComponents() {
        preferences = ctx.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        noOrders = findViewById(R.id.no_orders);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        mainContent = findViewById(R.id.main_content);
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, LoginActivity.class));
            }
        });
        findViewById(R.id.browse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, MasterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void checkView() {
        if (preferences.getInt(Constants.LOGIN_FLAG,0) == 1){
            login.setVisibility(View.GONE);
            getOrders();
        }
        else {
            login.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        isOnline();
    }

    @Override
    protected void onPause() {
        super.onPause();
        recylcerViewState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
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
            checkView();
        }
        else {
            dialogInternet.dismiss();
            dialogInternet.show();
        }
    }


    void getOrders(){
        listOrders = new ArrayList<>();
        listOrdersPast = new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                Constants.FETCH_ORDERS + preferences.getString(Constants.USER_ID,null), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("text");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        Orders pojoOrders = new Orders (
                                o.getString("order_id"),
                                o.getString("order_status"),
                                o.getString("product_name"),
                                o.getString("ordered_on"),
                                o.getString("order_total"),
                                o.getString("order_message")
                        );
                        listOrders.add(pojoOrders);

                    }
                    if (listOrders.size() == 0){
                        noOrders.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    else {
                        noOrders.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapterOrders = new AdapterOrders(listOrders, ctx, "ORDERS");
                        recyclerView.setAdapter(adapterOrders);
                        if (recylcerViewState != null){
                            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(recylcerViewState);
                        }
                        adapterOrders.notifyDataSetChanged();
                    }
                }catch (JSONException ignored){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
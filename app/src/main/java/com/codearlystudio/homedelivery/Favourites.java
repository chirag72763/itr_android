package com.codearlystudio.homedelivery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codearlystudio.homedelivery.adapters.AdapterFavourites;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.models.PojoProducts;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Favourites extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    static String url = "";
    @SuppressLint("StaticFieldLeak")
    public static Context ctx;
    static List<PojoProducts> listProducts;
    @SuppressLint("StaticFieldLeak")
    static AdapterFavourites adapterFavourites;
    public static RecyclerView recyclerView;
    public static Parcelable recylcerViewState;
    public static SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout coordinatorLayout;
    @SuppressLint("StaticFieldLeak")
    static LinearLayout noOrders;
    @SuppressLint("StaticFieldLeak")
    static TextView textCount;
    static FloatingActionButton fabCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        coordinatorLayout = findViewById(R.id.main_content);
        ctx = this;
        declareComponents();
    }

    @SuppressLint("CommitPrefEdits")
    void declareComponents(){
        noOrders = findViewById(R.id.no_orders);
        findViewById(R.id.browse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, MasterActivity.class));
                finish();
            }
        });
        fabCart = findViewById(R.id.fab_cart);
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, Cart.class);
                intent.putExtra(Constants.INTENT_SOURCE, "Favourites");
                startActivity(intent); }});
        textCount = findViewById(R.id.text_count);
        preferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        editor = preferences.edit();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    public static void getProducts(){
        listProducts = new ArrayList<>();
        url = Constants.FAVOURITES_FETCH_URL + preferences.getString(Constants.USER_ID,null);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("text");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        PojoProducts pojoProducts = new PojoProducts(
                                o.getString("product_id"),
                                o.getString("product_name"),
                                o.getString("product_tags"),
                                o.getString("product_image"),
                                o.getString("product_description"),
                                o.getString("product_available"),
                                o.getString("product_cart_count"),
                                o.getString("product_saved"),
                                o.getJSONArray("product_variant")
                        );
                        listProducts.add(pojoProducts);
                    }
                    if (listProducts.size() == 0){
                        recyclerView.setVisibility(View.GONE);
                        noOrders.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        noOrders.setVisibility(View.GONE);
                        adapterFavourites = new AdapterFavourites(listProducts, ctx);
                        recyclerView.setAdapter(adapterFavourites);
                        if (recylcerViewState != null){
                            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(recylcerViewState);
                        }
                        adapterFavourites.notifyDataSetChanged();
                    }
                    if (preferences.getInt(Constants.LOGIN_FLAG,0) == 1){
                        getCartCount();
                    }
                }catch (JSONException ignored){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    public static void getCartCount(){
        String cartUrl = Constants.CART_COUNTER_URL + preferences.getString(Constants.USER_ID,null);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, cartUrl, new Response.Listener<String>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(String response) {
                if (!response.equals("0")){
                    textCount.setVisibility(View.VISIBLE);
                    textCount.setText(response);
                    fabCart.setVisibility(View.VISIBLE);
                }
                else {
                    textCount.setVisibility(View.GONE);
                    fabCart.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnline();
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
            if (listProducts != null) {
                listProducts.clear();
            }
            if (preferences.getInt(Constants.LOGIN_FLAG, 0) == 1){
                getProducts();
            }
            else {
                recyclerView.setVisibility(View.GONE);
                noOrders.setVisibility(View.VISIBLE);
            }
        }
        else {
            dialogInternet.dismiss();
            dialogInternet.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        recylcerViewState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
package com.codearlystudio.homedelivery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codearlystudio.homedelivery.adapters.AdapterAddress;
import com.codearlystudio.homedelivery.adapters.AdapterCart;
import com.codearlystudio.homedelivery.adapters.AdapterCartFavourites;
import com.codearlystudio.homedelivery.adapters.AdapterTimeSlot;
import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.codearlystudio.homedelivery.models.CartItems;
import com.codearlystudio.homedelivery.models.PojoAddress;
import com.codearlystudio.homedelivery.models.PojoProducts;
import com.codearlystudio.homedelivery.models.PojoTimeSlot;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;

public class Cart extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak") static Context ctx;
    @SuppressLint("StaticFieldLeak") static AdapterCart adapterCart;
    @SuppressLint("StaticFieldLeak") static ImageView removeAll;
    @SuppressLint("StaticFieldLeak") static LinearLayout noItem, favouritesLayout;
    @SuppressLint("StaticFieldLeak") public static TextView cartTitle, checkoutText1, checkoutText2;
    @SuppressLint("StaticFieldLeak") public static AdapterAddress adapterAddress;
    @SuppressLint("StaticFieldLeak") public static AdapterTimeSlot adapterTimeSlot;
    @SuppressLint("StaticFieldLeak")  static Button login;
    @SuppressLint("StaticFieldLeak") static AdapterCartFavourites adapterFavourites;
    @SuppressLint("StaticFieldLeak") static Button btnContinue;
    @SuppressLint("StaticFieldLeak") static LinearLayout seeAll;
    public static List<CartItems> cartItems;
    static SharedPreferences preferences;
    SharedPreferences.Editor editor;
    static float itemTotal = 0;
    static CardView addressButton;
    public static Dialog dialog, dialogTime;
    public static RecyclerView recyclerView, recyclerViewAddress, recyclerViewToday, recyclerViewTomorrow;
    public static List<PojoAddress> listAddress;
    public static List<PojoTimeSlot> listTimeSlot;
    static float delivery_fee = 0.0f;
    static List<PojoProducts> listFavourites;
    public static RecyclerView recyclerViewFavourites;
    public static Parcelable recylcerViewFavouritesState;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        ctx = Cart.this;
        declareComponents();
    }

    @SuppressLint("CommitPrefEdits")
    void declareComponents(){
        intent = getIntent();
        seeAll = findViewById(R.id.see_all);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(ctx, Favourites.class));
            }
        });
        recyclerViewFavourites = findViewById(R.id.recyclerViewFavourites);
        recyclerViewFavourites.setHasFixedSize(true);
        recyclerViewFavourites.setNestedScrollingEnabled(true);
        recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
        favouritesLayout = findViewById(R.id.favourites_layout);
        cartTitle = findViewById(R.id.cart_title);
        checkoutText1 = findViewById(R.id.checkoutText1);
        checkoutText2 = findViewById(R.id.checkoutText2);
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
        addressButton = findViewById(R.id.address_button);
        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressButton.setClickable(false);
                addressButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addressButton.setClickable(true);
                    }
                }, 500);
                initializeAddressDialog();
            }
        });
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, LoginActivity.class));
            }
        });
        preferences = ctx.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
        noItem = findViewById(R.id.no_items);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        removeAll = findViewById(R.id.remove_all);
        removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllFromCart();
            }
        });
    }

    void checkView() {
        if (preferences.getInt(Constants.LOGIN_FLAG,0) == 1){
            login.setVisibility(View.GONE);
            getFavourites();
        }
        else {
            cartTitle.setText(Constants.NO_ITEMS_IN_CART);
            login.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            removeAll.setVisibility(View.GONE);
        }
    }

    public static void getProducts(){
        itemTotal = 0.0f;
        cartItems = new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                Constants.CART_URL+preferences.getString(Constants.USER_ID,null), new Response.Listener<String>() {
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
                        cartItems.add(pojoItems);
                        itemTotal = itemTotal + Float.parseFloat(pojoItems.getTotalProductPrice());
                    }
                    String fee = jsonObject.getString("delivery_fee"); //Fetching delivery fee from server
                    delivery_fee = Float.parseFloat(fee);
                    StringBuilder message = new StringBuilder();
                    JSONArray jsonArray1 = jsonObject.getJSONArray("message_array"); //checking if any price has changed for items. Fetching message array from server
                    if (jsonArray1.length() != 0){
                        for (int j = 0; j< jsonArray1.length(); j++){
                            message.append(jsonArray1.getString(j)).append("\n\n");
                        }
                        showPricingChangeAlert(message.toString().trim());
                    }
                    changeView();
                }catch (JSONException ignored){ }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    static void changeView(){
        if (cartItems.size() == 0){
            noItem.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            addressButton.setVisibility(View.GONE);
            cartTitle.setText(Constants.NO_ITEMS_IN_CART);
            removeAll.setVisibility(View.GONE);
        }
        else {
            removeAll.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            noItem.setVisibility(View.GONE);
            addressButton.setVisibility(View.VISIBLE);
            adapterCart = new AdapterCart(cartItems, ctx);
            recyclerView.setAdapter(adapterCart);
            adapterCart.notifyDataSetChanged();
            if (itemTotal == 0.0f) {
                addressButton.setVisibility(View.GONE);
                cartTitle.setText(Constants.NO_ITEMS_IN_CART);
            }
            else {
                addressButton.setVisibility(View.VISIBLE);
                checkoutText1.setText(cartItems.size() + " item");
                cartTitle.setText("Cart ("+cartItems.size()+" item)");
                checkoutText2.setText("₹ " + itemTotal);
            }
        }

    }

    static void showPricingChangeAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(Constants.PRICING_CHANGE_TITLE);
        builder.setMessage(message);
        builder.setPositiveButton(Constants.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    void removeAllFromCart(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Empty Cart");
        builder.setMessage("All the items from cart will be removed.");
        builder.setPositiveButton("Empty Cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Api getResponse = AppConfig.getRetrofit().create(Api.class);
                Call<ServerResponse> call = getResponse.removeAllFromCart(preferences.getString(Constants.USER_ID, null));
                call.enqueue(new retrofit2.Callback<ServerResponse>() {
                    @SuppressLint("Assert")
                    @Override
                    public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                        ServerResponse serverResponse = response.body();
                        if (serverResponse != null) {
                            if (serverResponse.getSuccess()) {
                                getFavourites();
                                recylcerViewFavouritesState = null;
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {

                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void initializeAddressDialog() {
        dialog = new BottomSheetDialog(ctx);
        dialog.setContentView(R.layout.address_layout);
        recyclerViewAddress = dialog.findViewById(R.id.recyclerViewAddress);
        recyclerViewAddress.setHasFixedSize(true);
        recyclerViewAddress.setNestedScrollingEnabled(true);
        recyclerViewAddress.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerViewAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        }) ;
        dialog.findViewById(R.id.add_new_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, Address.class);
                intent.putExtra("FROM", "Cart");
                ctx.startActivity(intent);
            }
        });
        btnContinue = dialog.findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeTimeDialog();
            }
        });
        dialog.show();
        showAddresses();
    }

    public static void showAddresses(){
        listAddress = new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                Constants.FETCH_ADDRESSES + preferences.getString(Constants.USER_ID,null), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("text");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        PojoAddress pojoAddress = new PojoAddress(
                                o.getString("address_id"),
                                o.getString("address_name"),
                                o.getString("address_phone"),
                                o.getString("address_pincode"),
                                o.getString("address"),
                                o.getString("address_landmark"),
                                o.getString("address_city"),
                                o.getString("address_state"),
                                o.getString("address_email"),
                                o.getString("address_default")
                        );
                        listAddress.add(pojoAddress);
                    }
                    btnContinue.setVisibility((listAddress.size() == 0) ? View.GONE : View.VISIBLE);
                    adapterAddress = new AdapterAddress(listAddress, ctx);
                    recyclerViewAddress.setAdapter(adapterAddress);
                    adapterAddress.notifyDataSetChanged();
                }catch (JSONException ignored){ }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void initializeTimeDialog() {
        dialogTime = new BottomSheetDialog(ctx);
        dialogTime.setContentView(R.layout.time_date_select);
        recyclerViewToday = dialogTime.findViewById(R.id.recyclerViewToday);
        recyclerViewToday.setHasFixedSize(true);
        recyclerViewToday.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerViewTomorrow = dialogTime.findViewById(R.id.recyclerViewTomorrow);
        recyclerViewTomorrow.setHasFixedSize(true);
        recyclerViewTomorrow.setLayoutManager(new LinearLayoutManager(ctx));
        dialogTime.show();
        showTime();
    }

    public static void showTime() {
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                Constants.FETCH_TIME_SLOTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    listTimeSlot = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("date_time_today");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        PojoTimeSlot pojoTimeSlot = new PojoTimeSlot(
                                o.getString("date_slot"),
                                o.getString("time_slot")
                        );
                        listTimeSlot.add(pojoTimeSlot);
                    }
                    adapterTimeSlot = new AdapterTimeSlot(listTimeSlot, ctx, true);
                    recyclerViewToday.setAdapter(adapterTimeSlot);
                    adapterTimeSlot.notifyDataSetChanged();
                    listTimeSlot = new ArrayList<>();
                    JSONArray jsonArray1 = jsonObject.getJSONArray("date_time_tomorrow");
                    for (int i = 0; i < jsonArray1.length(); i++){
                        JSONObject o1 = jsonArray1.getJSONObject(i);
                        PojoTimeSlot pojoTimeSlot = new PojoTimeSlot(
                                o1.getString("date_slot"),
                                o1.getString("time_slot")
                        );
                        listTimeSlot.add(pojoTimeSlot);
                    }
                    adapterTimeSlot = new AdapterTimeSlot(listTimeSlot, ctx, true);
                    recyclerViewTomorrow.setAdapter(adapterTimeSlot);
                    adapterTimeSlot.notifyDataSetChanged();
                }catch (JSONException ignored){ }
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
    public void onResume() {
        super.onResume();
        isOnline();
    }

    public static void getFavourites(){
        listFavourites = new ArrayList<>();
        String url = Constants.FAVOURITES_FETCH_URL + preferences.getString(Constants.USER_ID,null);
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
                        listFavourites.add(pojoProducts);
                    }
                    if (listFavourites.size() == 0){
                        favouritesLayout.setVisibility(View.GONE);
                    }
                    else {
                        favouritesLayout.setVisibility(View.VISIBLE);
                        adapterFavourites = new AdapterCartFavourites(listFavourites, ctx);
                        recyclerViewFavourites.setAdapter(adapterFavourites);
                        if (recylcerViewFavouritesState != null){
                            Objects.requireNonNull(recyclerViewFavourites.getLayoutManager()).onRestoreInstanceState(recylcerViewFavouritesState);
                        }
                        adapterFavourites.notifyDataSetChanged();
                    }
                    getProducts();
                }catch (JSONException ignored){ }
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
        if (intent != null) {
            if (Objects.requireNonNull(intent.getStringExtra(Constants.INTENT_SOURCE)).equals("FCM")){
                Intent intent = new Intent(ctx, MasterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            else {
                super.onBackPressed();
            }
        }
        else {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        recylcerViewFavouritesState = Objects.requireNonNull(recyclerViewFavourites.getLayoutManager()).onSaveInstanceState();
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

}
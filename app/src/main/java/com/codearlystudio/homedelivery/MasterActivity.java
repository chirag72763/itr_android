package com.codearlystudio.homedelivery;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codearlystudio.homedelivery.adapters.AdapterAddressSaved;
import com.codearlystudio.homedelivery.adapters.AdapterCategories;
import com.codearlystudio.homedelivery.adapters.AdapterSearch;
import com.codearlystudio.homedelivery.adapters.AdapterTimeSlot;
import com.codearlystudio.homedelivery.adapters.RecyclerViewAdapter;
import com.codearlystudio.homedelivery.data.Api;
import com.codearlystudio.homedelivery.data.AppConfig;
import com.codearlystudio.homedelivery.data.Constants;
import com.codearlystudio.homedelivery.data.ServerResponse;
import com.codearlystudio.homedelivery.models.PojoAddress;
import com.codearlystudio.homedelivery.models.PojoSearch;
import com.codearlystudio.homedelivery.models.PojoTimeSlot;
import com.codearlystudio.homedelivery.models.SectionCategories;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressLint("StaticFieldLeak")
public class MasterActivity extends AppCompatActivity {

    static Parcelable recylcerViewState;
    static List<SectionCategories> sectionCategoriesList;
    static List<PojoSearch> pojoSearches;
    public static List<PojoTimeSlot> listNextSlot;
    public static List<PojoTimeSlot> listTimeSlot;
    public static List<PojoAddress> listAddress;
    static RelativeLayout linearWhatsapp, orderTrackLayout;
    static SharedPreferences preferences; static SharedPreferences.Editor editor;
    public static Context ctx;
    static AdapterSearch adapterSearch;
    public static AdapterTimeSlot adapterTimeSlot;
    @SuppressLint("Range")
    @RequiresApi(api = Build.VERSION_CODES.M)
    ImageView searchButton;
    static BottomSheetDialog dialog;
    public static RecyclerView recyclerView, recyclerViewSearch, recyclerViewPopular, recyclerViewAddress, recyclerViewToday, recyclerViewTomorrow;
    static TextView noSearch, textViewCartBadge, textViewOrderBadge, userName, userPhone, totalCart, date, month, day1, day2, time, orderNumberText;
    static AdapterCategories adapterCategories;
    static AdapterAddressSaved adapterAddress;
    static LinearLayout noSearchItemLayout, suggestionsLayout, linearTime;
    public static EditText etSearch;
    static RecyclerViewAdapter recyclerViewAdapter;
    static DrawerLayout drawerLayout;
    CardView loginCard, logoutCard;
    EditText searchBox;
    static Dialog dialogTime;
    static FloatingActionButton fabCart;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        ctx = MasterActivity.this;
        preferences = Objects.requireNonNull(ctx).getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        editor = preferences.edit();
        declareComponents();
        isOnline();
        checkLogin();
    }

    @Override
    public void onPause() {
        super.onPause();
        recylcerViewState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLogin();
    }

    @SuppressLint("NewApi")
    void declareComponents(){
        orderNumberText = findViewById(R.id.order_number);
        orderTrackLayout = findViewById(R.id.order_track_layout);
        date = findViewById(R.id.date);
        month = findViewById(R.id.month);
        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        time = findViewById(R.id.time);
        linearTime = findViewById(R.id.linear_time);
        userName = findViewById(R.id.user_name);
        userPhone = findViewById(R.id.user_phone);
        loginCard = findViewById(R.id.login_card);
        logoutCard = findViewById(R.id.logout_card);
        textViewCartBadge = findViewById(R.id.text_count);
        textViewOrderBadge = findViewById(R.id.total_orders);
        totalCart = findViewById(R.id.total_cart);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        searchButton = findViewById(R.id.search_button);
        linearTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearTime.setClickable(false);
                linearTime.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linearTime.setClickable(true);
                    }
                }, 500);
                initializeTimeDialog();
            }
        });
        linearWhatsapp = findViewById(R.id.linear_whatsapp);
        orderTrackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(ctx, MyOrders.class)); }});
        loginCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(ctx, LoginActivity.class)); }});
        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { logout(); }});
        searchBox = findViewById(R.id.search_box);
        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setClickable(false);
                searchBox.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchBox.setClickable(true);
                    }
                }, 500);
                showSearchDialog();
            }
        });
        findViewById(R.id.open_drawer_navi).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) { drawerLayout.openDrawer(Gravity.LEFT); }});
        fabCart = findViewById(R.id.fab_cart);
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Intent intent = new Intent(ctx, Cart.class);
                intent.putExtra(Constants.INTENT_SOURCE, "Master");
                startActivity(intent); }
        });
        linearWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();whatsappActivity();
            }
        });
        findViewById(R.id.relative_home).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) { drawerLayout.closeDrawers(); }});
        findViewById(R.id.relative_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { drawerLayout.closeDrawers();Intent intent = new Intent(ctx, Cart.class);
                intent.putExtra(Constants.INTENT_SOURCE, "Master");
                startActivity(intent); }});
        findViewById(R.id.relative_orders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { drawerLayout.closeDrawers();startActivity(new Intent(ctx, MyOrders.class)); }});
        findViewById(R.id.relative_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                if (preferences.getInt(Constants.LOGIN_FLAG,0) != 0){
                    initializeAddressDialog();
                }
                else {
                    startActivity(new Intent(ctx, LoginActivity.class));
                }
            }});
        findViewById(R.id.relative_favourites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(ctx, Favourites.class)); }});
        findViewById(R.id.relative_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { drawerLayout.closeDrawers();showContactUsSection(); }});
        findViewById(R.id.relative_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { drawerLayout.closeDrawers();aboutUs(); }});
    }

    void checkLogin(){
        if (preferences.getInt(Constants.LOGIN_FLAG,0) == 1){
            logoutCard.setVisibility(View.VISIBLE);
            loginCard.setVisibility(View.GONE);
            userName.setText(preferences.getString(Constants.USER_NAME, null));
            userPhone.setText(preferences.getString(Constants.USER_PHONE, null));
            getCartCount();
            getOrderCount();
        }
        else {
            logoutCard.setVisibility(View.GONE);
            loginCard.setVisibility(View.VISIBLE);
            textViewCartBadge.setVisibility(View.GONE);
            totalCart.setVisibility(View.GONE);
            textViewOrderBadge.setVisibility(View.GONE);
            orderTrackLayout.setVisibility(View.GONE);
        }
        showTime(false);
        textViewCartBadge.setVisibility(View.GONE);
        textViewOrderBadge.setVisibility(View.GONE);
    }

    void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("LOGOUT");
        builder.setMessage("Do you want to logout ?");
        builder.setPositiveButton(Constants.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateFcmTokenOnServer();
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

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }



    void updateFcmTokenOnServer() {
        Api getResponse = AppConfig.getRetrofit().create(Api.class);
        Call<ServerResponse> call = getResponse.updateFcmTokenOnServer(preferences.getString(Constants.USER_ID, null));
        call.enqueue(new retrofit2.Callback<ServerResponse>() {
            @SuppressLint("Assert")
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        editor.putInt(Constants.LOGIN_FLAG, 0);
                        editor.putString(Constants.USER_ID, "0");
                        editor.commit();
                        checkLogin();
                        getSectionsCategories();
                    }
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) { }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void initializeAddressDialog() {
        dialog = new BottomSheetDialog(ctx);
        dialog.setContentView(R.layout.address_layout);
        TextView changeDeliveryAddress = dialog.findViewById(R.id.change_delivery_address);
        changeDeliveryAddress.setText(Constants.CHOOSE_DEFAULT_ADDRESS_TEXT);
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
                intent.putExtra("FROM", "Account");
                ctx.startActivity(intent);
            }
        });
        Button btnContinue = dialog.findViewById(R.id.btn_continue);
        btnContinue.setVisibility(View.GONE);
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
                    adapterAddress = new AdapterAddressSaved(listAddress, ctx);
                    recyclerViewAddress.setAdapter(adapterAddress);
                    adapterAddress.notifyDataSetChanged();
                }catch (JSONException ignored){ }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    void aboutUs(){
        dialog = new BottomSheetDialog(ctx);
        dialog.setContentView(R.layout.about_us);
        dialog.show();
    }

    void showContactUsSection(){
        dialog = new BottomSheetDialog(ctx);
        dialog.setContentView(R.layout.contact_us);
        dialog.findViewById(R.id.call_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse(Constants.TELEPHONE_NUMBER));
                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MasterActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 741);
                }
                else { startActivity(phoneIntent); }
            }
        });
        dialog.findViewById(R.id.mail_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse(Constants.MAIL_ADDRESS);
                intent.setData(data);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    void whatsappActivity(){
        String contact = Constants.WHATSAPP_DEFAULT_NUMBER;
        PackageManager packageManager = ctx.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = Constants.WHATSAPP_API_URL + contact +"&text=" + URLEncoder.encode("Hi", "UTF-8");
            i.setPackage(Constants.WHATSAPP_PACKAGE_NAME);
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                ctx.startActivity(i);
            }
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(ctx, Constants.WHATSAPP_NOT_INSTALLED, Toast.LENGTH_SHORT).show();
        }

    }

    static void showSearchDialog() {
        dialog = new BottomSheetDialog(ctx);
        dialog.setContentView(R.layout.search_layout);
        suggestionsLayout = dialog.findViewById(R.id.suggestions);
        noSearch = dialog.findViewById(R.id.no_search);
        noSearchItemLayout =  dialog.findViewById(R.id.no_search_items);
        recyclerViewPopular = dialog.findViewById(R.id.popular_searches);
        recyclerViewPopular.setHasFixedSize(true);
        recyclerViewPopular.setLayoutManager(new StaggeredGridLayoutManager(5,StaggeredGridLayoutManager.HORIZONTAL));
        recyclerViewAdapter = new RecyclerViewAdapter(ctx, Constants.searches);
        recyclerViewPopular.setAdapter(recyclerViewAdapter);
        recyclerViewSearch = dialog.findViewById(R.id.recyclerViewSearch);
        recyclerViewSearch.setHasFixedSize(true);
        recyclerViewSearch.setNestedScrollingEnabled(true);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(ctx));
        etSearch = dialog.findViewById(R.id.et_search);
        dialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.remove_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });
        dialog.show();
        final Timer[] timer = {new Timer()};
        final long DELAY = 500;
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(final Editable s) {
                timer[0].cancel();
                timer[0] = new Timer();
                timer[0].schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                fetchProductNames(s.toString());
                            }
                        },
                        DELAY
                );
            }
        });
    }

    static void fetchProductNames(final String key) {
        pojoSearches  = new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.GET, Constants.FETCH_PRODUCT_NAMES + key, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("text");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        PojoSearch pojoSearch = new PojoSearch(
                                o.getString("productId"),
                                o.getString("productName"),
                                o.getString("productTags"),
                                o.getString("productImage"),
                                o.getString("sectionName")
                        );
                        pojoSearches.add(pojoSearch);
                    }
                    if (pojoSearches.size() == 0) {
                        noSearchItemLayout.setVisibility(View.VISIBLE);
                        noSearch.setText(Constants.NO_SEARCH_RESULT + key +"'");
                        recyclerViewSearch.setVisibility(View.GONE);
                        suggestionsLayout.setVisibility(View.VISIBLE);
                    }
                    else {
                        if (key.equals("")){
                            suggestionsLayout.setVisibility(View.VISIBLE);
                            recyclerViewSearch.setVisibility(View.GONE);
                        }
                        else {
                            recyclerViewSearch.setVisibility(View.VISIBLE);
                            noSearchItemLayout.setVisibility(View.GONE);
                            adapterSearch = new AdapterSearch(pojoSearches, ctx);
                            recyclerViewSearch.setAdapter(adapterSearch);
                            adapterSearch.notifyDataSetChanged();
                            suggestionsLayout.setVisibility(View.GONE);
                        }
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

    public static void getSectionsCategories(){
        sectionCategoriesList  = new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                Constants.SECTIONS_FETCH_URL + preferences.getString(Constants.USER_ID, null), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("text");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        SectionCategories sectionCategories = new SectionCategories(
                                o.getString("section_category_name"),
                                o.getString("section_description"),
                                o.getString("section_category"),
                                o.getString("section_banner_title"),
                                o.getString("section_banner_subtitle"),
                                o.getString("section_banner_message"),
                                o.getString("section_banner_image"),
                                o.getJSONArray("section_items"),
                                o.getJSONArray("section_exclusive")
                        );
                        sectionCategoriesList.add(sectionCategories);
                    }
                    adapterCategories = new AdapterCategories(sectionCategoriesList, ctx);
                    recyclerView.setAdapter(adapterCategories);
                    if (recylcerViewState != null){
                        Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(recylcerViewState);
                    }
                    adapterCategories.notifyDataSetChanged();
                    linearWhatsapp.setVisibility(View.VISIBLE);
                    linearTime.setVisibility(View.VISIBLE);
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
                    textViewCartBadge.setVisibility(View.VISIBLE);
                    textViewCartBadge.setText(response);
                    totalCart.setVisibility(View.VISIBLE);
                    totalCart.setText(response);
                    fabCart.setVisibility(View.VISIBLE);
                }
                else {
                    textViewCartBadge.setVisibility(View.GONE);
                    totalCart.setVisibility(View.GONE);
                    fabCart.setVisibility(View.GONE);
                }
                getOrderCount();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getOrderCount();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    public static void getOrderCount() {
        String cartUrl = Constants.ORDER_COUNTER_URL + preferences.getString(Constants.USER_ID, null);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, cartUrl, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                if (!response.equals("0")){
                    textViewOrderBadge.setVisibility(View.VISIBLE);
                    textViewOrderBadge.setText(response);
                    orderTrackLayout.setVisibility(View.VISIBLE);
                    orderNumberText.setText(response + Constants.ACTIVE_ORDERS_TEXT);
                }
                else {
                    textViewOrderBadge.setVisibility(View.GONE);
                    orderTrackLayout.setVisibility(View.GONE);
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
        showTime(true);
    }

    public static void showTime(final boolean showTimeList) {
        StringRequest stringRequest=new StringRequest(Request.Method.GET, Constants.FETCH_TIME_SLOTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    listTimeSlot = new ArrayList<>();
                    listNextSlot = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    if (showTimeList) { showTimeList(jsonObject); } else { showNextSlot(jsonObject); }
                }catch (JSONException ignored){ }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }});
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    static void showTimeList(JSONObject jsonObject) {
        try{
            JSONArray jsonArray = jsonObject.getJSONArray("date_time_today");
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject o = jsonArray.getJSONObject(i);
                PojoTimeSlot pojoTimeSlot = new PojoTimeSlot(
                        o.getString("date_slot"),
                        o.getString("time_slot")
                );
                listTimeSlot.add(pojoTimeSlot);
            }
            adapterTimeSlot = new AdapterTimeSlot(listTimeSlot, ctx, false);
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
            adapterTimeSlot = new AdapterTimeSlot(listTimeSlot, ctx, false);
            recyclerViewTomorrow.setAdapter(adapterTimeSlot);
            adapterTimeSlot.notifyDataSetChanged();
        }
        catch (JSONException ignored){ }
    }

    @SuppressLint("SetTextI18n")
    static void showNextSlot(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("date_time_today");
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject o = jsonArray.getJSONObject(i);
                PojoTimeSlot pojoTimeSlot = new PojoTimeSlot(
                        o.getString("date_slot"),
                        o.getString("time_slot")
                );
                listNextSlot.add(pojoTimeSlot);
            }
            Calendar calendar = Calendar.getInstance();
            if (listNextSlot.size() != 0){
                String[] separated = listNextSlot.get(0).getDateSlot().split(" ");
                date.setText(separated[0]);
                month.setText(separated[1]);
                day1.setText(Constants.TODAY);
                Date date = calendar.getTime();
                day2.setText("(" +new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime()) +")");
                time.setText(Constants.NEXT_AVAILABLE_SLOT + listNextSlot.get(0).getTimeSlot());
            }
            else {
                listNextSlot = new ArrayList<>();
                JSONArray jsonArray1 = jsonObject.getJSONArray("date_time_tomorrow");
                for (int i = 0; i < jsonArray1.length(); i++){
                    JSONObject o1 = jsonArray1.getJSONObject(i);
                    PojoTimeSlot pojoTimeSlot = new PojoTimeSlot(
                            o1.getString("date_slot"),
                            o1.getString("time_slot")
                    );
                    listNextSlot.add(pojoTimeSlot);
                }
                String[] separated = listNextSlot.get(0).getDateSlot().split(" ");
                date.setText(separated[0]);
                month.setText(separated[1]);
                day1.setText(Constants.TOMORROW);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date date = calendar.getTime();
                day2.setText("(" +new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime()) +")");
                time.setText(Constants.NEXT_AVAILABLE_SLOT + listNextSlot.get(0).getTimeSlot());
            }
        }
        catch (JSONException ignored){ }
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
            getSectionsCategories();
            checkLogin();
        }
        else {
            dialogInternet.dismiss();
            dialogInternet.show();
        }
    }

}